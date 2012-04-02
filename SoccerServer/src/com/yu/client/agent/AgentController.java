package com.yu.client.agent;

import com.yu.basicelements.Side;
import com.yu.client.network.ClientOutputBuffer;
import com.yu.overallsth.Pitch;
import com.yu.overallsth.Player;

public class AgentController{
	Agent agents[];
	int numOfAgent;
	Side side;
	Pitch pitch;
	AgentOutputBuffer agentOutputBuffer;
	AgentOutput agentOutput;
	ClientOutputBuffer clientOutputBuffer;
	public AgentController(int numOfAgent, Side side, Pitch pitch, AgentOutputBuffer agentOutputBuffer, ClientOutputBuffer clientOutputBuffer) {
		super();
		this.numOfAgent = numOfAgent;
		this.side = side;
		this.pitch = pitch;
		this.agentOutputBuffer = agentOutputBuffer;
		this.agentOutput = agentOutput;
		this.clientOutputBuffer = clientOutputBuffer;
		agents = new Agent[this.numOfAgent];
		initAgent();
		
		agentOutput = new AgentOutput();
	}
	
	public void initAgent(){
		Player p[] = pitch.getAllPlayer(this.side);
		for(int i = 0;i< this.numOfAgent; i++){
			agents[i] = new Agent(p[i].getSide(), p[i].getNO(), this.pitch, agentOutputBuffer);
		}
		for(int i = 0;i< this.numOfAgent; i++){
			new Thread(agents[i]).start();
		}
	}
	
	public void stopAgentController(){
		for(int i = 0;i< this.numOfAgent; i++){
			agents[i].stopAgent();
		}
	}

}
