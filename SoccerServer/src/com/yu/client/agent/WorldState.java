package com.yu.client.agent;

import com.yu.basicelements.Position;
import com.yu.overallsth.Ball;
import com.yu.overallsth.Player;

public class WorldState {
	private double pitchWidth;
	private double pitchLength;
	private double goalWidth;
	
	
	private Player self;
	private Player player0[];
	private Player player1[];
	private Ball ball;
//	private int numOfOwnPlayer;
//	private int numOfOppoPlayer;
	private int ownScore;
	private int oppoScore;
//	private int playStyle;
	public WorldState(double pitchWidth, double pitchLength, double goalWidth, Player self, Player[] player0, Player[] player1, Ball ball, int ownScore, int oppoScore) {
		super();
		this.pitchWidth = pitchWidth;
		this.pitchLength = pitchLength;
		this.goalWidth = goalWidth;
		this.self = self.clone();
		//没必要clone
		this.player0 = player0;
		this.player1 = player1;
		this.ball = ball;
		this.ownScore = ownScore;
		this.oppoScore = oppoScore;
	}
	
	
	
	public Player getSelf() {
		return self;
	}



	public Position getBallPosition(){
		return this.ball.getPosition();
	}
	
	public Position getOwnPosition(){
		return this.self.getPosition();
	}

	public double getPitchWidth() {
		return pitchWidth;
	}

	public double getPitchLength() {
		return pitchLength;
	}

	public double getGoalWidth() {
		return goalWidth;
	}

	//TODO 看看有没有效
	public Player[] getPlayer0() {
		return player0.clone();
	}
	public Player[] getPlayer1() {
		return player1.clone();
	}

	public Ball getBall() {
		return ball;
	}
	
	
	
}
