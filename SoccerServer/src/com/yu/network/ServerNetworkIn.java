package com.yu.network;

import java.io.DataInputStream;
import java.io.IOException;

import com.yu.overallsth.Str;

public class ServerNetworkIn implements Runnable {

	DataInputStream inputFromClient = null;
	ServerInputBuffer inputBuffer = null;

	private String commandUp = Str.COMMAND_UP;

	boolean isRunning = true;
	
	public ServerNetworkIn(DataInputStream inputFromClient,
			ServerInputBuffer inputBuffer) {
		this.inputFromClient = inputFromClient;
		this.inputBuffer = inputBuffer;
	}

	@Override
	public void run() {
		try {
			while (isRunning) {
				String s = inputFromClient.readUTF();
				if (s.equals(commandUp)) {
					inputBuffer.add();
				} 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void stopRunning(){
		isRunning = false;
	}

}
