package com.yu.client.agent;

import com.yu.client.network.ClientOutputBuffer;

public class AgentOutput implements Runnable {

	AgentOutputBuffer agentOutoutBuffer;
	ClientOutputBuffer clientOutputBuffer;
	boolean isRunning = true;
	
	
	public AgentOutput(AgentOutputBuffer agentOutoutBuffer,
			ClientOutputBuffer clientOutputBuffer) {
		super();
		this.agentOutoutBuffer = agentOutoutBuffer;
		this.clientOutputBuffer = clientOutputBuffer;
	}

	@Override
	public void run() {
		String s = agentOutoutBuffer.getAllThenRemove();
		clientOutputBuffer.add(s);
	}
	
	public void stopRunning(){
		this.isRunning = false;
	}

}
