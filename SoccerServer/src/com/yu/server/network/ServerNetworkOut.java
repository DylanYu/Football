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
				//if (!outputBuffer.isEmpty()) {
					String s = outputBuffer.getThenRemove();
					if (s == null)
						System.out.println("ServerOutputBuffer error");
					outputToClient.writeUTF(s);
					outputToClient.flush();
//				} else {
//					//TODO
//					try {
//						Thread.sleep(20);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopRunning() {
		isRunning = false;
	}
}
