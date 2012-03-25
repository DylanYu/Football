package com.yu.client.view;

import com.yu.activities.UpClientActivity;
import com.yu.overallsth.GameData;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {

	GameData data0;
	GameData data1;
	// DrawThread dt;
	Canvas canvas;
	SurfaceHolder sholder;
	UpClientActivity father;
	//Thread thread;
	Paint paint;
	
	//
	boolean isRunning = true;

	// ////context
	public GameView(UpClientActivity father, GameData data0, GameData data1) {
		super(father);
		this.father = father;
		sholder = this.getHolder();
		getHolder().addCallback(this);
		//thread = new Thread(this);
		paint = new Paint();
		// paint.setAntiAlias(true);
		paint.setColor(Color.BLUE);
//		this.x = data.getX();
//		this.y = data.getY();
//		this.angle = data.getAngle();
//		this.radius = data.getRadius();
		this.data0 = data0;
		this.data1 = data1;
		// dt = new DrawThread(this, this.getHolder());
	}

	public void draw() {
		try {
			canvas = sholder.lockCanvas();
			canvas.drawColor(Color.WHITE);
			canvas.drawCircle((float)data0.getX(), (float)data0.getY(), (float)data0.getRadius(), paint);
			canvas.drawCircle((float)data1.getX(), (float)data1.getY(), (float)data1.getRadius(), paint);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (canvas != null)
				sholder.unlockCanvasAndPost(canvas);
		}
	}

	public void run() {
		while (isRunning) {
			//synchronized(data){
				draw();
			//}
			try {
				//TODO 30fps
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		// if(!dt.isAlive()){
		// dt.start();
		// }
		//thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// if(dt.isAlive()){
		// dt.flag = false;
		// }

	}
	
	public void stopRunning(){
		isRunning = false;
	}

}
