package com.yu.client.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientNetwork implements Runnable{
	
	//ServerIP
	String serverIP = null;
	int serverPort = -1;
	
	ClientInputBuffer inputBuffer = null;
	ClientOutputBuffer outputBuffer = null;
	DataOutputStream outputToServer = null;
	DataInputStream inputFromServer = null;

	ClientNetworkIn networkIn;
	ClientNetworkOut networkOut;
	
	Socket socket;
	//thread
	Thread threadOut;
	Thread threadIn;
	
	public ClientNetwork(String serverIP, int serverPort, ClientOutputBuffer outputBuffer, ClientInputBuffer inputBuffer){
		this.serverIP = new String(serverIP);
		this.serverPort = serverPort;
		this.inputBuffer = inputBuffer;
		this.outputBuffer = outputBuffer;
	}
	
	public void run(){
		try{
        	socket = new Socket(serverIP, serverPort);
	        outputToServer = new DataOutputStream(socket.getOutputStream());
			inputFromServer = new DataInputStream(socket.getInputStream());
			networkIn = new ClientNetworkIn(inputFromServer, inputBuffer);
			networkOut = new ClientNetworkOut(outputToServer, outputBuffer);
			threadOut = new Thread(networkOut);
			threadIn = new Thread(networkIn);
			threadOut.start();
			threadIn.start();
        }catch (IOException e){
        	e.printStackTrace();
        }
	}
	
	public void stopClientNetwork(){
		networkIn.stopRunning();
		networkOut.stopRunning();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showSubThreadStatus(){
		System.out.println("threadOut:" + threadOut.isAlive());
		System.out.println("threadIn:" + threadIn.isAlive());
	}

}
