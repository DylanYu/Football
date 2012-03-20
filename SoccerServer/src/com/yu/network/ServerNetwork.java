package com.yu.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


public class ServerNetwork implements Runnable {

	DataInputStream inputFromClient = null;
	DataOutputStream outputToClient = null;
	ServerOutputBuffer outputBuffer = null;
	ServerInputBuffer inputBuffer = null;
	
	
	
	public ServerNetwork(ServerOutputBuffer outputBuffer, ServerInputBuffer inputBuffer) {
		super();
		this.outputBuffer = outputBuffer;
		this.inputBuffer = inputBuffer;
	}

	@Override
	public void run() {
		ServerSocket sc = null;
		try {
			sc = new ServerSocket(5678);
			System.out.println("Server Started at " + new Date());
			int numOfClient = 0;
			while (true) {
				Socket socket = sc.accept();
				outputToClient = new DataOutputStream(socket.getOutputStream());
				inputFromClient = new DataInputStream(socket.getInputStream());
				System.out.println("Client No." + numOfClient
						+ "connected at " + new Date());
				InetAddress inetAddress = socket.getInetAddress();
				System.out.println("Client No." + numOfClient
						+ "'s name is " + inetAddress.getHostName());
				System.out.println("Client No." + numOfClient
						+ "'s address is " + inetAddress.getHostAddress());
				numOfClient++;
				
				
				ServerNetworkIn networkIn = new ServerNetworkIn(inputFromClient, inputBuffer);
				new Thread(networkIn).start();
				ServerNetworkOut networkOut = new ServerNetworkOut(outputToClient, outputBuffer);
				new Thread(networkOut).start();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				sc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
