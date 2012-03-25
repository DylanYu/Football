package com.yu.server.network;

import java.io.DataOutputStream;
import java.io.IOException;

public class ServerNetworkOut implements Runnable {

	DataOutputStream outputToClient = null;
	ServerOutputBuffer outputBuffer = null;

	boolean isRunning = true;
	
	public ServerNetworkOut(DataOutputStream outputToClient,
			ServerOutputBuffer outputBuffer) {
		this.outputToClient = outputToClient;
		this.outputBuffer = outputBuffer;
	}

	@Override
	public void run() {
		try {
			while (isRunning) {
				if (!outputBuffer.isEmpty()) {
					double[] t = outputBuffer.getThenRemove();
					if(t == null)
						System.out.println("ServerOutputBuffer error");
					String s = t[0] + "," + t[1] + "," + t[2] + "," + t[3];
					outputToClient.writeUTF(s);
					outputToClient.flush();
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
