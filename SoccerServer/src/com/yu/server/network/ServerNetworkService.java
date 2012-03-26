package com.yu.server.network;

import com.yu.overallsth.GameData;
import com.yu.server.controller.ServerController;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class ServerNetworkService extends Service {

	//
	ServerOutputBuffer outputBuffer;
	ServerInputBufferPool inputBufferPool;
	// Thread
	Thread threadNetwork;
	Thread threadController;
	//
	ServerNetwork network;
	ServerController controller;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		System.out.println("ON------------------------------DESTROY!!!");
		super.onDestroy();
		//停止线程
		stop();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//获取端口号
		Bundle bundle = intent.getExtras();
		String s = bundle.getString("ListeningPort");
		int port = Integer.parseInt(s);
		//
		
		GameData data0 = new GameData(100, 400, 10, 0);
		GameData data1 = new GameData(200, 400, 10, 0);
		outputBuffer = new ServerOutputBuffer();
		inputBufferPool = new ServerInputBufferPool();
		network = new ServerNetwork(port, outputBuffer, inputBufferPool);
		controller = new ServerController(data0,  data1, outputBuffer, inputBufferPool,
				320, 480);
		threadNetwork = new Thread(network);
		threadController = new Thread(controller);
		threadNetwork.start();
		threadController.start();
		
		return super.onStartCommand(intent, flags, startId);
		
	}
	
	
	/**
	 * 停止控制和网络线程
	 */
	private void stop() {
		network.stopServerNetwork();
		controller.stopRunning();
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("threadNet:" + threadNetwork.isAlive());
		network.showSubThreadStatus();
		System.out.println("threadCtrl:" + threadController.isAlive());

	}

	
}