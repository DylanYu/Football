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

	
	public ClientNetwork(ClientOutputBuffer outputBuffer, ClientInputBuffer inputBuffer){
		this.inputBuffer = inputBuffer;
		this.outputBuffer = outputBuffer;
	}
	
	public void run(){
		try{
        	Socket socket = new Socket("10.0.2.2",4567);
	        outputToServer = new DataOutputStream(socket.getOutputStream());
			inputFromServer = new DataInputStream(socket.getInputStream());
			ClientNetworkIn netWorkIn = new ClientNetworkIn(inputFromServer, inputBuffer);
			ClientNetworkOut netWorkOut = new ClientNetworkOut(outputToServer, outputBuffer);
			Thread threadOut = new Thread(netWorkOut);
			Thread threadIn = new Thread(netWorkIn);
			threadOut.start();
			threadIn.start();
			
        }catch (IOException e){
        	e.printStackTrace();
        }
	}

}
