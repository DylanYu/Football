package com.yu.basicelements;

public class Acceleration {
	double ax;
	double ay;
	public Acceleration() {
		super();
	}
	
	public Acceleration(Acceleration a){
		this.ax = a.getAx();
		this.ay = a.getAy();
	}
	public Acceleration(double ax, double ay) {
		super();
		this.ax = ax;
		this.ay = ay;
	}
	public void setAcceleration(double ax, double ay){
		this.ax = ax;
		this.ay = ay;
	}
	public double getAx() {
		return ax;
	}
	public void setAx(double ax) {
		this.ax = ax;
	}
	public double getAy() {
		return ay;
	}
	public void setAy(double ay) {
		this.ay = ay;
	}
}
