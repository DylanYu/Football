package com.yu.controller;

import com.yu.network.ServerInputBuffer;
import com.yu.network.ServerOutputBuffer;
import com.yu.overallsth.GameData;

public class ServerController implements Runnable {

	private GameData data = null;
	private ServerOutputBuffer outputBuffer = null;
	private ServerInputBuffer inputBuffer = null;
	private int width;
	private int height;

	public ServerController(GameData data, ServerOutputBuffer outputBuffer,
			ServerInputBuffer inputBuffer, int width, int height) {
		super();
		this.outputBuffer = outputBuffer;
		this.inputBuffer = inputBuffer;
		this.data = data;
		this.width = width;
		this.height = height;
	}

	public void init() {

	}

	@Override
	public void run() {
		while (true) {
			move();
			setOutput();
			try {
				//TODO
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void move() {
		data.downY();
		if (!inputBuffer.isEmpty()) {
			// inputBuffer.getThenRemove();
			boolean succs = inputBuffer.remove();
			if(!succs)
				System.out.println("ServerInputBuffer error in controller");
			data.upY();
		}
	}

	private void setOutput() {
		outputBuffer.add(data.getX(), data.getY());
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
