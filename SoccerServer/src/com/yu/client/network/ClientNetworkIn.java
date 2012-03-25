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
				String[] s = inputFromServer.readUTF().split(",");
				double a = Double.parseDouble(s[0]);
				double b = Double.parseDouble(s[1]);
				double c = Double.parseDouble(s[2]);
				double d = Double.parseDouble(s[3]);
				// TODO synchronized
				inputBuffer.add(a, b, c, d);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void stopRunning(){
		isRunning = false;
	}

}
