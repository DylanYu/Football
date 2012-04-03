package com.yu.server.network;

import java.io.DataInputStream;
import java.io.IOException;

import com.yu.overallsth.Str;

public class ServerNetworkIn implements Runnable {

	DataInputStream inputFromClient = null;
	ServerInputBufferPool inputBufferPool = null;
	//判别在BufferPool中的位置
	int noOfClient = -1;

//	private String commandUp = Str.COMMAND_UP;
//	private String commandUp0 = Str.COMMAND_UP0;
//	private String commandUp1 = Str.COMMAND_UP1;

	boolean isRunning = true;
	
	public ServerNetworkIn(DataInputStream inputFromClient,
			ServerInputBufferPool inputBufferPool, int noOfClient) {
		this.inputFromClient = inputFromClient;
		this.inputBufferPool = inputBufferPool;
		this.noOfClient = noOfClient;
	}

	@Override
	public void run() {
		try {
			while (isRunning) {
				String s = inputFromClient.readUTF();
				if(noOfClient == 0)
					inputBufferPool.getFirstBuffer().add(s);
				else if(noOfClient == 1) 
					inputBufferPool.getSecondBuffer().add(s);
				else System.out.println("ServerInputPostion Error!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void stopRunning(){
		isRunning = false;
	}

}
