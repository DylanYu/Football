package com.yu.activities;

import com.yu.R;
import com.yu.overallsth.GameData;
import com.yu.server.controller.ServerController;
import com.yu.server.network.ServerInputBufferPool;
import com.yu.server.network.ServerNetwork;
import com.yu.server.network.ServerOutputBuffer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class UpServerActivity extends Activity {

	// btn
	Button startListening = null;
	Button show = null;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server);
		startListening = (Button) findViewById(R.id.startListening);
		startListening.setOnClickListener(new startListener());
		show = (Button) findViewById(R.id.showResult);
		show.setOnClickListener(new showListener());
	}

	class showListener implements OnClickListener {
		public void onClick(View v) {
			show();
		}
	}

	class startListener implements OnClickListener {
		public void onClick(View v) {
			//GameData
			GameData data0 = new GameData(100, 400, 10, 0);
			GameData data1 = new GameData(200, 400, 10, 0);
			outputBuffer = new ServerOutputBuffer();
			inputBufferPool = new ServerInputBufferPool();
			network = new ServerNetwork(outputBuffer, inputBufferPool);
			controller = new ServerController(data0,  data1, outputBuffer, inputBufferPool,
					320, 480);
			threadNetwork = new Thread(network);
			threadController = new Thread(controller);
			threadNetwork.start();
			threadController.start();
		}
	}

	private void show() {
		Log.i("show", "outBuffer is " + outputBuffer.getSize());
		Log.i("show", "inBuffer0 is " + inputBufferPool.getFirstBuffer().getSize());
		Log.i("show", "inBuffer1 is " + inputBufferPool.getSecondBuffer().getSize());
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

	@Override
	protected void onPause() {
		super.onPause();
		stop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
