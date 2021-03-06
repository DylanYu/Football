package com.yu.server.network;

import java.io.DataOutputStream;
import java.io.IOException;

public class ServerNetworkOut implements Runnable {

	DataOutputStream outputToClient = null;
	ServerOutputBuffer outputBuffer = null;

	/*
	 * 告知客户端他应该控制哪一部分player
	 */
	int noOfClient = -1;
	boolean isVirgin = true;

	boolean isRunning = true;

	public ServerNetworkOut(DataOutputStream outputToClient,
			ServerOutputBuffer outputBuffer, int noOfClient) {
		this.outputToClient = outputToClient;
		this.outputBuffer = outputBuffer;
		this.noOfClient = noOfClient;
	}

	@Override
	public void run() {
		try {
			if(isVirgin){
				isVirgin = false;
				
				String s = String.valueOf(this.noOfClient);
				outputToClient.writeUTF(s);
				outputToClient.flush();
				System.out.println(noOfClient + "--------SERVER FIRST OUT---------------");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			while (isRunning) {
				
					//TODO big problem,
					String s = outputBuffer.getThenRemove(noOfClient);
					if (s == null){
						System.out.println("ServerNetowrkOutput::Error");
					}
					else {
						outputToClient.writeUTF(s);
						outputToClient.flush();
					}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopRunning() {
		isRunning = false;
	}
}
