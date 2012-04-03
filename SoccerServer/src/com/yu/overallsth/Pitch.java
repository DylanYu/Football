package com.yu.overallsth;

import com.yu.basicelements.Side;
import com.yu.client.agent.WorldState;

//TODO BIG THING
/**
 * 最后需要将其切割为服务端和客户端分别使用的pitch
 * @author hElo
 *
 */
public class Pitch {
	
	
	//TODO
	private long gameTime;
	
	
	//pitch 长宽
	private double pitchWidth = 58;
	private double pitchLength = 105;

	//各类尺寸
	private double playerRadius = 0.6 * 2;
	

	private double ballRadius = 0.3 * 2;
	
	private double playerSpeedDecay = 0.5;
	private double ballSpeedDecay = 0;
	
	private double ballHitPlayerDecay = 0.8;
	private double playerCrashDecay = 0.5;

	// not good
	private double maxPlayerSpeed = 5;
	private double maxBallSpeed = 5;

	private int numOfPlayer = 3;
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
			player0[i].setNO(i);
			player0[i].setSide(Side.LEFT);
			player1[i] = new Player();
			player1[i].setNO(i);
			player1[i].setSide(Side.RIGHT);
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

	public void calAllObjectsNextCycle() {
		calBallNextCycle();
		for (int i = 0; i < numOfPlayer; i++) {
			calPlayerNextCycle(Side.LEFT, i);
			calPlayerNextCycle(Side.RIGHT, i);
		}
		
		/**
		 * 修正撞击事件
		 */
		amendPlayerCrash();
		amendBallHit();
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

	/**
	 * 修正球员之间发生撞击后各自的位置和速度
	 */
	private void amendPlayerCrash(){
		for(int i = 0; i< numOfPlayer; i++){
			amendPlayerCrashed(player0[i]);
			amendPlayerCrashed(player1[i]);
		}
	}
	
	private void amendPlayerCrashed(Player p){
		for(int i = 0; i< numOfPlayer; i++){
			if(p.getNO() != player0[i].getNO()){
				amendTwoPlayerCrashed(p, player0[i]);
			}
			if(p.getNO() != player1[i].getNO()){
				amendTwoPlayerCrashed(p, player1[i]);
			}
		}
	}
	private void amendTwoPlayerCrashed(Player p1, Player p2){
		double distance = calDistance(p1.getPosition().getX(), p1.getPosition().getY(), p2.getPosition().getX(), p2.getPosition().getY());
		if(distance < playerRadius * 2){
			p1.setSpeed(p1.getSpeed().getSpeedX() * this.playerCrashDecay * -1, p1.getSpeed().getSpeedY() * this.playerSpeedDecay * -1);
			p2.setSpeed(p2.getSpeed().getSpeedX() * this.playerCrashDecay * -1, p2.getSpeed().getSpeedY() * this.playerSpeedDecay * -1);
		}
	}
	
	/**
	 * 修正球与球员发生撞击后球的位置和速度
	 */
	private void amendBallHit(){
		double ballX = this.ball.getPosition().getX();
		double ballY = this.ball.getPosition().getY();
		double minDistance = this.ballRadius + this.playerRadius;
		double distance = 0;
		for(int i = 0; i< this.numOfPlayer; i++){
			distance = calDistance(ballX, ballY, player0[i].getPosition().getX(), player0[i].getPosition().getY());
			if(distance < minDistance){
				ball.setSpeed(ball.getSpeed().getSpeedX() * this.ballHitPlayerDecay * -1, ball.getSpeed().getSpeedY() * this.ballHitPlayerDecay* -1);
			}
			distance = calDistance(ballX, ballY, player1[i].getPosition().getX(), player1[i].getPosition().getY());
			if(distance < minDistance){
				ball.setSpeed(ball.getSpeed().getSpeedX() * this.ballHitPlayerDecay * -1, ball.getSpeed().getSpeedY() * this.ballHitPlayerDecay -1);
			}
		}
	}
	
	private double calDistance(double x1, double y1, double x2, double y2){
		return Math.pow((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2), 0.5);
	}
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

	//TODO return problem
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

	public double getPitchWidth() {
		return pitchWidth;
	}

	public double getPitchLength() {
		return pitchLength;
	}
	
	public double getPlayerRadius() {
		return playerRadius;
	}

	public void setPlayerRadius(double playerRadius) {
		this.playerRadius = playerRadius;
	}

	public double getBallRadius() {
		return ballRadius;
	}

	public void setBallRadius(double ballRadius) {
		this.ballRadius = ballRadius;
	}
	

	public int getScore0() {
		return score0;
	}

	public int getScore1() {
		return score1;
	}

	public long getGameTime() {
		return gameTime;
	}

	public void setGameTime(long gameTime) {
		this.gameTime = gameTime;
	}

	public void setScore0(int score0) {
		this.score0 = score0;
	}

	public void setScore1(int score1) {
		this.score1 = score1;
	}

	/**
	 * CLIENT
	 */
	
	public WorldState requestWorldState(Side side, int unum){
		WorldState worldState;
		Player self;
		int ownScore;
		int oppoScore;
		if(side == Side.LEFT){
			self = player0[unum];
			ownScore = this.score0;
			oppoScore = this.score1;
		}	
		else{
			self = player1[unum];
			ownScore = this.score1;
			oppoScore = this.score0;
		}
		Player players[] = new Player[numOfPlayer * 2];
		
		for(int i = 0; i < numOfPlayer; i++){
			players[i] = new Player();
			players[i].setPlayer(player0[i]);
		}
		for(int i = numOfPlayer; i < numOfPlayer * 2; i++){
			players[i] = new Player();
			players[i].setPlayer(player1[i - numOfPlayer]);
		}
		
		worldState = new WorldState(self, players, this.ball, ownScore, oppoScore);
		return worldState;
	}

}
