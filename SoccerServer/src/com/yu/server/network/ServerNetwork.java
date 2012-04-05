package com.yu.server.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class ServerNetwork implements Runnable {

	//端口号
	int port = -1;
	
	DataInputStream inputFromClient = null;
	DataOutputStream outputToClient = null;
	ServerOutputBuffer outputBuffer = null;
	ServerInputBufferPool inputBufferPool = null;

	ServerNetworkIn networkIn;
	ServerNetworkOut networkOut;

	Thread threadNetworkIn;
	Thread threadNetworkOut;

	ServerSocket sc = null;

	boolean isRunning = true;

	public ServerNetwork(int port, ServerOutputBuffer outputBuffer,
			ServerInputBufferPool inputBufferPool) {
		super();
		this.port = port;
		this.outputBuffer = outputBuffer;
		this.inputBufferPool = inputBufferPool;
	}

	@Override
	public void run() {
		try {
			sc = new ServerSocket(port);
			System.out.println("Server Started at " + new Date());
			int noOfClient = -1;
			while (isRunning) {
				Socket socket = null;
				socket = sc.accept();
				//NO
				noOfClient++;
				/*
				 * 每當有客戶接入，服務器輸出緩存增加一位
				 */
				outputBuffer.addAClient();
				//
				
				outputToClient = new DataOutputStream(socket.getOutputStream());
				inputFromClient = new DataInputStream(socket.getInputStream());
				System.out.println("Client No." + noOfClient + "connected at "
						+ new Date());
				InetAddress inetAddress = socket.getInetAddress();
				System.out.println("Client No." + noOfClient + "'s name is "
						+ inetAddress.getHostName());
				System.out.println("Client No." + noOfClient
						+ "'s address is " + inetAddress.getHostAddress());
				
				//TODO 发送客户端唯一标识 NO
				//outputToClient.writeUTF(String.valueOf(numOfClient));
				
				//TODO 这里如果用全局变量，会不会导致双机连接时的大问题

				
				//指定客户机的序号
				networkIn = new ServerNetworkIn(inputFromClient, inputBufferPool, noOfClient);
				networkOut = new ServerNetworkOut(outputToClient, outputBuffer, noOfClient);
				threadNetworkIn = new Thread(networkIn);
				threadNetworkOut = new Thread(networkOut);
				threadNetworkIn.start();
				threadNetworkOut.start();
				
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO 为什么这里的finally没有发挥作用
//		finally {
//			try {
//				sc.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
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
