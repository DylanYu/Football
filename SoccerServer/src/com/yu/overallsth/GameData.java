package com.yu.overallsth;

public class GameData {
	private double x;
	private double y;
	private double radius;
	private double angle;

	public GameData(double x, double y, double radius, double angle) {
		super();
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.angle = angle;
	}

	public void upY() {
		this.y += 30;
	}

	public void downY() {
		//this.y -= 5;
		this.y -= 3;
	}

	public void setPosition(double x, double y) {
		setX(x);
		setY(y);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}
}
