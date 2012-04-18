package com.yu.overallsth;

public class Values {
	public static final String COMMAND_UP = "command_up";
	public static final String COMMAND_UP0 = "command_up0";
	public static final String COMMAND_UP1 = "command_up1";
	public static final String SIGNAL_UP = "signal_up";
	public static final String NO_COMMAND = "no_Command";
	
	public static final int AGENT_SLEEP_TIME = 100;
	
	//参数
	public static final double MAX_DASH_POWER = 100;
	public static final double NORMAL_DASH_POWER = 50;
	public static final double MAX_KICK_POWER = 100;
	public static final double DASH_POWER_TO_ACC = 50;
	public static final double KICK_POWER_TO_ACC = 10;
	public static final double PLAYER_SPEED_DECAY = 1;
	public static final double BALL_SPEED_DECAY = 0.5;
	public static double PATH_PLANNING_ATTRACTION_K = 1;
	public static double PATH_PLANNING_REPULSION_K = -5;
	
	public static final double SHOOT_CUTAVOIDANCE_DISTANCE = 2;
	public static final double SHOOTABLE_DISTANCE = 20;
	public static final double DRIBBLE_SAFE_DISTANCE = 2;
	public static final double CLOSE_ENOUGH_TO_CHASE_DISTANCE = 10;
	
	public static final double BALL_ATTRACTION_K = 1.1;
	
	//球员类型
	public static final int PLAYER_TYPE_GOALKEEPER = 0;
	public static final int PLAYER_TYPE_LEFT_FULLBACK = 1;
	public static final int PLAYER_TYPE_LEFT_CENTERBACK = 2;
	public static final int PLAYER_TYPE_RIGHT_CENTERBACK = 3;
	public static final int PLAYER_TYPE_RIGHT_FULLBACK = 4;
	public static final int PLAYER_TYPE_LEFT_WINGER = 5;
	public static final int PLAYER_TYPE_LEFT_MIDFIELDER = 6;
	public static final int PLAYER_TYPE_RIGHT_MIDFIELDER = 7;
	public static final int PLAYER_TYPE_RIGHT_WINGER= 8;
	public static final int PLAYER_TYPE_LEFT_FORWARD = 9;
	public static final int PLAYER_TYPE_RIGHT_FORWARD = 10;
}
