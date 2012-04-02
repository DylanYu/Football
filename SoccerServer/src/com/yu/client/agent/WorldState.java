package com.yu.client.agent;

import com.yu.basicelements.Position;
import com.yu.overallsth.Ball;
import com.yu.overallsth.Player;

public class WorldState {
	private Player self;
	private Player players[];
	private Ball ball;
//	private int numOfOwnPlayer;
//	private int numOfOppoPlayer;
	private int ownScore;
	private int oppoScore;
//	private int playStyle;
	public WorldState(Player self, Player[] players, Ball ball, int ownScore, int oppoScore) {
		super();
		this.self = self.clone();
		this.players = players.clone();
		this.ball = ball.clone();
		this.ownScore = ownScore;
		this.oppoScore = oppoScore;
	}
	
	public Position getBallPosition(){
		return this.ball.getPosition();
	}
	
	public Position getOwnPosition(){
		return this.self.getPosition();
	}
	
}
