package com.yu.server.controller;

import java.util.Date;

import com.yu.basicelements.Side;
import com.yu.overallsth.Pitch;
import com.yu.overallsth.Player;
import com.yu.server.network.ServerInputBuffer;
import com.yu.server.network.ServerInputBufferPool;
import com.yu.server.network.ServerOutputBuffer;

public class ServerController implements Runnable {

	private Pitch pitch = null;
	private ServerOutputBuffer outputBuffer = null;
	private ServerInputBufferPool inputBufferPool = null;
	private int width;
	private int height;
	
	private int frequency = -1;

	boolean isRunning = true;
	
	public ServerController(Pitch pitch, ServerOutputBuffer outputBuffer,
			ServerInputBufferPool inputBufferPool, int width, int height, int frequency) {
		super();
		this.outputBuffer = outputBuffer;
		this.inputBufferPool = inputBufferPool;
		this.pitch = pitch;
		this.width = width;
		this.height = height;
		this.frequency = frequency;
	}

	@Override
	public void run() {
		while (isRunning) {
			move();
			setOutput();
			//TODO delete
			//System.out.println("X:" + pitch.getBallPX() + ";Y:" + pitch.getBallPY() + ";SX:" + pitch.getBallSpeedX() + ";SY:" + pitch.getBallSpeedY());
			try {
				//TODO 这是全局频率，相当重要
				Thread.sleep(1000/frequency);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void move() {
		pitch.calAllObjectsNextCycle();
		if (!inputBufferPool.getFirstBuffer().isEmpty()) {
			boolean succs = inputBufferPool.getFirstBuffer().remove();
			if(!succs)
				System.out.println("ServerInputBuffer0 error in controller");
			//pitch.changePlayerSpeedRamdomly(Side.LEFT);
			//pitch.initLeftRandomly();
			//TODO delete
			pitch.initPitchRandomly();
		}
		if (!inputBufferPool.getSecondBuffer().isEmpty()) {
			boolean succs = inputBufferPool.getSecondBuffer().remove();
			if(!succs)
				System.out.println("ServerInputBuffer1 error in controller");
			//pitch.changePlayerSpeedRamdomly(Side.RIGHT);
			pitch.initRightRandomly();
		}
	}

	/**
	 * 输出信息
	 */
	private void setOutput() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(System.currentTimeMillis() + "|");
		buffer.append(pitch.getScore0() + "|");
		buffer.append(pitch.getScore1() + "|");
		//
		buffer.append(pitch.getBallPX() + ";");
		buffer.append(pitch.getBallPY() + ";");
		buffer.append(pitch.getBallSpeedX() + ";");
		buffer.append(pitch.getBallSpeedY() + ";");
		buffer.append("|");
		//
		Player player0[] = pitch.getAllPlayer(Side.LEFT);
		Player player1[] = pitch.getAllPlayer(Side.RIGHT);
		for(int i = 0; i < player0.length; i++){
			buffer.append("0" + ";");
			buffer.append(i + ";");
			buffer.append(player0[i].getPosition().getX() + ";");
			buffer.append(player0[i].getPosition().getY() + ";");
			buffer.append(player0[i].getSpeed().getSpeedX() + ";");
			buffer.append(player0[i].getSpeed().getSpeedY() + ";");
			//to be continued
			buffer.append("|");
		}
		for(int i = 0; i < player1.length; i++){
			buffer.append("0" + ";");
			buffer.append(i + ";");
			buffer.append(player1[i].getPosition().getX() + ";");
			buffer.append(player1[i].getPosition().getY() + ";");
			buffer.append(player1[i].getSpeed().getSpeedX() + ";");
			buffer.append(player1[i].getSpeed().getSpeedY() + ";");
			//to be continued
			buffer.append("|");
		}
		
		outputBuffer.add(buffer.toString());
	}
	
	public void stopRunning(){
		isRunning = false;
	}
	
}
