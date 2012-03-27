package com.yu.overallsth;

import com.yu.basicelements.Side;

public class Pitch {
	// TODO pitch 长宽
	private double pitchWidth = 320;
	private double pitchLength = 480;

	// TODO decay
	private double playerSpeedDecay = 0;
	private double ballSpeedDecay = 0.3;

	// not good
	private double maxPlayerSpeed = 10;
	private double maxBallSpeed = 20;

	private int numOfPlayer = 11;
	private Player[] player0;
	private Player[] player1;
	private Ball ball;

	// GameState
	private int score0 = 0;
	private int score1 = 0;

	public Pitch() {
		// this.numOfPlayer = numOfPlayer;
		player0 = new Player[numOfPlayer];
		player1 = new Player[numOfPlayer];
		for (int i = 0; i < numOfPlayer; i++) {
			player0[i] = new Player();
			player1[i] = new Player();
		}
		ball = new Ball();
	}

	public void initLeftRandomly() {
		for (int i = 0; i < numOfPlayer; i++) {
			int signX = (Math.random() >= 0.5) ? -1 : 1;
			int signY = (Math.random() >= 0.5) ? -1 : 1;
			double px = Math.random() * pitchWidth;
			double py = Math.random() * pitchLength / 2;
			double speedX = Math.random() * maxPlayerSpeed * signX;
			double speedY = Math.random() * maxPlayerSpeed * signY;
			player0[i].setPosition(px, py);
			player0[i].setSpeed(speedX, speedY);
		}
	}
	public void initRightRandomly(){
		for (int i = 0; i < numOfPlayer; i++) {
			int signX = (Math.random() >= 0.5) ? -1 : 1;
			int signY = (Math.random() >= 0.5) ? -1 : 1;
			double px = Math.random() * pitchWidth;
			double py = Math.random() * pitchLength / 2 + pitchLength / 2;
			double speedX = Math.random() * maxPlayerSpeed * signX;
			double speedY = Math.random() * maxPlayerSpeed * signY;
			player1[i].setPosition(px, py);
			player1[i].setSpeed(speedX, speedY);
		}
	}
	
	public void initBallRandomly(){
		int signX = (Math.random() >= 0.5) ? -1 : 1;
		int signY = (Math.random() >= 0.5) ? -1 : 1;
		double speedX = Math.random() * maxBallSpeed * signX;
		double speedY = Math.random() * maxBallSpeed * signY;
		ball.setPosition(pitchWidth / 2, pitchLength / 2);
		ball.setSpeed(speedX, speedY);
	}
	
	public void initPitchRandomly(){
		initLeftRandomly();
		initRightRandomly();
		initBallRandomly();
	}

	public int getScore0() {
		return score0;
	}

	public int getScore1() {
		return score1;
	}

	public void calAllObjectsNextCycle() {
		calBallNextCycle();
		for (int i = 0; i < numOfPlayer; i++) {
			calPlayerNextCycle(Side.LEFT, i);
			calPlayerNextCycle(Side.RIGHT, i);
		}
	}

	public void changePlayerSpeedRamdomly(Side side) {
		int unum = -1;
		unum = (int) Math.floor(Math.random() * 11);
		double speedX = Math.random() * 10;
		double speedY = Math.random() * 10;
		if (side == Side.LEFT) {
			player0[unum].setSpeed(speedX, speedY);
		} else if (side == Side.RIGHT) {
			player1[unum].setSpeed(speedX, speedY);
		}
	}

	/******************************************************
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Ball
	 * 
	 * 
	 * 
	 * 
	 * 
	 *********************************************************/

	public void setBall(Ball ball) {
		this.ball.setBall(ball);
	}

	public void calBallNextCycle() {
		calMovingObjectNextCycle(this.ball);
	}

	/************************************************
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Common
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 ************************************************/

	private boolean isInsidePitch(MovingObject object) {
		double x = object.getPosition().getX();
		double y = object.getPosition().getY();
		if ((x >= 0 && x <= pitchWidth) && (y >= 0 && y <= pitchLength))
			return true;
		else
			return false;
	}

	private void speedDecay(MovingObject object) {
		double speedX = object.getSpeed().getSpeedX();
		double speedY = object.getSpeed().getSpeedY();
		if (speedX == 0 && speedY == 0)
			return;
		//
		double signX;
		double signY;
		if(speedX > 0)
			signX = 1;
		else signX = -1;
		if(speedY > 0)
			signY = 1;
		else signY = -1;
		// 已经停下
		
		double speedXNext = 0;
		double speedYNext = 0;
		double speedNext = 0;
		if (object.getClass() == Ball.class) {
			if(ballSpeedDecay == 0)
				 return;
			speedNext = Math.pow((speedX * speedX + speedY * speedY), 0.5) - ballSpeedDecay;
			if(speedNext < 0){
				speedNext = 0;
				object.setSpeed(0, 0);
				return;
			}
				
		} else if (object.getClass() == Player.class) {
			 if(playerSpeedDecay == 0)
			 return;
			speedNext = Math.pow((speedX * speedX + speedY * speedY), 0.5)- playerSpeedDecay;
			if(speedNext < 0){
				speedNext = 0;
				object.setSpeed(0, 0);
				return;
			}
		}
		speedXNext = signX * Math.pow((speedNext * speedNext * speedX * speedX)
				/ (speedX * speedX + speedY * speedY), 0.5);
		speedYNext = signY * Math.pow((speedNext * speedNext * speedY * speedY)
				/ (speedX * speedX + speedY * speedY), 0.5);

		object.setSpeed(speedXNext, speedYNext);
	}

	/**
	 * 如果移动物体将要出现在场地之外，则进行镜面反射将其送回场内
	 * 
	 * @param object
	 */
	private void amendMovingObject(MovingObject object) {
		double px;
		double py;
		double speedX;
		double speedY;
		while (!isInsidePitch(object)) {
			px = object.getPosition().getX();
			py = object.getPosition().getY();
			speedX = object.getSpeed().getSpeedX();
			speedY = object.getSpeed().getSpeedY();
			if (px < 0) {
				px = -px;
				speedX = -speedX;
			} else if (px > pitchWidth) {
				px = pitchWidth * 2 - px;
				speedX = -speedX;
			}
			if (py < 0) {
				py = -py;
				speedY = -speedY;
			} else if (py > pitchLength) {
				py = pitchLength * 2 - py;
				speedY = -speedY;
			}
			object.setPosition(px, py);
			object.setSpeed(speedX, speedY);
		}
	}

	private void calMovingObjectNextCycle(MovingObject object) {
		double nextPX;
		double nextPY;
		nextPX = object.getPosition().getX() + object.getSpeed().getSpeedX();
		nextPY = object.getPosition().getY() + object.getSpeed().getSpeedY();
		object.setPosition(nextPX, nextPY);
		// 修正物体位置和速度
		amendMovingObject(object);
		// 减速
		speedDecay(object);
	}

	/***************************************************
	 * 
	 * 
	 * 
	 * 
	 * Player
	 * 
	 * 
	 * 
	 * 
	 * 
	 **************************************************/

	public void calPlayerNextCycle(Side side, int unum) {
		Player player;
		if (unum >= 0 && unum <= 10) {
			if (side == Side.LEFT) {
				player = player0[unum];
				calMovingObjectNextCycle(player);
			} else if (side == Side.RIGHT) {
				player = player1[unum];
				calMovingObjectNextCycle(player);
			}
		} else
			System.out.println("Pitch::calPlayerNextCycle() Error");
	}

	public void setPlayerPosition(Side side, int unum, double px, double py) {
		if (unum >= 0 && unum <= 10) {
			if (side == Side.LEFT) {
				player0[unum].setPosition(px, py);
			} else if (side == Side.RIGHT) {
				player1[unum].setPosition(px, py);
			}
		} else
			System.out.println("Pitch::setPlayerPosition() Error");
	}

	public void setPlayerSpeed(Side side, int unum, double speedX, double speedY) {
		if (unum >= 0 && unum <= 10) {
			if (side == Side.LEFT) {
				player0[unum].setSpeed(speedX, speedY);
			} else if (side == Side.RIGHT) {
				player1[unum].setSpeed(speedX, speedY);
			}
		} else
			System.out.println("Pitch::setPlayerSpeed() Error");
	}

	public void setPlayer(Side side, int unum, Player player) {
		if (side == Side.LEFT) {
			player0[unum].setPlayer(player);
		} else if (side == Side.RIGHT) {
			player1[unum].setPlayer(player);
		} else
			System.out.println("Pitch::setPlayer() Error");
	}

	public Player getPlayer(Side side, int unum) {
		if (side == Side.LEFT) {
			if (unum >= 0 && unum <= 10)
				return player0[unum];
		} else if (side == Side.RIGHT) {
			if (unum >= 0 && unum <= 10)
				return player1[unum];
		}
		return null;
	}

	public Player[] getAllPlayer(Side side) {
		if (side == Side.LEFT)
			return player0;
		else
			return player1;
	}

	public double getBallPX() {
		return this.ball.getPosition().getX();
	}

	public double getBallPY() {
		return this.ball.getPosition().getY();
	}

	public double getBallSpeedX() {
		return this.ball.getSpeed().getSpeedX();
	}

	public double getBallSpeedY() {
		return this.ball.getSpeed().getSpeedY();
	}

	public int getNumOfPlayer() {
		return this.numOfPlayer;
	}

}
