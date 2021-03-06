package com.yu.overallsth;

import com.yu.basicelements.Side;
import com.yu.basicelements.Util;
import com.yu.client.agent.WorldState;

//TODO BIG THING 最后需要将其切割为服务端和客户端分别使用的pitch
/**
 * diary4/6:将球员碰撞检测修改为：若两人速度夹角小于90度，则认为不会发生碰撞
 * 
 * diary4/7:分割后可以改造成单体模式
 * 
 * 
 * @author hElo
 *
 */
public class Pitch {
	
	
	//TODO
	private long gameTime;
	
	
	//pitch 长宽
	private double pitchWidth = 68;
	private double pitchLength = 105;
	//球门宽度
	private double goalWidth = 7.32 * 2;
	

	//各类尺寸
	private double playerRadius = 0.6 * 2;
	

	private double ballRadius = 0.3 * 2;
	
	private double playerSpeedDecay = Values.PLAYER_SPEED_DECAY;
	private double ballSpeedDecay = Values.BALL_SPEED_DECAY;
	
	private double ballHitPlayerDecay = 0.1;
	private double playerCrashDecay = 0.5;

	// not good
	private double playerSpeedForRandom = 5;
	private double ballSpeedForRandom = 5;

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
			double speedX = Math.random() * playerSpeedForRandom * signX;
			double speedY = Math.random() * playerSpeedForRandom * signY;
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
			double speedX = Math.random() * playerSpeedForRandom * signX;
			double speedY = Math.random() * playerSpeedForRandom * signY;
			player1[i].setPosition(px, py);
			player1[i].setSpeed(speedX, speedY);
		}
	}
	
	public void initBallRandomly(){
		int signX = (Math.random() >= 0.5) ? -1 : 1;
		int signY = (Math.random() >= 0.5) ? -1 : 1;
		double speedX = Math.random() * ballSpeedForRandom * signX;
		double speedY = Math.random() * ballSpeedForRandom * signY;
		ball.setPosition(pitchWidth / 2, pitchLength / 2);
		ball.setSpeed(speedX, speedY);
	}
	
	public void initPitchRandomly(){
		initLeftRandomly();
		initRightRandomly();
		initBallRandomly();
	}
	
	public void initLeftFormally(){
		double widthA = pitchWidth / 8;
		double lengthA = pitchLength / 12;
		double x = -1;
		double y = -1;
		for(int i = 0; i < this.numOfPlayer; i++){
			switch (player0[i].getNO()) {
			case 0:
				x = widthA * 4;
				y = lengthA * 0;
				break;
			case 1:
				x = widthA * 7;
				y = lengthA * 3;
				break;
			case 2:
				x = widthA * 5;
				y = lengthA * 2;
				break;
			case 3:
				x = widthA * 3;
				y = lengthA * 2;
				break;
			case 4:
				x = widthA * 1;
				y = lengthA * 3;
				break;
			case 5:
				x = widthA * 7;
				y = lengthA * 7;
				break;
			case 6:
				x = widthA * 5;
				y = lengthA * 5.5;
				break;
			case 7:
				x = widthA * 3;
				y = lengthA * 5.5;
				break;
			case 8:
				x = widthA * 1;
				y = lengthA * 7;
				break;
			case 9:
				x = widthA * 5;
				y = lengthA * 9;
				break;
			case 10:
				x = widthA * 3;
				y = lengthA * 9;
				break;
			default:
				System.out.println("判断球员类型出错");
				break;
			}
			player0[i].setPosition(x, y);
		}
	}
	
	public void initRightFormally(){
		double widthA = pitchWidth / 8;
		double lengthA = pitchLength / 12;
		double x = -1;
		double y = -1;
		for(int i = 0; i < this.numOfPlayer; i++){
			switch (player0[i].getNO()) {
			case 0:
				x = widthA * 4;
				y = lengthA * 11;
				break;
			case 1:
				x = widthA * 1;
				y = lengthA * 8;
				break;
			case 2:
				x = widthA * 3;
				y = lengthA * 9;
				break;
			case 3:
				x = widthA * 5;
				y = lengthA * 9;
				break;
			case 4:
				x = widthA * 7;
				y = lengthA * 8;
				break;
			case 5:
				x = widthA * 1;
				y = lengthA * 4;
				break;
			case 6:
				x = widthA * 3;
				y = lengthA * 6.5;
				break;
			case 7:
				x = widthA * 5;
				y = lengthA * 6.5;
				break;
			case 8:
				x = widthA * 7;
				y = lengthA * 4;
				break;
			case 9:
				x = widthA * 3;
				y = lengthA * 2;
				break;
			case 10:
				x = widthA * 5;
				y = lengthA * 2;
				break;
			default:
				System.out.println("判断球员类型出错");
				break;
			}
			player1[i].setPosition(x, y);
		}
	}
	
	private void setPlayerSpeed(){
//		for(int i = 0; i < this.numOfPlayer; i++){
//			if(player0[i].getNO() == 9 || player0[i].getNO() == 10)
				player0[9].setMaxSpeed(Values.PLAYER_MAX_SPEED_WING);
				player0[10].setMaxSpeed(Values.PLAYER_MAX_SPEED_WING);
//		}
	}
	public void initPitchFormally(){
		this.initLeftFormally();
		this.initRightFormally();
		this.initBallRandomly();
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
		//TODO 暂时取消球员碰撞处理
//		amendPlayerCrash();
		//TODO 改进ballHit事件的处理
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
	
	public void setBallSpeed(double sx, double sy){
		this.ball.setSpeed(sx, sy);
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
		if(distance < playerRadius * 2 && !isDirectionSame(p1, p2)){
			p1.setSpeed(p1.getSpeed().getSpeedX() * this.playerCrashDecay * -1, p1.getSpeed().getSpeedY() * this.playerSpeedDecay * -1);
			p2.setSpeed(p2.getSpeed().getSpeedX() * this.playerCrashDecay * -1, p2.getSpeed().getSpeedY() * this.playerSpeedDecay * -1);
		}
	}
	
	private boolean isDirectionSame(Player p1, Player p2){
		double s1x = p1.getSpeed().getSpeedX();
		double s1y = p1.getSpeed().getSpeedY();
		double s2x = p2.getSpeed().getSpeedX();
		double s2y = p2.getSpeed().getSpeedY();
		return Util.isDirectionSame(s1x, s1y, s2x, s2y);
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
				ball.setSpeed(ball.getSpeed().getSpeedX() * this.ballHitPlayerDecay * -1, ball.getSpeed().getSpeedY() * this.ballHitPlayerDecay * -1);
			}
			distance = calDistance(ballX, ballY, player1[i].getPosition().getX(), player1[i].getPosition().getY());
			if(distance < minDistance){
				ball.setSpeed(ball.getSpeed().getSpeedX() * this.ballHitPlayerDecay * -1, ball.getSpeed().getSpeedY() * this.ballHitPlayerDecay * -1);
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
	public void makeBallAcc(double ax, double ay){
		this.ball.makeAcc(ax, ay);
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
			//clone
			self = player0[unum].clone();
			ownScore = this.score0;
			oppoScore = this.score1;
		}	
		else{
			self = player1[unum].clone();
			ownScore = this.score1;
			oppoScore = this.score0;
		}
		Player p0[] = new Player[numOfPlayer];
		Player p1[] = new Player[numOfPlayer];
		
		for(int i = 0; i < numOfPlayer; i++){
			p0[i] = new Player();
			p0[i].setPlayer(this.player0[i]);
		}
		for(int i = 0; i < numOfPlayer; i++){
			p1[i] = new Player();
			p1[i].setPlayer(this.player1[i]);
		}
		Ball agentBall = new Ball();
		agentBall.setBall(this.ball);
		worldState = new WorldState(this.pitchWidth, this.pitchLength, this.goalWidth, 
				self, p0, p1, agentBall, ownScore, oppoScore);
		return worldState;
	}

}
