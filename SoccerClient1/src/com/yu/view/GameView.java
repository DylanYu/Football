package com.yu.view;

import com.yu.overallsth.GameData;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;

public class GameView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {

	GameData data;
	// DrawThread dt;
	Canvas canvas;
	SurfaceHolder sholder;
	UpActivity father;
	//Thread thread;
	Paint paint;

	// ////context
	public GameView(UpActivity father, GameData data) {
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
		this.data = data;
		// dt = new DrawThread(this, this.getHolder());
	}

	public void draw() {
		try {
			canvas = sholder.lockCanvas();
			canvas.drawColor(Color.WHITE);
			canvas.drawCircle((float)data.getX(), (float)data.getY(), (float)data.getRadius(), paint);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (canvas != null)
				sholder.unlockCanvasAndPost(canvas);
		}
	}

	public void run() {
		while (true) {
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

}
