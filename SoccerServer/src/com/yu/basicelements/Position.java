package com.yu.basicelements;

public class Position {
	double px;
	double py;

	public Position() {
		px = -1;
		py = -1;
	}

	public Position(Position p){
		this.px = p.getX();
		this.py = p.getY();
	}
	
	public Position(double px, double py) {
		this.px = px;
		this.py = py;
	}

	public void setPosition(double px, double py) {
		this.px = px;
		this.py = py;
	}

	public double getX() {
		return px;
	}

	public void setX(double px) {
		this.px = px;
	}

	public double getY() {
		return py;
	}

	public void setY(double py) {
		this.py = py;
	}
}
