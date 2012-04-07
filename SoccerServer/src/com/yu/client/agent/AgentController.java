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
	
	//
	AgentOutput agentOutput;
	//
	
	ClientOutputBuffer clientOutputBuffer;
	public AgentController(int numOfAgent, Side side, Pitch pitch, AgentOutputBuffer agentOutputBuffer, ClientOutputBuffer clientOutputBuffer) {
		super();
		this.numOfAgent = numOfAgent;
		this.side = side;
		this.pitch = pitch;
		this.agentOutputBuffer = agentOutputBuffer;
		this.clientOutputBuffer = clientOutputBuffer;
		agents = new Agent[this.numOfAgent];
		initAgent();
		
		agentOutput = new AgentOutput(agentOutputBuffer, clientOutputBuffer);
		new Thread(agentOutput).start();
	}
	
	public void initAgent(){
		Player p[] = pitch.getAllPlayer(this.side);
		for(int i = 0;i< this.numOfAgent; i++){
			agents[i] = new Agent(p[i].getSide(), p[i].getNO(), this.pitch, agentOutputBuffer);
		}
		for(int i = 0;i< this.numOfAgent; i++){
			//TODO 只让一半的人动作
			//if(i == 1 ||i == 3 ||i == 5 ||i == 7 ||i == 9)
				new Thread(agents[i]).start();
		}
	}
	
	public void stopAgentController(){
		for(int i = 0;i< this.numOfAgent; i++){
			agents[i].stopRunning();
		}
		agentOutput.stopRunning();
	}

}
