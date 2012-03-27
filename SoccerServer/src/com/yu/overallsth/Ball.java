package com.yu.overallsth;

import com.yu.basicelements.Position;
import com.yu.basicelements.Speed;

public class Ball extends MovingObject{
//	private Position position;
//	private Speed speed;
//	private double angle;

	public Ball() {
		super();
	}

	public Ball(Position position, Speed speed) {
		super(position, speed);
	}
	
	public void setBall(Ball ball){
		this.setMovingObject(ball);
	}
	
	
	
	

}
