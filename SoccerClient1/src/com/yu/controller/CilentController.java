package com.yu.controller;

import com.yu.network.ClientInputBuffer;
import com.yu.network.ClientOutputBuffer;
import com.yu.overallsth.GameData;

public class CilentController implements Runnable{
	GameData data;
	ClientOutputBuffer outputBuffer;
	ClientInputBuffer inputBuffer;
	int width;
	int height;
	
	boolean isRunning = true;

	public CilentController(GameData data, ClientOutputBuffer outputBuffer, ClientInputBuffer inputBuffer, int width, int height) {
		super();
		this.data = data;
		this.outputBuffer = outputBuffer;
		this.inputBuffer = inputBuffer;
		this.width = width;
		this.height = height;
	}
	
	public void run() {
		while(isRunning){
			updateState();
			try {
				//TODO
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据服务器传过来的信息更新当前屏幕状态
	 */
	private void updateState(){
		//TODO syn
		if(!inputBuffer.isEmpty()){
			double[] in = inputBuffer.getThenRemove();
			if(in == null)
				System.out.println("ClientInputBuffer error in controller");
			data.setPosition(in[0], in[1]);
		}else {
			//TODO
			////看看会不会有丢帧
			System.out.println("ClientInputBuffer is empty");
		}
	}
	
	/**
	 * 上升一次
	 */
	public void upOnce(){
		outputBuffer.add();
	}
	
	public void handleAClick(){
		upOnce();
	}
	
	public void stopRunning(){
		isRunning = false;
	}
}
