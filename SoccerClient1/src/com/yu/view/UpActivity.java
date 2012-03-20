package com.yu.view;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;

import com.yu.controller.CilentController;
import com.yu.network.ClientInputBuffer;
import com.yu.network.ClientNetwork;
import com.yu.network.ClientOutputBuffer;
import com.yu.overallsth.GameData;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class UpActivity extends Activity {
	
	//net
	ClientNetwork netWork = null;
	Thread netWorkThread = null;
	//buffer
	ClientInputBuffer inputBuffer = null;
	ClientOutputBuffer outputBuffer = null;
	//view
	LinearLayout layout = null;
	GameView gameView ; 
    
	GameData data = null;
	
	CilentController controller = null;
	Thread controllerThread = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
        		WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN
        		);
        //
        data = new GameData(100,200,20,45);
        

        //buffer
        outputBuffer = new ClientOutputBuffer();
        inputBuffer = new ClientInputBuffer();

        //
        netWork = new ClientNetwork(outputBuffer, inputBuffer);
        netWorkThread = new Thread(netWork);
        netWorkThread.start();
        
        //controller
        controller = new CilentController(data, outputBuffer, inputBuffer, 320, 480);
        //System.out.println(gameView.getWidth() + "  " + gameView.getHeight());
        controllerThread = new Thread(controller);
        controllerThread.start();
        //layout = (LinearLayout)findViewById(R.id.linearLayout);
        //layout.setOnTouchListener(onTouchListener);
        //gameView.startGame();
        
        //view
        gameView = new GameView(this,data);
        setContentView(gameView);
        new Thread(gameView).start();
    }

    public boolean onTouchEvent(MotionEvent event){
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			controller.upOnce();
			show();
			break;
//		case MotionEvent.ACTION_UP:
//			controller.handleAClick(event.getX(), event.getY(), 20);
//			break;
		default:
			break;
		}
		return false;
	}
    
    private void show(){
    	Log.i("show", "outBuffer is " + outputBuffer.getSize());
    	Log.i("show", "inBuffer is " + inputBuffer.getSize());
    }
}