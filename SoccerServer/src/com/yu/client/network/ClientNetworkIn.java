package com.yu.client.network;

import java.io.DataInputStream;
import java.io.IOException;

public class ClientNetworkIn implements Runnable {
	DataInputStream inputFromServer = null;
	ClientInputBuffer inputBuffer = null;

	boolean isRunning = true;
	
	public ClientNetworkIn(DataInputStream inputFromServer, ClientInputBuffer inputBuffer) {
		this.inputFromServer = inputFromServer;
		this.inputBuffer = inputBuffer;
	}

	public void run() {
		try {
			while (isRunning) {
				String s = inputFromServer.readUTF();
				// TODO synchronized
				inputBuffer.add(s);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void stopRunning(){
		isRunning = false;
	}

}
