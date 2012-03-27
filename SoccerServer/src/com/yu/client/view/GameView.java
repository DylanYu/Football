package com.yu.client.view;

import com.yu.activities.UpClientActivity;
import com.yu.basicelements.Side;
import com.yu.overallsth.Pitch;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {

	Pitch pitch;
	// DrawThread dt;
	Canvas canvas;
	SurfaceHolder sholder;
	UpClientActivity father;
	//Thread thread;
	Paint paint;
	
	//
	boolean isRunning = true;

	// ////context
	public GameView(UpClientActivity father, Pitch pitch) {
		super(father);
		this.father = father;
		sholder = this.getHolder();
		getHolder().addCallback(this);
		//thread = new Thread(this);
		paint = new Paint();
		// paint.setAntiAlias(true);
		//paint.setColor(Color.BLUE);
//		this.x = data.getX();
//		this.y = data.getY();
//		this.angle = data.getAngle();
//		this.radius = data.getRadius();
		this.pitch = pitch;
		// dt = new DrawThread(this, this.getHolder());
	}

	public void draw() {
		try {
			canvas = sholder.lockCanvas();
			canvas.drawColor(Color.GREEN);
			//画ball
			paint.setColor(Color.WHITE);
			canvas.drawCircle((float)pitch.getBallPX(), (float)pitch.getBallPY(), 10, paint);
			//画left
			paint.setColor(Color.BLUE);
			for(int i = 0; i < pitch.getNumOfPlayer(); i ++)
				canvas.drawCircle((float)pitch.getPlayer(Side.LEFT, i).getPosition().getX(), (float)pitch.getPlayer(Side.LEFT, i).getPosition().getY(), 8, paint);
			//画right
			paint.setColor(Color.BLACK);
			for(int i = 0; i < pitch.getNumOfPlayer(); i ++)
				canvas.drawCircle((float)pitch.getPlayer(Side.RIGHT, i).getPosition().getX(), (float)pitch.getPlayer(Side.RIGHT, i).getPosition().getY(), 8, paint);
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
