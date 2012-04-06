package com.yu.overallsth;

import com.yu.basicelements.Position;
import com.yu.basicelements.Speed;

/**
 * diary4/6:增加最高速度限制
 * @author hElo
 *
 */
public class Ball extends MovingObject{
//	private Position position;
//	private Speed speed;
//	private double angle;

	//限制
	private double maxSpeed = 20;
		
	public Ball() {
		super();
	}

	public Ball(Position position, Speed speed) {
		super(position, speed);
	}
	
	public void setBall(Ball ball){
		this.setMovingObject(ball);
	}
	
	/**
	 * 球加速，最高速被限制为maxSpeed
	 * @param ax
	 * @param ay
	 */
	public void makeAcc(double ax, double ay){
		double sx = this.getSpeed().getSpeedX() + ax;
		double sy = this.getSpeed().getSpeedY() + ay;
		double s = Math.pow((sx * sx + sy * sy), 0.5);
		if(s > this.maxSpeed){
			sx = sx / s * this.maxSpeed;
			sy = sy / s * this.maxSpeed;
		}
		this.setSpeed(sx, sy);
	}
	
	//TODO clone?
	public Ball clone(){
		MovingObject object = super.clone();
		return new Ball(object.getPosition(), object.getSpeed());
	}
	
	

}
