package com.yu.basicelements;

public class Speed {
	double speedX;
	double speedY;

	public Speed() {
		super();
	}

	public Speed(Speed s){
		this.speedX = s.getSpeedX();
		this.speedY = s.getSpeedY();
	}
	
	public Speed(double speedX, double speedY) {
		super();
		this.speedX = speedX;
		this.speedY = speedY;
	}

	public void setSpeed(double speedX, double speedY) {
		this.speedX = speedX;
		this.speedY = speedY;
	}

	public double getSpeedX() {
		return speedX;
	}

	public void setSpeedX(double speedX) {
		this.speedX = speedX;
	}

	public double getSpeedY() {
		return speedY;
	}

	public void setSpeedY(double speedY) {
		this.speedY = speedY;
	}
}
