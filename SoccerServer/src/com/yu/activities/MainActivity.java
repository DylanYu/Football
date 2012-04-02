package com.yu.activities;

import com.yu.R;
import com.yu.server.network.ServerNetworkService;

import android.app.Activity;	
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	Button btnStartServer;
	Button btnStopServer;
	Button btnClient;
	
	EditText editListeningPort;
	EditText editFrequency;
	EditText editServerIP;
	EditText editServerPort;
	EditText editScreenLength;
	EditText editScreenWidth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		btnStartServer = (Button)findViewById(R.id.btnStartServer);
		btnStopServer = (Button)findViewById(R.id.btnStopServer);
		btnClient = (Button)findViewById(R.id.btnClient);
		editListeningPort = (EditText)findViewById(R.id.editListeningPort);
		editFrequency = (EditText)findViewById(R.id.editFrequency);
		editServerIP = (EditText)findViewById(R.id.editServerIP);
		editServerPort = (EditText)findViewById(R.id.editServerPort);
		editScreenLength = (EditText)findViewById(R.id.editScreenLength);
		editScreenWidth = (EditText)findViewById(R.id.editScreenWidth);
		btnStartServer.setOnClickListener(new startServer());
		btnStopServer.setOnClickListener(new stopServer());
		btnClient.setOnClickListener(new startClient());
	}
	
	class startServer implements OnClickListener {
		@Override
		public void onClick(View v) {
			String port = editListeningPort.getText().toString();
			String frequency = editFrequency.getText().toString();
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, ServerNetworkService.class);
			intent.putExtra("ListeningPort", port);
			intent.putExtra("Frequency", frequency);
			//TODO startServiceForResult
			startService(intent);
		}
	}
	
	class stopServer implements OnClickListener {
		public void onClick(View V){
			Intent intent= new Intent();
			intent.setClass(MainActivity.this, ServerNetworkService.class);
			stopService(intent);
		}
	}
	
	class startClient implements OnClickListener {
		@Override
		public void onClick(View v) {
			String IP = editServerIP.getText().toString();
			String port = editServerPort.getText().toString();
			String length = editScreenLength.getText().toString();
			String width = editScreenWidth.getText().toString();
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, UpClientActivity.class);
			intent.putExtra("ServerIP", IP);
			intent.putExtra("ServerPort", port);
			intent.putExtra("ScreenLength", length);
			intent.putExtra("ScreenWidth", width);
			startActivity(intent);
		}
		
	}
}
