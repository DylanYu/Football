package com.yu.server.controller;

import com.yu.overallsth.GameData;
import com.yu.server.network.ServerInputBuffer;
import com.yu.server.network.ServerInputBufferPool;
import com.yu.server.network.ServerOutputBuffer;

public class ServerController implements Runnable {

	private GameData data0 = null;
	private GameData data1 = null;
	private ServerOutputBuffer outputBuffer = null;
	private ServerInputBufferPool inputBufferPool = null;
	private int width;
	private int height;
	
	private int frequency = -1;

	boolean isRunning = true;
	
	public ServerController(GameData data0, GameData data1, ServerOutputBuffer outputBuffer,
			ServerInputBufferPool inputBufferPool, int width, int height, int frequency) {
		super();
		this.outputBuffer = outputBuffer;
		this.inputBufferPool = inputBufferPool;
		this.data0 = data0;
		this.data1 = data1;
		this.width = width;
		this.height = height;
		this.frequency = frequency;
	}

	@Override
	public void run() {
		while (isRunning) {
			move();
			setOutput();
			try {
				//TODO 这是全局频率，相当重要
				Thread.sleep(1000/frequency);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void move() {
		data0.downY();
		if (!inputBufferPool.getFirstBuffer().isEmpty()) {
			boolean succs = inputBufferPool.getFirstBuffer().remove();
			if(!succs)
				System.out.println("ServerInputBuffer0 error in controller");
			data0.upY();
		}
		data1.downY();
		if (!inputBufferPool.getSecondBuffer().isEmpty()) {
			boolean succs = inputBufferPool.getSecondBuffer().remove();
			if(!succs)
				System.out.println("ServerInputBuffer1 error in controller");
			data1.upY();
		}
	}

	private void setOutput() {
		outputBuffer.add(data0.getX(), data0.getY(), data1.getX(), data1.getY());
	}
	
	public void stopRunning(){
		isRunning = false;
	}
	/*
	 * private void move() { //TODO //synchronized(data){ if (!isIn()) {
	 * if(getCollisionType() == 1){ data.setAngle(180 - data.getAngle()); }else{
	 * data.setAngle(360 - data.getAngle()); } } data.setX((data.getX() + 6 *
	 * Math.cos(data.getAngle() / 180.0 * Math.PI))) ; data.setY((data.getY() +
	 * 6 * Math.sin(data.getAngle() / 180.0 * Math.PI))) ; }
	 * 
	 * private boolean isIn() { if (data.getX() <= data.getRadius() ||
	 * data.getY() <= data.getRadius() || data.getX() >= (width -
	 * data.getRadius()) || data.getY() >= (height - data.getRadius())) return
	 * false; else return true; }
	 * 
	 * private int getCollisionType(){ if(data.getX() <= data.getRadius() ||
	 * data.getX() >= (width - data.getRadius())) return 1; else return 2; }
	 */
}
