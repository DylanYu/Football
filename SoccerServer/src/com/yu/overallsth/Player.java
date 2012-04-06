/**
 * 
 */
package com.yu.overallsth;

import com.yu.basicelements.Acceleration;
import com.yu.basicelements.Position;
import com.yu.basicelements.Side;
import com.yu.basicelements.Speed;

/**
 * diary4/6:在makeAcc中加入最高速度限制
 * 
 * 
 * 
 * @author hElo
 *
 */
public class Player extends MovingObject{
	private Side side;
	private int NO;
//	private Position position;
//	private Speed speed;
	private  Acceleration acceleration;
	
	//reserved
	private double angle;
//	private double neckAngle;
	private double stamina;
	
	//限制
	private double maxSpeed = 8;
	
	
	
	public Player() {
		super();
		this.side = Side.LEFT;
		this.acceleration = new Acceleration(0, 0);
		this.angle = 0;
		this.stamina = 0;
	}
	
	public Player(Side side, int NO, Position position, Speed speed, Acceleration acceleration, double angle, double stamina) {
		super(position, speed);
		this.side = side;
		this.NO = NO;
		this.acceleration = new Acceleration(acceleration);
		this.angle = angle;
		this.stamina = stamina;
	}
	
	public void setPlayer(Player player){
		this.setMovingObject(player);
		this.acceleration.setAcceleration(player.getAcceleration().getAx(), player.getAcceleration().getAy());
		this.angle = player.getAngle();
		this.stamina = player.getStamina();
	}

	/**
	 * 球员加速，最高速被限制为maxSpeed
	 * @param ax
	 * @param ay
	 */
	public void makeAcc(double ax, double ay){
		double sx = this.getSpeed().getSpeedX() + ax;
		double sy = this.getSpeed().getSpeedY() + ay;
		double s = Math.pow((sx * sx + sy * sy), 0.5);
		if(s > this.maxSpeed){
			//x = this.maxSpeed;
			sx = sx / s * this.maxSpeed;
			sy = sy / s * this.maxSpeed;
		}
		this.setSpeed(sx, sy);
	}
	
	public Acceleration getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Acceleration acceleration) {
		this.acceleration.setAx(acceleration.getAx());
		this.acceleration.setAx(acceleration.getAy());
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public double getStamina() {
		return stamina;
	}

	public void setStamina(double stamina) {
		this.stamina = stamina;
	}
	public int getNO() {
		return NO;
	}

	public void setNO(int NO) {
		this.NO = NO;
	}
	public Side getSide() {
		return side;
	}

	public void setSide(Side side) {
		this.side = side;
	}
	
	//TODO clone
	public Player clone(){
		super.clone();
		Player player = new Player();
		player.setSide(this.side);
		player.setNO(this.NO);
		player.setPosition(this.getPosition());
		player.setSpeed(this.getSpeed());
		player.setAcceleration(this.acceleration);
		player.setAngle(this.angle);
		player.setStamina(this.stamina);
		return player;
	}
}
