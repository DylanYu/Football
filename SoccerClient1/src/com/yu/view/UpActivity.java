package com.yu.view;

import com.yu.controller.CilentController;
import com.yu.network.ClientInputBuffer;
import com.yu.network.ClientNetwork;
import com.yu.network.ClientOutputBuffer;
import com.yu.overallsth.GameData;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class UpActivity extends Activity {
	
	//net
	ClientNetwork network = null;
	Thread threadNetwork = null;
	//buffer
	ClientInputBuffer inputBuffer = null;
	ClientOutputBuffer outputBuffer = null;
	//view
	LinearLayout layout = null;
	GameView gameView ; 
    Thread threadGameView = null;
	GameData data = null;
	
	CilentController controller = null;
	Thread threadController = null;
	
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
        network = new ClientNetwork(outputBuffer, inputBuffer);
        threadNetwork = new Thread(network);
        threadNetwork.start();
        
        //controller
        controller = new CilentController(data, outputBuffer, inputBuffer, 320, 480);
        //System.out.println(gameView.getWidth() + "  " + gameView.getHeight());
        threadController = new Thread(controller);
        threadController.start();
        //layout = (LinearLayout)findViewById(R.id.linearLayout);
        //layout.setOnTouchListener(onTouchListener);
        //gameView.startGame();
        
        //view
        gameView = new GameView(this,data);
        setContentView(gameView);
        threadGameView = new Thread(gameView);
        threadGameView.start();
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
    
    
    //TODO
    /**
     *
     * 初步的实验证明后台仍有线程运行时按返回键似乎并不调用onDestory，这是什么原因？
     */
    protected void onDestory(){
		super.onDestroy();
		
	}
    
    /**
     * 停止并显示后台线程们的状态
     */
    private void stop() {
    	network.stopClientNetwork();
		controller.stopRunning();
		gameView.stopRunning();
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("threadNetwork:" + threadNetwork.isAlive());
		network.showSubThreadStatus();
		System.out.println("threadController:" + threadController.isAlive());
		System.out.println("threadGameView:" + threadGameView.isAlive());
    }
    
    /**
     * 按“返回”键后退出后台线程（这很重要）
     */
    protected void onPause(){
		super.onPause();
		stop();
	}
}