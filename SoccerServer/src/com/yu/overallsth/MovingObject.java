package com.yu.overallsth;

import com.yu.basicelements.Position;
import com.yu.basicelements.Speed;

public class MovingObject implements Cloneable{
	private Position position;
	private Speed speed;

	public MovingObject() {
		this.position = new Position(0, 0);
		this.speed = new Speed(0, 0);
	}

	public MovingObject(Position position, Speed speed) {
		this.position = new Position(position);
		this.speed = new Speed(speed);
	}
	
	public void setMovingObject(MovingObject object){
		this.setPosition(object.getPosition());
		this.setSpeed(object.getSpeed());
	}

	public Position getPosition() {
		return position;
	}

	/**
	 * java的值传递问题
	 * @param position
	 */
	public void setPosition(Position position) {
		this.position.setPosition(position.getX(), position.getY());
	}
	
	public void setPosition(double px, double py) {
		this.position.setPosition(px, py);
	}

	public Speed getSpeed() {
		return speed;
	}

	public void setSpeed(Speed speed) {
		this.speed.setSpeed(speed.getSpeedX(), speed.getSpeedY());
	}
	
	public void setSpeed(double speedX, double speedY) {
		this.speed.setSpeed(speedX, speedY);
	}
	
	public MovingObject clone(){
		try {
			super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		Position p = new Position(this.position);
		Speed s = new Speed(this.speed);
		return new MovingObject(p, s);
	}

}
