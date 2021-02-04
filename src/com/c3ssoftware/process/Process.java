package com.c3ssoftware.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.c3ssoftware.Peer;
import com.c3ssoftware.model.Message;
import com.c3ssoftware.util.Constants;
import com.c3ssoftware.util.Partition;
import com.c3ssoftware.util.Utils;

public class Process {
	public Peer myPeer = null;
	private List<Peer> peers = new ArrayList<>();
	public static final int COORDINATOR_DEFAULT = 9090;
	private boolean coordinatorFlag = false;

	public Process() {
		// Assign default port to the peer initially
		myPeer = new Peer(COORDINATOR_DEFAULT);
		myPeer.setProcess(this);
	}

	public void run() {
		// If there's coordinator open socket and start conversation
		// Else set the highest peer as coordinator and open socket and start
		// conversation
		if (this.checkCoordinatorExistance() != null) {
			myPeer.Listen();
		} else {
			System.out.println("Can't conenct to coordinator");
			setCoordinator(true);
			myPeer.Listen();
		}
	}

	/**
	 * Utility function to process the received response depend on the status and
	 * encode it to reply to the received request
	 * 
	 * If NEW status received add new peer and notify others with the new peers list
	 * Else if VICTORY status received remove the peer that won the election and
	 * became coordinator else if
	 * 
	 * @param response
	 * @return
	 */
	public Message ResponseEncoder(String response, Peer myPeer) {
		Message message = new Message(response);
		String messageBody = message.getBody();
		Status processStatus = message.getStatus();
		switch (processStatus) {
		case NEW:
			// If new peer received respond with list of other peers
			// Adding coordinator port and other ports including receiver port
			peers.add(Utils.newPeer(peers));
			notifyWithNewPeer();
			return new Message(Utils.getNowTimeStamp(), Constants.HOST, myPeer.getPort(), Status.LIST,
					Utils.encodePeers(myPeer, peers));
		case VICTORY:
			// Remove the peer that won the election and become COORDINATOR
			removePeer(Integer.parseInt(messageBody));
			myPeer.setActive(true);
			return Utils.encodeMessage(myPeer, Status.OK);
		case ADD_PEER:
			// Add new peer to list of peers
			peers.add(new Peer(Integer.parseInt(message.getBody())));
			return Utils.encodeMessage(myPeer, Status.OK);
		case TASK:
			return Utils.encodeMessage(myPeer, Status.OK, "", Utils.min(message.getTaskList(response)));
		default:
			return Utils.encodeMessage(myPeer, Status.OK);
		}
	}

	/**
	 * Response Decoder utility function to decode the returned response from the
	 * socket
	 * 
	 * @param response
	 */
	public Message ResponseDecoder(String response) {
		// Decode the response using the Message constructor splitter
		Message message = new Message(response);
		String msgBody = message.getBody();
		int senderPort = message.getPort();
		Status processStatus = message.getStatus();

		switch (processStatus) {
		case LIST:
			System.out.println("Received list of peers");
			System.out.println(msgBody);
			updatePeerList(msgBody);
			break;
		case OK:
			System.out.println(message.toString());
			System.out.println("Received okay from port: " + senderPort);
			break;
		default:
			System.out.println("Cannot resolve this response");
			break;
		}

		return message;
	}

	/**
	 * Utility function to update the peer list in response of adding new peer
	 * 
	 * @param body
	 */
	public void updatePeerList(String body) {
		List<Peer> updatedPeers = Utils.peersDecoder(body);
		// Get last peer in the update peers list as it's the coordinator.
		Peer lastPeerInTheList = Utils.getPeerByIndex(updatedPeers, updatedPeers.size() - 1);
		myPeer.setPort(lastPeerInTheList.getPort()); // setting my port as last in list
		updatedPeers.remove(updatedPeers.size() - 1);// remove myself -last-
		setPeers(updatedPeers);
	}

	/**
	 * Notify All the Processes that the Election started If the myPeer port(ID) is
	 * higher than others then it will be the coordinator If there's another process
	 * with port(ID) higher then it will be the coordinator
	 * 
	 */
	synchronized public void electionBroadcast() {
		Message message = new Message(Utils.getNowTimeStamp(), Constants.HOST, myPeer.getPort(), Status.ELECTION);
		message.setBody(myPeer.getPort() + "");
		broadcaster(message, 100);
		boolean isCoordinator = true;
		for (int i = 0; i < peers.size() - 1; ++i) {
			Message msg = myPeer.messageReceiver(200);
			if (msg != null && msg.getStatus() == Status.VICTORY) {
				isCoordinator = false;
			} else if (msg != null && msg.getStatus() == Status.ELECTION) {
				System.out.println("election from " + msg.getBody());
				if (Integer.parseInt(msg.getBody()) < myPeer.getPort()) {
					isCoordinator = false;
				}
			}
		}
		if (isCoordinator) {
			victoryBroadcast();
			taskDispatcher(peers);
		}
		myPeer.Listen();
	}

	/**
	 * Notify All the processes with the elected Peer to be deleted from the peer
	 * list and setting the default port(ID) to the elected peer port(ID)
	 * 
	 */
	void victoryBroadcast() {

		System.out.println("Victory from " + myPeer.getPort());
		int oldPort = myPeer.getPort();
		try {
			myPeer.getServerSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		myPeer.setActive(true);
		setCoordinator(true);
		myPeer.setPort(COORDINATOR_DEFAULT);
		removePeer(COORDINATOR_DEFAULT);/// remove coordinator
		broadcaster(new Message(Utils.getNowTimeStamp(), Constants.HOST, myPeer.getPort(), Status.VICTORY, "" + oldPort),
				Constants.VICTORY_TIMEOUT);
		taskDispatcher(peers);
		myPeer.Listen();
	}

	/**
	 * Notifying other Peers with new peer except the last peer in the list(the
	 * newly added one)
	 *
	 */
	public void notifyWithNewPeer() {
		System.out.println("Notifying others with the new peer");
		for (int i = 0; i < peers.size() - 1; i++) {
			myPeer.messageDispatcher(peers.get(i),
					Utils.encodeMessage(myPeer, Status.ADD_PEER, "" + peers.get(peers.size() - 1).getPort(), 0), 1000);
		}

	}

	/**
	 * Ping the coordinator to check if it's exists or not
	 * 
	 * @return
	 */
	public Message checkCoordinatorExistance() {
		return myPeer.messageDispatcher(myPeer, Utils.encodeMessage(myPeer, Status.NEW), 4000);
	}

	/*
	 * Broadcast messages to other peers
	 * 
	 */
	public void broadcaster(Message message, int timeOut) {
		for (Peer peer : peers) {
			myPeer.messageDispatcher(peer, message, timeOut);
		}
	}

	/**
	 * Send request to the coordinator to check if still Alive or down
	 * 
	 */
	public void sendAlive() {
		broadcaster(new Message(Utils.getNowTimeStamp(), Constants.HOST, myPeer.getPort(), Status.ALIVE),
				Constants.ALIVE_TIME_OUT);
	}

	/**
	 * Remove peer from peer list using the given port
	 * 
	 * @param port
	 */
	synchronized public void removePeer(int port) {
		for (Iterator<Peer> peerIterator = peers.iterator(); peerIterator.hasNext();) {
			Peer peer = peerIterator.next();
			if (peer.getPort() == port) {
				System.out.println("Peer with port: " + port + " removed successfully");
				peerIterator.remove();
			}
		}
	}

	public boolean isCoordinator() {
		return this.coordinatorFlag;
	}

	public void setCoordinator(boolean coordinator) {
		this.coordinatorFlag = coordinator;
	}

	public List<Peer> getPeers() {
		return peers;
	}

	public void setPeers(List<Peer> peers) {
		this.peers = peers;
	}

	public void taskDispatcher(List<Peer> peerList) {
		List<Integer> minValueList = new ArrayList<>();
		List<Integer> taskList = Utils.listGenerator();
		int noOfSubList = 0;
		if (peerList.size() > 0) {
			noOfSubList = taskList.size() / peerList.size();
		}

		List<List<Integer>> result = Partition.ofSize(taskList, noOfSubList);
		for (int i = 0; i < peerList.size(); i++) {
			Message msg = new Message(Utils.getNowTimeStamp(), Constants.HOST, myPeer.getPort(), Status.TASK,
					"" + peerList.get(i).getPort());
			msg.setTaskList(result.get(i));
			Message returnedMsg = peerList.get(i).messageDispatcher(peerList.get(i), msg, 200);
			if (returnedMsg != null) {
				minValueList.add(returnedMsg.getMinValue());
			}

		}

		if (minValueList != null && minValueList.size() > 0) {
			System.out.println("Recieved Task result as following: " + Utils.min(minValueList));
		}
	}

}
