package com.yu.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientNetwork implements Runnable{
	
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
	
	public ClientNetwork(ClientOutputBuffer outputBuffer, ClientInputBuffer inputBuffer){
		this.inputBuffer = inputBuffer;
		this.outputBuffer = outputBuffer;
	}
	
	public void run(){
		try{
        	socket = new Socket("10.0.2.2",4567);
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
