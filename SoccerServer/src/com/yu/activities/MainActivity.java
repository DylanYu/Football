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
	EditText editServerIP;
	EditText editServerPort;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		btnStartServer = (Button)findViewById(R.id.btnStartServer);
		btnStopServer = (Button)findViewById(R.id.btnStopServer);
		btnClient = (Button)findViewById(R.id.btnClient);
		editListeningPort = (EditText)findViewById(R.id.editListeningPort);
		editServerIP = (EditText)findViewById(R.id.editServerIP);
		editServerPort = (EditText)findViewById(R.id.editServerPort);
		btnStartServer.setOnClickListener(new startServer());
		btnStopServer.setOnClickListener(new stopServer());
		btnClient.setOnClickListener(new startClient());
	}
	
	class startServer implements OnClickListener {
		@Override
		public void onClick(View v) {
			String port = editListeningPort.getText().toString();
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, ServerNetworkService.class);
			intent.putExtra("ListeningPort", port);
			//TODO startServiceForResult
			startService(intent);
		}
	}
	
	//TODO 貌似无效
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
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, UpClientActivity.class);
			intent.putExtra("ServerIP", IP);
			intent.putExtra("ServerPort", port);
			startActivity(intent);
		}
		
	}
}
