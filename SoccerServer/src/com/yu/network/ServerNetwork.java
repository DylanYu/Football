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

	ServerNetworkIn networkIn;
	ServerNetworkOut networkOut;

	Thread threadNetworkIn;
	Thread threadNetworkOut;

	ServerSocket sc = null;

	boolean isRunning = true;

	public ServerNetwork(ServerOutputBuffer outputBuffer,
			ServerInputBuffer inputBuffer) {
		super();
		this.outputBuffer = outputBuffer;
		this.inputBuffer = inputBuffer;
	}

	@Override
	public void run() {
		try {
			sc = new ServerSocket(5678);
			System.out.println("Server Started at " + new Date());
			int numOfClient = 0;
			while (isRunning) {
				Socket socket = null;
				socket = sc.accept();
				outputToClient = new DataOutputStream(socket.getOutputStream());
				inputFromClient = new DataInputStream(socket.getInputStream());
				System.out.println("Client No." + numOfClient + "connected at "
						+ new Date());
				InetAddress inetAddress = socket.getInetAddress();
				System.out.println("Client No." + numOfClient + "'s name is "
						+ inetAddress.getHostName());
				System.out.println("Client No." + numOfClient
						+ "'s address is " + inetAddress.getHostAddress());
				numOfClient++;

				//TODO 这里如果用全局变量，会不会导致双机连接时的大问题
				
				networkIn = new ServerNetworkIn(inputFromClient, inputBuffer);
				networkOut = new ServerNetworkOut(outputToClient, outputBuffer);
				threadNetworkIn = new Thread(networkIn);
				threadNetworkOut = new Thread(networkOut);
				threadNetworkIn.start();
				threadNetworkOut.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO 为什么这里的finally没有发挥作用
		finally {
			try {
				sc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopServerNetwork() {
		this.isRunning = false;
		if(networkIn != null)
			networkIn.stopRunning();
		if(networkOut != null)
		networkOut.stopRunning();
		try {
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showSubThreadStatus() {
		if(threadNetworkIn != null)
			System.out.println("threadNetworkIn:" + threadNetworkIn.isAlive());
		if(threadNetworkOut != null)
			System.out.println("threadNetworkOut:" + threadNetworkOut.isAlive());
	}

}
