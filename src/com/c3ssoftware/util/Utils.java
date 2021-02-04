package com.c3ssoftware.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import com.c3ssoftware.Peer;
import com.c3ssoftware.model.Message;
import com.c3ssoftware.process.Process;
//import com.c3ssoftware.Peer;
//import com.c3ssoftware.process.Process;
import com.c3ssoftware.process.Status;

public class Utils {

	/**
	 * Utility function to wrap-up message with the process and it's status
	 * 
	 * @param peer
	 * @param processStatus
	 * @return Message object holds all peer specification alongside with it's
	 *         status and descriptive message
	 */
	public static Message encodeMessage(Peer peer, Status processStatus) {
		return encodeMessage(peer, processStatus, " ", 0);
	}

	/**
	 * Utility function to wrap-up message with the process and it's status
	 * 
	 * @param peer
	 * @param processStatus
	 * @param body
	 * @return Message object holds all peer specification alongside with it's
	 *         status and descriptive message
	 */
	public static Message encodeMessage(Peer peer, Status processStatus, String body, Integer minValue) {
		switch (processStatus) {
		case ADD_PEER:
			return new Message(getNowTimeStamp(), Constants.HOST, peer.getPort(), processStatus, body);
		case OK:
			return new Message(getNowTimeStamp(), Constants.HOST, peer.getPort(), processStatus, minValue);
		case NEW:
			return new Message(getNowTimeStamp(), Constants.HOST, peer.getPort(), processStatus);
		default:
			return null;
		}
	}

	/**
	 * Utility function to return the current timeStamp
	 * 
	 * @return Current Date
	 */
	public static long getNowTimeStamp() {
		return new Timestamp(System.currentTimeMillis()).getTime();
	}

	/**
	 * Utility function to retrieve peer from peer list by index
	 * 
	 * @param peers
	 * @param indx
	 * @return
	 */
	public static Peer getPeerByIndex(List<Peer> peers, int indx) {
		for (int i = 0; i < peers.size(); ++i) {
			if (i == indx) {
				return peers.get(i);
			}
		}
		return null;
	}

	/**
	 * Utility function to encode Peer list by separating them with space
	 * 
	 * @param peer
	 * @return
	 */
	public static String encodePeers(Peer peer, List<Peer> peers) {
		String encodedPeers = peer.getPort() + "";
		for (Peer p : peers) {
			encodedPeers += (" " + p.getPort());
		}
		return encodedPeers;
	}

	/**
	 * Utility function that split new updated peers port and create list of peers
	 * 
	 * @param body
	 * @return
	 */
	public static List<Peer> peersDecoder(String body) {
		List<Peer> updatedPeers = new ArrayList<>();
		for (String peer : body.split(" ")) {
			updatedPeers.add(new Peer(Integer.parseInt(peer)));
		}
		return updatedPeers;
	}

	/**
	 * Utility function Generate random integer numbers
	 * 
	 * @return
	 */
	public static int numberGenerator() {
		int num;
		Random r = new Random();
		num = r.nextInt() & Integer.MAX_VALUE;
		return num;
	}

	/**
	 * Utility function to identify and retrieve minimum value in list of values;
	 * 
	 * @param taskList
	 * @return
	 */
	public static int min(List<Integer> taskList) {
		return taskList.stream().mapToInt(x -> x).min().orElseThrow(NoSuchElementException::new);
	}

	/**
	 * Utility function to create new peer object
	 * 
	 * @param peers
	 * @return
	 */
	public static Peer newPeer(List<Peer> peers) {
		if (peers.size() == 0) {
			return new Peer(Process.COORDINATOR_DEFAULT + 1);
		} else {
			int sz = peers.size();
			int last = peers.get(sz - 1).getPort();

			return new Peer(last + 1);
		}

	}

	/**
	 * Utility function to generate list of integers
	 * 
	 * @return
	 */
	public static List<Integer> listGenerator() {
		List<Integer> taskList = new ArrayList<>();
		for (int i = 0; i < 50; i++)
			taskList.add(Utils.numberGenerator());

		return taskList;
	}
}
