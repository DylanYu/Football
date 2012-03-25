package com.yu.client.controller;

import com.yu.client.network.ClientInputBuffer;
import com.yu.client.network.ClientOutputBuffer;
import com.yu.overallsth.GameData;
import com.yu.overallsth.Str;

public class CilentController implements Runnable{
	
	private String COMMAND_UP = Str.COMMAND_UP;
	
	GameData data0;
	GameData data1;
	ClientOutputBuffer outputBuffer;
	ClientInputBuffer inputBuffer;
	int width;
	int height;
	
	boolean isRunning = true;

	public CilentController(GameData data0, GameData data1, ClientOutputBuffer outputBuffer, ClientInputBuffer inputBuffer, int width, int height) {
		super();
		this.data0 = data0;
		this.data1 = data1;
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
			data0.setPosition(in[0], in[1]);
			data1.setPosition(in[2], in[3]);
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
		outputBuffer.add(COMMAND_UP);
	}
	
	public void handleAClick(){
		upOnce();
	}
	
	public void stopRunning(){
		isRunning = false;
	}
}
