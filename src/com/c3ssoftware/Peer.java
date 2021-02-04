package com.c3ssoftware;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import com.c3ssoftware.model.Message;
import com.c3ssoftware.process.Process;
import com.c3ssoftware.util.Constants;

public class Peer {
	private int port = 8090;
	private ServerSocket serverSocket = null;
	private boolean active = true;
	private List<Integer> taskChunk = null;
	Process process = null;

	public Peer(int port) {
		this.port = port;
	}

	/**
	 * Message Dispatcher function is responsible for dispatching(transmitting)
	 * message to other peers using sockets and peer port
	 * 
	 * @param peer
	 * @param message
	 * @param timeOut
	 * @return
	 */
	public Message messageDispatcher(Peer peer, Message message, int timeOut) {
		try {
			Socket s = new Socket(Constants.HOST, peer.getPort());
			System.out.println("sending " + message.toString() + " to " + peer.getPort());
			s.setSoTimeout(timeOut);
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			DataInputStream din = new DataInputStream(s.getInputStream());
			dout.writeUTF(message.toString());
			String response = din.readUTF(); /// we need to decode response
			Message msg = process.ResponseDecoder(response);
			dout.flush();
			dout.close();
			s.close();
			return msg;
		} catch (Exception e) {
			System.out.println(e + " " + peer.getPort());
		}
		return null;
	}

	/**
	 * Message Receiver function is responsible for receiving the message from other
	 * peers and prepare it to be used
	 * 
	 * @param timeOut
	 * @return
	 */
	public Message messageReceiver(int timeOut) {
		try {
			if (timeOut > 0)
				serverSocket.setSoTimeout(timeOut);
			Socket socket = serverSocket.accept();// establishes connection
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
			String receivedResponse = dataInputStream.readUTF();
			System.out.println("Received message:  " + receivedResponse);
			Message message = process.ResponseEncoder(receivedResponse, process.myPeer);
			String response = message.toString();
			System.out.println("Responded with: " + response);
			dataOutputStream.writeUTF(response);
			dataOutputStream.flush();
			dataOutputStream.close();
			dataInputStream.close();
			return new Message(receivedResponse);
		} catch (Exception e) {
			// If the coordinator is down then start new election process
			if (!process.isCoordinator())
				active = false;

		}
		return null;
	}

	/**
	 * Prepare the process to listen to port using sockets and check coordinator If
	 * coordinator down initiate election to select another one
	 */
	public void Listen() {
		System.out.println("Start listening to port: " + this.getPort());
		while (active) {
			if (process.isCoordinator()) {
				// Use same socket if not already initialized otherwise create one
				if (serverSocket == null || serverSocket.isClosed())
					bindServerSocket();
				messageReceiver(1000); // listen for 1 second and send alive wait indefinitely
				process.sendAlive();
			} else {
				if (serverSocket == null || serverSocket.isClosed())
					bindServerSocket();

				messageReceiver(Constants.NO_RESPONSE_SPAN);
			}
		}
		System.out.println(this.getPort() + " With no coordinator");
		if (!process.isCoordinator())
			process.electionBroadcast();

	}

	private boolean bindServerSocket() {
		System.out.println("Bind to port: " + this.getPort());
		try {
			serverSocket = new ServerSocket(this.getPort());
			return true;
		} catch (Exception e) {
			System.out.println("Cannot bind port: " + port);
		}
		return false;
	}

	public String getHost() {
		return Constants.HOST;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<Integer> getTaskChunk() {
		return taskChunk;
	}

	public void setTaskChunk(List<Integer> taskChunk) {
		this.taskChunk = taskChunk;
	}
}