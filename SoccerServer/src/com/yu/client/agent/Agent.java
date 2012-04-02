package com.yu.client.agent;

import com.yu.basicelements.Side;
import com.yu.client.network.ClientOutputBuffer;
import com.yu.overallsth.Pitch;
import com.yu.overallsth.Str;

public class Agent implements Runnable{

	Side side;
	int NO;
	Pitch pitch;
	

	AgentOutputBuffer agentOutputBuffer;
	
	//
	WorldState worldState;
	//TODO isRunning
	private boolean isRunning = true;
	
	
	
	public Agent(Side side, int unum, Pitch pitch,
			AgentOutputBuffer agentOutputBuffer) {
		super();
		this.side = side;
		this.NO = unum;
		this.pitch = pitch;
		//this.worldState = worldState;
		this.agentOutputBuffer = agentOutputBuffer;
	}

	public void run() {
		while(isRunning){
			worldState = pitch.requestWorldState(this.side, this.NO);
			chaseBall();
			
			//TODO client agent sleep a while
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void chaseBall(){
//		double dashPower = Str.MAX_DASH_POWER;
		double dashPower = Str.NORMAL_DASH_POWER;
				
		double a = 0;
		double x = worldState.getOwnPosition().getX();
		double y = worldState.getOwnPosition().getY();
		double ballX = worldState.getBallPosition().getX();
		double ballY = worldState.getBallPosition().getY();
		double dx = ballX - x;
		double dy = ballY - y;
		double l = Math.pow(dx * dx + dy * dy, 0.5);
		double acos = Math.acos(dx / l);
		double asin = Math.asin(dy / l);
		
		/*
		 * 逆时针角度
		 */
		if(acos < Math.PI /2){
				a = asin;
		} else {
				a = Math.PI - asin;
		}

		StringBuffer buffer = new StringBuffer();
		String side;
		if(this.side == Side.LEFT)
			side = "0";
		else side = "1";
		//开始
		buffer.append(side + ",");
		buffer.append(this.NO + ",");
		buffer.append("dash" + ",");
		buffer.append(a + ",");
		buffer.append(dashPower + ",");
		//结尾
		buffer.append("|");
		
		agentOutputBuffer.add(this.NO, buffer);
	}
	
	
//	private double changeAngle(double angle){
//		return angle / Math.PI * 180;
//	}
	
	public void stopAgent(){
		this.isRunning = false;
	}

}
