package com.yu.client.network;

import java.io.DataInputStream;
import java.io.IOException;

public class ClientNetworkIn implements Runnable {
	DataInputStream inputFromServer = null;
	ClientInputBuffer inputBuffer = null;

	/*
	 * 这个标识是为了判别自己是哪一台客户端，应该控制哪一部分player
	 */
	int noOfClient = -1;
	boolean isVirgin = true;
	
	boolean isRunning = true;
	
	public ClientNetworkIn(DataInputStream inputFromServer, ClientInputBuffer inputBuffer) {
		this.inputFromServer = inputFromServer;
		this.inputBuffer = inputBuffer;
	}

	public void run() {
		
		
		try {
			String ss = inputFromServer.readUTF();
			
			/*
			 * 初次连接时被告知自己的身份，记忆下来
			 */
			if(isVirgin){
				isVirgin = false;
				noOfClient = Integer.parseInt(ss);
				System.out.println(noOfClient + "-------CLIENT FIRST IN-------");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		///////////////
		//TODO delete
//				int n = 0;
//				double a = Math.random() * 10;
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
	
	public int getNoOfClient(){
		return this.noOfClient;
	}
	
	public void stopRunning(){
		isRunning = false;
	}

}
