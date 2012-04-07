package com.yu.client.view;

import com.yu.activities.UpClientActivity;
import com.yu.basicelements.Side;
import com.yu.overallsth.Pitch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * diary4/7:画上了各类线和弧
 * 
 * @author hElo
 * 
 */
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

	/*
	 * 
	 */
	int pointRadius = 2;
	int cornerRadius = 4;
	
	
	
	double pitchWidth;
	double pitchLength;
	double widthMultiple;
	double lengthMultiple;
	double multiple;
	float centerX;
	float bottom;
	float yard;
	float leftGoalX;
	float rightGoalX;
	float leftGoalAreaX ;
	float rightGoalAreaX;
	float leftPenaltyBoxX;
	float rightPenaltyBoxX ;
	float penaltyBoxY0;
	float goalAreaY0;
	float penaltyPointY0;
	float penaltyBoxY1;
	float goalAreaY1;
	float penaltyPointY1;
	
	float []left_top = new float[2];
	float []right_top = new float[2];
	float []left_bottom = new float[2];
	float []right_bottom = new float[2];
	float []left_center = new float[2];
	float []right_center = new float[2];
	float []whole_center = new float[2];

	// ////context
	public GameView(UpClientActivity father, Pitch pitch, double length,
			double width) {
		super(father);
		this.father = father;
		sholder = this.getHolder();
		getHolder().addCallback(this);
		paint = new Paint();
		// Size
		this.length = length;
		this.width = width;
		this.pitch = pitch;
		//
		/*
		 * 
		 */
		pitchWidth = pitch.getPitchWidth();
		pitchLength = pitch.getPitchLength();
		widthMultiple = this.width / pitch.getPitchWidth();
		lengthMultiple = this.length / pitch.getPitchLength();
		multiple = widthMultiple;
		centerX = (float) (pitchWidth / 2);
		bottom = (float) (pitchLength * lengthMultiple);
		yard = (float) 0.915;
		leftGoalX = (float) ((centerX - 8 * yard) * widthMultiple);
		rightGoalX = (float) ((centerX + 8 * yard) * widthMultiple);
		leftGoalAreaX = (float) ((centerX - 10 * yard) * widthMultiple);
		rightGoalAreaX = (float) ((centerX + 10 * yard) * widthMultiple);
		leftPenaltyBoxX = (float) ((centerX - 22 * yard) * widthMultiple);
		rightPenaltyBoxX = (float) ((centerX + 22 * yard) * widthMultiple);
		penaltyBoxY0 = (float) ((0 + 18 * yard) * lengthMultiple);
		goalAreaY0 = (float) ((0 + 6 * yard) * lengthMultiple);
		penaltyPointY0 = (float) ((0 + 12 * yard) * lengthMultiple);
		penaltyBoxY1 = (float) ((pitchLength - 18 * yard) * lengthMultiple);
		goalAreaY1 = (float) ((pitchLength - 6 * yard) * lengthMultiple);
		penaltyPointY1 = (float) ((pitchLength - 12 * yard) * lengthMultiple);
		
		left_top[0] = 0;
		left_top[1] = 0;
		right_top[0] = (float) (pitchWidth * widthMultiple);
		right_top[1] = 0;
		left_bottom[0] =0;
		left_bottom[1] = (float) (pitchLength * lengthMultiple);
		right_bottom[0] = right_top[0];
		right_bottom[1] = left_bottom[1];
		left_center[0] = 0;
		left_center[1] = (left_top[1] + left_bottom[1]) / 2;
		right_center[0] = right_top[0];
		right_center[1] = (right_top[1] + right_bottom[1]) / 2;
		whole_center[0] = (left_top[0] + right_top[0]) / 2;
		whole_center[1] = left_center[1];
	}

	public void draw() {

		try {
			canvas = sholder.lockCanvas();
			canvas.drawColor(Color.GREEN);
			/*
			 * 画球场
			 */
			paint.setColor(Color.WHITE);
			canvas.drawLine(this.left_top[0], this.left_top[1], this.right_top[0], this.right_top[1], paint);
			canvas.drawLine(this.right_top[0], this.right_top[1], this.right_bottom[0], this.right_bottom[1], paint);
			canvas.drawLine(this.right_bottom[0], this.right_bottom[1], this.left_bottom[0], this.left_bottom[1], paint);
			canvas.drawLine(this.left_bottom[0], this.left_bottom[1], this.left_top[0], this.left_top[1], paint);
			canvas.drawLine(this.left_center[0], this.left_center[1], this.right_center[0], this.right_center[1], paint);
			canvas.drawCircle(this.whole_center[0], this.whole_center[1], 2, paint);
			
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawCircle(this.whole_center[0], this.whole_center[1], (float) (9.15 * yard * widthMultiple), paint);
			canvas.drawCircle(this.left_top[0], this.left_top[1], cornerRadius, paint);
			canvas.drawCircle(this.right_top[0], this.right_top[1], cornerRadius, paint);
			canvas.drawCircle(this.right_bottom[0], this.right_bottom[1], cornerRadius, paint);
			canvas.drawCircle(this.left_bottom[0], this.left_bottom[1], cornerRadius, paint);
			
			paint.setStyle(Paint.Style.FILL);
			
			canvas.drawCircle(leftGoalX, 0, pointRadius, paint);
			canvas.drawCircle(rightGoalX, 0, pointRadius, paint);
			//canvas.drawLine(leftGoalX, 0, rightGoalX, 0, paint);

			canvas.drawLine(leftGoalAreaX, 0, leftGoalAreaX, goalAreaY0, paint);
			canvas.drawLine(rightGoalAreaX, 0, rightGoalAreaX, goalAreaY0,
					paint);
			canvas.drawLine(leftGoalAreaX, goalAreaY0, rightGoalAreaX,
					goalAreaY0, paint);

			canvas.drawLine(leftPenaltyBoxX, 0, leftPenaltyBoxX, penaltyBoxY0,
					paint);
			canvas.drawLine(rightPenaltyBoxX, 0, rightPenaltyBoxX,
					penaltyBoxY0, paint);
			canvas.drawLine(leftPenaltyBoxX, penaltyBoxY0, rightPenaltyBoxX,
					penaltyBoxY0, paint);

			canvas.drawCircle((float) (centerX * widthMultiple),
					penaltyPointY0, pointRadius, paint);
			// STROKE
			paint.setStyle(Paint.Style.STROKE);
			RectF rect0 = new RectF(
					(int) ((centerX - 10 * yard) * widthMultiple),
					(int) (2 * yard * lengthMultiple),
					(int) ((centerX + 10 * yard) * widthMultiple),
					(int) (22 * yard * lengthMultiple));
			canvas.drawArc(rect0,
					(float) (Math.acos(4.0 / 5.0) / Math.PI * 180),
					(float) (180 - 2 * Math.acos(4.0 / 5.0) / Math.PI * 180),
					false, paint);

			RectF rect1 = new RectF(
					(int) ((centerX - 10 * yard) * widthMultiple),
					(int) ((pitchLength - 22 * yard) * lengthMultiple),
					(int) ((centerX + 10 * yard) * widthMultiple),
					(int) ((pitchLength - 2 * yard) * lengthMultiple));
			canvas.drawArc(rect1, (float) (180 + Math.acos(4.0 / 5.0) / Math.PI
					* 180), (float) (180 - 2 * Math.acos(4.0 / 5.0) / Math.PI
					* 180), false, paint);
			// FILL
			paint.setStyle(Paint.Style.FILL);
			canvas.drawCircle(leftGoalX, bottom, pointRadius, paint);
			canvas.drawCircle(rightGoalX, bottom, pointRadius, paint);
			//canvas.drawLine(leftGoalX, bottom - 1, rightGoalX, bottom - 1,paint);

			canvas.drawLine(leftGoalAreaX, bottom, leftGoalAreaX, goalAreaY1,
					paint);
			canvas.drawLine(rightGoalAreaX, bottom, rightGoalAreaX, goalAreaY1,
					paint);
			canvas.drawLine(leftGoalAreaX, goalAreaY1, rightGoalAreaX,
					goalAreaY1, paint);

			canvas.drawLine(leftPenaltyBoxX, bottom, leftPenaltyBoxX,
					penaltyBoxY1, paint);
			canvas.drawLine(rightPenaltyBoxX, bottom, rightPenaltyBoxX,
					penaltyBoxY1, paint);
			canvas.drawLine(leftPenaltyBoxX, penaltyBoxY1, rightPenaltyBoxX,
					penaltyBoxY1, paint);

			canvas.drawCircle((float) (centerX * widthMultiple),
					penaltyPointY1, 2, paint);

			// ////////////

			// 画left
			paint.setColor(Color.BLUE);
			for (int i = 0; i < pitch.getNumOfPlayer(); i++)
				canvas.drawCircle((float) widthMultiple
						* (float) pitch.getPlayer(Side.LEFT, i).getPosition()
								.getX(), (float) lengthMultiple
						* (float) pitch.getPlayer(Side.LEFT, i).getPosition()
								.getY(),
						(float) multiple * (float) pitch.getPlayerRadius(),
						paint);
			// 画right
			paint.setColor(Color.BLACK);
			for (int i = 0; i < pitch.getNumOfPlayer(); i++)
				canvas.drawCircle((float) widthMultiple
						* (float) pitch.getPlayer(Side.RIGHT, i).getPosition()
								.getX(), (float) lengthMultiple
						* (float) pitch.getPlayer(Side.RIGHT, i).getPosition()
								.getY(),
						(float) multiple * (float) pitch.getPlayerRadius(),
						paint);

			// 画ball
			paint.setColor(Color.YELLOW);
			canvas.drawCircle(
					(float) widthMultiple * (float) pitch.getBallPX(),
					(float) lengthMultiple * (float) pitch.getBallPY(),
					(float) multiple * (float) pitch.getBallRadius(), paint);

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
				// TODO 30fps
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void stopRunning() {
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
