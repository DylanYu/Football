package com.yu;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

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
	Button show	= null;
	//
	ServerOutputBuffer outputBuffer;
	ServerInputBuffer inputBuffer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		startListening = (Button) findViewById(R.id.startListening);
		startListening.setOnClickListener(new startListener());
		show = (Button)findViewById(R.id.showResult);
		show.setOnClickListener(new showListener());
	}

	class showListener implements OnClickListener{
		public void onClick(View v){
			show();
		}
	}
	class startListener implements OnClickListener {
		public void onClick(View v) {
			GameData data = new GameData(160, 400, 10, 0);
			outputBuffer = new ServerOutputBuffer();
			inputBuffer =new ServerInputBuffer();
			ServerNetwork network = new ServerNetwork(outputBuffer, inputBuffer);
			ServerController controller = new ServerController(data, outputBuffer, inputBuffer, 320, 480);
			new Thread(network).start();
			Thread threadCtrl = new Thread(controller);
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			threadCtrl.start();
		}
	}
	
	private void show(){
    	Log.i("show", "outBuffer is " + outputBuffer.getSize());
    	Log.i("show", "inBuffer is " + inputBuffer.getSize());
    }
}
