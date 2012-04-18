package com.yu.server.controller;

public class ServerCommand implements Runnable {

	private ServerCommandBuffer serverCommandBuffer;
	private int frequency;
	
	public ServerCommand(ServerCommandBuffer serverCommandBuffer) {
		super();
		this.serverCommandBuffer = serverCommandBuffer;
	}

	private boolean isRunning = true;

	@Override
	public void run() {
		while(isRunning){
			this.serverCommandBuffer.setCommandFromServerInputBuffer();
			try {
				//TODO ServerCommand frequency
				Thread.sleep(80);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stoRunning(){
		this.isRunning = false;
	}

}
