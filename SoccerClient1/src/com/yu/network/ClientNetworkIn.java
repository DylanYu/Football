package com.yu.network;

import java.io.DataInputStream;
import java.io.IOException;

public class ClientNetworkIn implements Runnable {
	DataInputStream inputFromServer = null;
	ClientInputBuffer inputBuffer = null;

	public ClientNetworkIn(DataInputStream inputFromServer, ClientInputBuffer inputBuffer) {
		this.inputFromServer = inputFromServer;
		this.inputBuffer = inputBuffer;
	}

	public void run() {
		try {
			while (true) {
				String[] s = inputFromServer.readUTF().split(",");
				double a = Double.parseDouble(s[0]);
				double b = Double.parseDouble(s[1]);
				// TODO synchronized
				inputBuffer.add(a, b);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
