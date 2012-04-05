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
		}
	}
	
	public void stoRunning(){
		this.isRunning = false;
	}

}
