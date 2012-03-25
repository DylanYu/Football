package com.yu.activities;

import com.yu.R;

import android.app.Activity;	
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	Button btnServer;
	Button btnClient;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		btnServer = (Button)findViewById(R.id.btnServer);
		btnClient = (Button)findViewById(R.id.btnClient);
		btnServer.setOnClickListener(new startServer());
		btnClient.setOnClickListener(new startClient());
	}
	
	class startServer implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, UpServerActivity.class);
			startActivity(intent);
		}
	}
	
	class startClient implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, UpClientActivity.class);
			startActivity(intent);
		}
		
	}
}
