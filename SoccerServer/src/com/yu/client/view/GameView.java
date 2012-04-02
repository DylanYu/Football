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
	Canvas canvas;
	SurfaceHolder sholder;
	UpClientActivity father;
	Paint paint;
	
	private double length;
	private double width;
	
	//
	boolean isRunning = true;

	// ////context
	public GameView(UpClientActivity father, Pitch pitch, double length,double width) {
		super(father);
		this.father = father;
		sholder = this.getHolder();
		getHolder().addCallback(this);
		paint = new Paint();
		
		//Size
		this.length = length;
		this.width = width;
		
		this.pitch = pitch;
	}

	public void draw() {
		double widthMultiple = this.width / pitch.getPitchWidth();
		double lengthMultiple = this.length / pitch.getPitchLength();
		double multiple = widthMultiple;
		try {
			canvas = sholder.lockCanvas();
			canvas.drawColor(Color.GREEN);
			//画ball
			paint.setColor(Color.WHITE);
			canvas.drawCircle((float)widthMultiple * (float)pitch.getBallPX(), (float)lengthMultiple * (float)pitch.getBallPY(), (float)multiple * (float)pitch.getBallRadius(), paint);
			//画left
			paint.setColor(Color.BLUE);
			for(int i = 0; i < pitch.getNumOfPlayer(); i ++)
				canvas.drawCircle((float)widthMultiple *(float)pitch.getPlayer(Side.LEFT, i).getPosition().getX(), (float)lengthMultiple * (float)pitch.getPlayer(Side.LEFT, i).getPosition().getY(), (float)multiple *(float)pitch.getPlayerRadius(), paint);
			//画right
			paint.setColor(Color.BLACK);
			for(int i = 0; i < pitch.getNumOfPlayer(); i ++)
				canvas.drawCircle((float)widthMultiple *(float)pitch.getPlayer(Side.RIGHT, i).getPosition().getX(), (float)lengthMultiple * (float)pitch.getPlayer(Side.RIGHT, i).getPosition().getY(), (float)multiple *(float)pitch.getPlayerRadius(), paint);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (canvas != null)
				sholder.unlockCanvasAndPost(canvas);
		}
	}

	public void run() {
		while (isRunning) {
				draw();
			try {
				//TODO 30fps
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	public void stopRunning(){
		isRunning = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {

		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		
	}

}
