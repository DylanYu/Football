package com.yu.activities;

import com.yu.basicelements.Side;
import com.yu.client.agent.AgentController;
import com.yu.client.agent.AgentOutputBuffer;
import com.yu.client.controller.CilentController;
import com.yu.client.network.ClientInputBuffer;
import com.yu.client.network.ClientNetwork;
import com.yu.client.network.ClientOutputBuffer;
import com.yu.client.view.GameView;
import com.yu.overallsth.Pitch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class UpClientActivity extends Activity {
	
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
	Pitch pitch = null;
	
	CilentController controller = null;
	Thread threadController = null;
	
	//Agent
	AgentOutputBuffer agentOutputBuffer;
	 AgentController agentController;
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
        		WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN
        		);
        //Server IP
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String ip = bundle.getString("ServerIP");
        int port = Integer.parseInt(bundle.getString("ServerPort"));
        double length = Double.parseDouble(bundle.getString("ScreenLength"));
        double width = Double.parseDouble(bundle.getString("ScreenWidth"));
        //
        pitch = new Pitch();
        //TODO delete
        pitch.initPitchRandomly();
        

        //buffer
        outputBuffer = new ClientOutputBuffer();
        inputBuffer = new ClientInputBuffer();

        //
        network = new ClientNetwork(ip, port, outputBuffer, inputBuffer);
        threadNetwork = new Thread(network);
        threadNetwork.start();
        
        //controller
        controller = new CilentController(pitch, outputBuffer, inputBuffer, 320, 480);
        //System.out.println(gameView.getWidth() + "  " + gameView.getHeight());
        threadController = new Thread(controller);
        threadController.start();
        //layout = (LinearLayout)findViewById(R.id.linearLayout);
        //layout.setOnTouchListener(onTouchListener);
        //gameView.startGame();
        
        //view
        
        gameView = new GameView(this, pitch, length, width);
        setContentView(gameView);
        threadGameView = new Thread(gameView);
        threadGameView.start();
        
        //TODO AgentController
        int numOfAgent = 2;
        agentOutputBuffer = new AgentOutputBuffer(numOfAgent);
        agentController = new AgentController(numOfAgent, Side.LEFT, pitch, agentOutputBuffer);
    }

    public boolean onTouchEvent(MotionEvent event){
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			controller.upOnce();
			show();
			break;
		default:
			break;
		}
		return false;
	}
    
    private void show(){
    	Log.i("show", "outBuffer is " + outputBuffer.getSize());
    	Log.i("show", "inBuffer is " + inputBuffer.getSize());
    }
    
    
    //TODO 并不调用onDestory
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
		
		agentController.stopAgentController();
		
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