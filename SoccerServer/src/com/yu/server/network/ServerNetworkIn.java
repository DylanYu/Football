package com.yu.server.network;

import java.io.DataInputStream;
import java.io.IOException;

import com.yu.overallsth.Str;

public class ServerNetworkIn implements Runnable {

	DataInputStream inputFromClient = null;
	ServerInputBufferPool inputBufferPool = null;
	//在BufferPool中的位置
	int position = -1;

	private String commandUp = Str.COMMAND_UP;
	private String commandUp0 = Str.COMMAND_UP0;
	private String commandUp1 = Str.COMMAND_UP1;

	boolean isRunning = true;
	
	public ServerNetworkIn(DataInputStream inputFromClient,
			ServerInputBufferPool inputBufferPool, int position) {
		this.inputFromClient = inputFromClient;
		this.inputBufferPool = inputBufferPool;
		this.position = position;
	}

	@Override
	public void run() {
		try {
			while (isRunning) {
				String s = inputFromClient.readUTF();
				if (s.equals(commandUp)) {
					if(position == 0)
						inputBufferPool.getFirstBuffer().add(commandUp0);
					else if(position ==1) 
						inputBufferPool.getSecondBuffer().add(commandUp1);
					else System.out.println("ServerInputPostion Error!");
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
