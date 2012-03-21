package com.yu;

import com.yu.controller.ServerController;
import com.yu.network.ServerInputBuffer;
import com.yu.network.ServerNetwork;
import com.yu.network.ServerOutputBuffer;
import com.yu.overallsth.GameData;

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
	ServerInputBuffer inputBuffer;
	// Thread
	Thread threadNetwork;
	Thread threadController;
	//
	ServerNetwork network;
	ServerController controller;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
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
			GameData data = new GameData(160, 400, 10, 0);
			outputBuffer = new ServerOutputBuffer();
			inputBuffer = new ServerInputBuffer();
			network = new ServerNetwork(outputBuffer, inputBuffer);
			controller = new ServerController(data, outputBuffer, inputBuffer,
					320, 480);
			threadNetwork = new Thread(network);
			threadController = new Thread(controller);
			threadNetwork.start();
			threadController.start();
		}
	}

	private void show() {
		Log.i("show", "outBuffer is " + outputBuffer.getSize());
		Log.i("show", "inBuffer is " + inputBuffer.getSize());
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
