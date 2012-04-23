package com.yu.client.agent;

import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

import com.yu.basicelements.Side;
import com.yu.basicelements.Util;
import com.yu.overallsth.Ball;
import com.yu.overallsth.Pitch;
import com.yu.overallsth.Player;
import com.yu.overallsth.Values;

/**
 * diary4/6:增加kick，固有意识kickableMargin 增加keepFormation，在没有其他事情做时保持队形
 * 
 * diary4/7:为了解决misskick现象，在agent端将kickableMargin变小
 * 限制agent更加接近球才可以踢到，这样server端可以“宽容”agent的踢球miss情况
 * 
 * diary4/24:增加了一种边路进攻的战术
 * @author hElo
 * 
 */
public class Agent implements Runnable {

	//保持当前状态的时间，ms
	private int holdTime = 0;
	
	Side side;
	int NO;
	Pitch pitch;

	AgentOutputBuffer agentOutputBuffer;

	//
	WorldState worldState;

	private double pitchWidth;
	private double pitchLength;
	private double goalWidth;

	private Player self;
	private Player player0[];
	private Player player1[];
	private Ball ball;
	private int ownScore;
	private int oppoScore;

	
	//为了方便，引入worldState里的内容
	double selfX;
	double selfY;
	double ballX;
	double ballY;
	
	
	// TODO 约定type的数值
	// 在队伍中的作用,用于确定大约的位置
	private int playerType;

	// 一些固有意识
	// TODO 同步kickableMargin
	private double kickableMargin = 2;
	
	private double pathPlanningAttractionK = Values.PATH_PLANNING_ATTRACTION_K;
	private double pathPlanningReplusionK = Values.PATH_PLANNING_REPULSION_K;

	// TODO isRunning
	private boolean isRunning = true;

	public Agent(Side side, int unum, Pitch pitch,
			AgentOutputBuffer agentOutputBuffer) {
		super();
		this.side = side;
		this.NO = unum;
		this.pitch = pitch;
		// this.worldState = worldState;
		this.agentOutputBuffer = agentOutputBuffer;

		// TODO 根据NO确定TYPE，先确定成这样，以后会更改
		this.playerType = NO;
	}

	public void run() {
		while (isRunning) {
			worldState = pitch.requestWorldState(this.side, this.NO);

			pitchWidth = worldState.getPitchWidth();
			pitchLength = worldState.getPitchLength();
			goalWidth = worldState.getGoalWidth();
			self = worldState.getSelf();
			player0 = worldState.getPlayer0();
			player1 = worldState.getPlayer1();
			ball = worldState.getBall();
			// ownScore = worldState.ge;
			// oppoScore;
			selfX = self.getPosition().getX();
			selfY = self.getPosition().getY();
			ballX = ball.getPosition().getX();
			ballY = ball.getPosition().getY();
			
			////
			act();

//			Log.i(this.NO+"", "run once");
			// TODO !!!client agent sleep a while
			try {
				Thread.sleep(Values.AGENT_SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void act() {
		String command = null;
		command = tactics1();
		// //////////////////////////
		if (command != null)
			wrapCommandToBuffer(command);
		else {
			System.out.println("Agent:" + this.side + "," + this.NO + ", missed a chance to act");
			return;
		}
	}
	
	private String tactics0(){
		String command = null;
//		Player []p = (this.side == Side.LEFT)? this.player0: this.player1;
		//离球最近的是本方球员，本方控球，进攻姿态
		if(isPlayerClosestToBallOwn()){
								if (isBallKickable()) {
										double []tgt = produceShootTgt();
										if (shouldShoot(tgt[0], tgt[1])) {
											command = shootToGoalWithMaxPowerAndFixedAngle(tgt[0], tgt[1]);
										} else {
											Player closeP = this.getClosestOwnPlayer();
											if(this.isSpaceNoOppo(closeP, 5) && Util.calDistance(self, closeP) > 7 && this.isPlayerFronterThanSelf(closeP))
												command = this.passToPlayer(closeP);
											else if (this.isSpaceNoOppo(Values.DRIBBLE_SAFE_DISTANCE))
												command = dribbleToGoalWithPlanning();
											else
												command = passWithSelection();
										}
								} else {
									if(isClosestToBallInOwnTeam())
										command = chaseBallWithNormalSpeed();
									else
										command = keepFormation();
								}
		} 
		
		//对方控球，防守姿态
		else {
			//球距离足迹足够近，可以采取行动
			if(isBallCloserThan(Values.CLOSE_ENOUGH_TO_CHASE_DISTANCE)){
								//并且是离球最近的本方球员
								if(isClosestToBallInOwnTeam()){
									if (isBallKickable()) {
											double []tgt = produceShootTgt();
											if (this.shouldShoot(tgt[0], tgt[1])) {
												command = shootToGoalWithMaxPowerAndFixedAngle(tgt[0], tgt[1]);
											}  else {
												if (this.isSpaceNoOppo(Values.DRIBBLE_SAFE_DISTANCE))
													command = dribbleToGoalWithPlanning();
												else
													command = passWithSelection();
											}
									} else {
										command = chaseBallWithNormalSpeed();
									}
								}
								//距离足够近但不是离球最近的本方球员，保持队形
								else {
									command = keepFormation();
								}
								
			} 
			//球距离自己还不够近，保持队形
			else{
				command = keepFormation();
			}
		}
		return command;
	}
	
	private String tactics1(){
		String command = null;
		switch(this.playerType){
		case 2:case 3:
			command = tactics1_Player2_3();
			break;
		case 1:case 4:
			command = tactics1_Player1_4();
			break;
		case 6:case 7:
			command = tactics1_Player6_7();
			break;
		case 5:case 8:
			command = tactics1_Player5_8();
			break;
		case 9:case 10:
			command = tactics1_Player9_10();
			break;
		default:
			command = tactics0();
		}
		return command;
	}

	private String tactics1_Player2_3(){
		String command = null;
		if (isBallKickable()) {
			Player []p = (this.side == Side.LEFT) ? this.player0 : this.player1;
			double disTo6 = Util.calDistance(self, p[6]);
			double disTo7 = Util.calDistance(self, p[7]);
			int tgtNO = -1;
			if(disTo6 < disTo7)
				tgtNO = 6;
			else
				tgtNO = 7;
			if(this.isSpaceNoOppo(p[tgtNO], 3))
				command = this.passToPlayer(p[tgtNO]);
			else
				//TODO delete keepFormation
				if(this.isPlayerClosestToBallOwn() && this.isClosestToBallInOwnTeam() && this.isSpaceNoOppo(4))
					command = tactics0();
				else
					command = keepFormation();
		} else {
			if(this.isPlayerClosestToBallOwn() && this.isClosestToBallInOwnTeam() && this.isSpaceNoOppo(4))
				command = tactics0();
			else
				command = keepFormation();
		}
		return command;
	}
	
	private String tactics1_Player1_4(){
		String command = null;
		if (isBallKickable()) {
			Player []p = (this.side == Side.LEFT) ? this.player0 : this.player1;
			Player frontPlayer = (this.NO == 1) ? p[5] : p[8];
			Player []player4Pass = {frontPlayer, p[6], p[7]};
			double cutAvoidance = 0.5;
			double tgtSafeLevel = 1.0;
			int passPlayerNO = passSelection(player4Pass, cutAvoidance, tgtSafeLevel);
			Player passPlayer = player4Pass[passPlayerNO];
			if(this.isSpaceNoOppo(passPlayer, 3))
				command = this.passToPlayer(passPlayer);
			else
				//TODO delete keepFormation
				if(this.isPlayerClosestToBallOwn() && this.isClosestToBallInOwnTeam() && this.isSpaceNoOppo(4))
					command = tactics0();
				else
					command = keepFormation();
		} else {
			if(this.isPlayerClosestToBallOwn() && this.isClosestToBallInOwnTeam() && this.isSpaceNoOppo(4))
				command = tactics0();
			else
				command = keepFormation();
		}
		return command;
	}
	
	private String tactics1_Player6_7(){
		String command = null;
		if (isBallKickable()) {
			Player []p = (this.side == Side.LEFT) ? this.player0 : this.player1;
			Player otherMid = (this.NO == 6) ? p[7] : p[6];
			Player wing = (Math.random() >= 0.5) ? p[5]: p[8];
			if(this.isPlayerFronterThanSelf(otherMid) && this.isSpaceNoOppo(otherMid, 2))
				command = this.passToPlayer(otherMid);
			else if(this.isSpaceNoOppo(wing, 2))
				command = this.passToPlayer(wing);
			else
				command = tactics0();
		} else {
			command = tactics0();
		}
		return command;
	}
	
	private String tactics1_Player5_8(){
		String command = null;
		if (isBallKickable()) {
			double tgtX;
			double tgtY;
			if(this.side == Side.LEFT){
				tgtX = (this.NO == 5)? this.pitchWidth / 8 * 7 : this.pitchWidth / 8;
				tgtY = this.pitchLength / 11 * 9.75;
			} else{
				tgtX = (this.NO == 5)? this.pitchWidth / 8 : this.pitchWidth / 8 * 7;
				tgtY = this.pitchLength / 11 * (11 - 9.75);
			}
			int state = this.getAttackState();
			Player []p = (this.side == Side.LEFT) ? this.player0 : this.player1;
//			if(state != 6 && state != 7){
			if(!this.isInPosition(tgtX, tgtY)){
				if(this.isSpaceNoOppo(6))
					command = this.dribbleToPoint(tgtX, tgtY);
				else {
					Player tgtPlayer = (this.NO == 5)? p[9]: p[10];
					if(this.isSpaceNoOppo(tgtPlayer, 2) && !(this.getNumOfOppoCutter(self, tgtPlayer, 1) > 0)){
						command = this.passToPlayer(tgtPlayer);
					} else{
						command = this.dribbleToPoint(tgtX, tgtY);
					}
				}
			} else{
				if(!this.isInPosition(tgtX, tgtY))
					command = this.dribbleToPoint(tgtX, tgtY);
				else{
					int passPlayerNO = (this.NO == 5)? 10: 9;
					command = this.passToPlayerForward(p[passPlayerNO], 3);
				}
			}
		} else {
			command = tactics0();
		}
		return command;
	}
	
	private String tactics1_Player9_10(){
		String command = null;
		if (isBallKickable()) {
			int state = this.getAttackState();
			double []tgt = produceShootTgt();
			if (shouldShoot(tgt[0], tgt[1]))
				command = shootToGoalWithMaxPowerAndFixedAngle(tgt[0], tgt[1]);
			else{
				Player []p = (this.side == Side.LEFT) ? this.player0 : this.player1;
				Player wing0 = p[5];
				Player wing1 = p[8];
				double d0 = Util.calDistance(self, wing0);
				double d1 = Util.calDistance(self, wing1);
				Player passTgt;
				passTgt = (d0 < d1)? wing0: wing1;
				double tgtY = passTgt.getPosition().getY();
				if(state == 5){
					Player otherForward = (this.NO == 9)? p[10]: p[9];
					if(!(this.getNumOfOppoCutter(self, otherForward, 2) > 0) && this.isSpaceNoOppo(otherForward, 2))
						command = this.passToPlayer(otherForward);
					else if(side == Side.LEFT && tgtY > 75 || side == Side.RIGHT && tgtY > 30){
						command = this.passToPlayer(passTgt);
					}
				}else{
					 if(side == Side.LEFT && tgtY > 75 || side == Side.RIGHT && tgtY > 30)
							command = this.passToPlayer(passTgt);
					else
						command = tactics0();
				}
			}
		} else {
			command = tactics0();
		}
		return command;
	}
	
	private String goalieAction(){
		return null;
	}
	
	private double[] getGoaliePos(){
		double []pos = new double[2];
		double ballX = this.ball.getPosition().getX();
		double ballY = this.ball.getPosition().getY();
		double goalX = this.pitchWidth / 2;
		double goalY;
		if (this.side == Side.LEFT) {
			goalY = 0;
		} else
			goalY = this.pitchLength;
		double distanceToGoal =Util.calDistance(ballX, ballY, goalX, goalY); 
		if(distanceToGoal > Values.SHOOTABLE_DISTANCE * 3){
			pos = this.getNotGoaliePosWithBallAttraction(Values.GAME_STATE_ATTACK);
		} else{
			double k = 7.5 / 10.0;
			pos[0] = ballX + (goalX - ballX) * k; 
			pos[1] = ballY + (goalY - ballY) * k;
		}
		return pos;
	}
	
	private String dribbleToGoalWithNormalSpeed() {
		double tgtX = this.pitchWidth / 2;
		double tgtY = (this.side == Side.LEFT) ? this.pitchLength : 0;
		double ballX = this.ball.getPosition().getX();
		double ballY = this.ball.getPosition().getY();
		double angle = Util.calAngle(ballX, ballY, tgtX, tgtY);
		double kickPower = Values.MAX_KICK_POWER / 10;
		return dribble(angle, kickPower);
	}

	private String dribbleToPoint(double x, double y){
		double angle = this.calDribbleAngle(x, y);
		double kickPower = Values.MAX_KICK_POWER / 15;
		return dribble(angle, kickPower);
	}
	
	private String dribble(double angle, double kickPower) {
		// 也许有重复计算
		double x = this.self.getPosition().getX();
		double y = this.self.getPosition().getY();
		double ballX = this.ball.getPosition().getX();
		double ballY = this.ball.getPosition().getY();
		double distance = Util.calDistance(x, y, ballX, ballY);
		if (distance > this.kickableMargin)
			return chaseBallWithNormalSpeed();
		else {
			return kick(angle, kickPower);
		}

	}
	
	private String dribbleToGoalWithPlanning(){
		double tgtX = this.pitchWidth / 2;
		double tgtY = (this.side == Side.LEFT) ? this.pitchLength : 0;
		double angle = this.calDribbleAngle(tgtX, tgtY);
		double kickPower = Values.MAX_KICK_POWER / 15;
		return dribble(angle, kickPower);
	}
	
	/**
	 * 计算最优带球路线
	 * @return
	 */
	private double calDribbleAngle(double tgtX, double tgtY){
		int total = 9;
		double per = 2 * Math.PI / total;
		//预测的带球滚动距离，可能需要修正
		double dribbleDitance = 3.0;
		ArrayList<double[]> list = new ArrayList<double[]>();
		double myX = this.self.getPosition().getX();
		double myY = this.self.getPosition().getY();
		double ballX = this.ball.getPosition().getX();
		double ballY = this.ball.getPosition().getY();
		for (int i = 0; i < total; i++){
			double a = per * i;
			double [] p = new double[2];
			p[0] = ballX + dribbleDitance * Math.cos(a);
			p[1] = ballY + dribbleDitance * Math.sin(a);
			list.add(p);
		}
		double[] bonus = new double[total];
		int n = 0;
		double max = -1000;
		int maxN = 0;
		while (n < total) {
			double[] p = list.get(n++);
			bonus[n - 1] = calPathPlanningBonus(tgtX, tgtY, myX, myY, p[0], p[1]);
//			System.out.print("【"+ (n-1)+"】"+bonus[n - 1]+";");
			if (bonus[n - 1] > max) {
				max = bonus[n - 1];
				maxN = n - 1;
			}
		}
//		System.out.println();
		return per * maxN;
	}
	
	/**
	 * 从1最终要到达tgt，若途径2，计算其bonus
	 * @param tgtX
	 * @param tgtY
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	private double calPathPlanningBonus(double tgtX, double tgtY, double x1, double y1, double x2, double y2) {
		double aK = this.pathPlanningAttractionK;
		double bK = this.pathPlanningReplusionK;
		double d0 = Util.calDistance(x1, y1, tgtX, tgtY);
		double d1 = Util.calDistance(x2, y2, tgtX, tgtY);
		double distanceBonus = aK * (d0 - d1);
	
		Player closeP = this.getClosestOppoPlayer();
		double oX = closeP.getPosition().getX();
		double oY = closeP.getPosition().getY();
		double d2 = Util.calDistance(x2, y2, oX, oY);
		double oppoBonus = 0;
//		if(d2 <= 2)
//			oppoBonus = -10;
//		else
			oppoBonus = bK / d2;
//		double oppoBonus = 0;
		return distanceBonus + oppoBonus;
	}

	/**
	 * 根据运算向最佳对象传球，cutAvoidance = 0.5;
	 * @return
	 */
	private String passWithSelection() {
		double cutAvoidance = 0.5;
		double tgtSafeLevel = 1.0;
		int NO = passSelection(cutAvoidance, tgtSafeLevel);
		Player[] p = this.getOwnPlayers();
		double ballX = this.ball.getPosition().getX();
		double ballY = this.ball.getPosition().getY();
		double x2 = p[NO].getPosition().getX();
		double y2 = p[NO].getPosition().getY();
		double distance = Util.calDistance(ballX, ballY, x2, y2);
		double angle = Util.calAngle(ballX, ballY, x2, y2);
		double kickPower = dstsToKickPower(distance);
		return kick(angle, kickPower);
	}

	private String passForward() {
		double maxKickPower = Values.MAX_KICK_POWER;
		double kickPower = maxKickPower;

		// double kickPower = Values.MAX_KICK_POWER / 3;
		Player[] p;
		Player self = worldState.getSelf();
		if (this.side == Side.LEFT)
			// 这里的get有clone，应该有效
			p = worldState.getPlayer0();
		else
			p = worldState.getPlayer1();

		for (int i = 0; i < p.length; i++) {
			if (this.side == Side.LEFT) {
				if (p[i].getPosition().getY() <= self.getPosition().getY()) {
					p[i] = null;
				}
			}
			if (this.side == Side.RIGHT) {
				if (p[i].getPosition().getY() >= self.getPosition().getY()) {
					p[i] = null;
				}
			}
		}
		int sNO = 0;
		double sDistance = 1000;
		double x = self.getPosition().getX();
		double y = self.getPosition().getY();
		// 找到最前面的队友，排除最前面是自己的情况
		for (int i = 0; i < p.length; i++) {
			if (p[i] != null && i != this.NO) {
				double distance = Util.calDistance(x, y, p[i].getPosition()
						.getX(), p[i].getPosition().getY());
				if (distance < sDistance) {
					sDistance = distance;
					sNO = i;
				}
			}
		}
		if (sDistance != 1000) {
			double angle = Util.calAngle(x, y, p[sNO].getPosition().getX(),
					p[sNO].getPosition().getY());
			kickPower = dstsToKickPower(sDistance);
			return kick(angle, kickPower);
		} else
			return null;
	}
	
	/**
	 * 向指定的球员的身前传球，认为此球员为本方球员
	 * @param p
	 * @param forwardLength
	 * @return
	 */
	private String passToPlayerForward(Player p, double forwardLength){
		String command = null;
		if(this.side == Side.RIGHT)
			forwardLength *= -1;
		double pX = p.getPosition().getX();
		double pY = p.getPosition().getY() + forwardLength;
		if(pY < 0 || pY > this.pitchLength){
			System.out.println("Agent::passToPlayerForward Error!");
			return null;
		}
		double angle = Util.calAngle(ballX, ballY, pX, pY);
		double distance = Util.calDistance(ballX, ballY,  pX, pY);
		double kickPower = dstsToKickPower(distance);
		command = this.kick(angle, kickPower);
		return command;
	}
	
	/**
	 * 向指定的球员传球
	 * @param p
	 * @return
	 */
	private String passToPlayer(Player p){
		String command = null;
		double pX = p.getPosition().getX();
		double pY = p.getPosition().getY();
		double angle = Util.calAngle(ballX, ballY, pX, pY);
		double distance = Util.calDistance(ballX, ballY,  pX, pY);
		double kickPower = dstsToKickPower(distance);
		command = this.kick(angle, kickPower);
		return command;
	}

	private String shootToGoalWithMaxPowerAndFixedAngle(double tgtX, double tgtY) {
		double kickPower = Values.MAX_KICK_POWER / 4 * 3;
		double ballX =this.ball.getPosition().getX();
		double ballY = this.ball.getPosition().getY();
		double angle = Util.calAngle(ballX, ballY, tgtX, tgtY);
		return kick(angle, kickPower);
	}
	
	private String shootToGoalWithMaxPowerAndRandomAngle() {
		double shootCutAvoidanceSafeDistance = 2;
		double kickPower = Values.MAX_KICK_POWER / 4 * 3;
		// double kickPower = Values.MAX_KICK_POWER / 2;
		double goalX = 0;
		double goalY = 0;
		double sign = (Math.random() >= 0.5) ? -1 : 1;
		double offset = Math.random() * this.goalWidth / 2;
		goalX = this.pitchWidth / 2 + sign * offset;
		if (this.side == Side.LEFT) {
			goalY = this.pitchLength;
		} else
			goalY = 0;
		double ballX = worldState.getBallPosition().getX();
		double ballY = worldState.getBallPosition().getY();
		// shoot cut avoidance
		int num = getNumOfOppoCutter(ballX, ballY, goalX, goalY, shootCutAvoidanceSafeDistance);
		if (num > 0) {
			// TODO 无法完成射门，则射向某处
			kickPower = kickPower / 2;
			double angle = Math.random() >= 0.5 ? Math.PI * 0.75 : Math.PI * 0.35;
			if (this.side == Side.LEFT)
				angle = angle;
			else
				angle = -angle;
			return kick(angle, kickPower);
		} else {
			double angle = Util.calAngle(ballX, ballY, goalX, goalY);
			return kick(angle, kickPower);
		}
	}
	
	/**
	 * 跑向球场某点,用力分划点为15M,10M,5M
	 * @param tgtX
	 * @param tgtY
	 * @return
	 */
	private String dashToPoint(double tgtX, double tgtY){
		double dashPower;
		double x = this.self.getPosition().getX();
		double y = this.self.getPosition().getY();
		double distance =  Util.calDistance(x, y, tgtX, tgtY);
		if(distance > 10)
			dashPower = Values.NORMAL_DASH_POWER * 1.5;
		else if(distance >5)
			dashPower = Values.NORMAL_DASH_POWER;
		else
			dashPower = Values.NORMAL_DASH_POWER * 0.5; 
		double angle = Util.calAngle(x, y, tgtX, tgtY);
		return dash(angle, dashPower);
	}
	
	private String chaseBallWithNormalSpeed() {
		// double dashPower = Str.MAX_DASH_POWER;
		double dashPower = Values.NORMAL_DASH_POWER;
		return chaseBall(dashPower);
	}

	private String chaseBall(double dashPower) {
		double a = 0;
		double x = worldState.getOwnPosition().getX();
		double y = worldState.getOwnPosition().getY();
		double ballX = worldState.getBallPosition().getX();
		double ballY = worldState.getBallPosition().getY();
		a = Util.calAngle(x, y, ballX, ballY);

		return dash(a, dashPower);
	}

	/**
	 * 为了保持队形，进行相应dash
	 * 
	 * @return
	 */
	private String keepFormation() {
		double[] p = getMyPos();
		// double[] p = getPos(this.side);
		double x0 = worldState.getOwnPosition().getX();
		double y0 = worldState.getOwnPosition().getY();
		double x1 = p[0];
		double y1 = p[1];
		double distance = Util.calDistance(x0, y0, x1, y1);

		// TODO 参数待测试 距离与dashPower
		double dashPower = 20 + distance / 0.9;
		double angle = Util.calAngle(x0, y0, x1, y1);
		return dash(angle, dashPower);
//		return dashToPoint(x1, y1);
	}

	/**
	 * @param cutAvoidance
	 * 		被截球防止等级：从1开始，数字越大表示越保守，需要防止的范围越大。现在规定1表示5M，2表示10M
	 * @param tgtSafeLevel
	 * 		要求传球目标安全等级：从0开始，数字越大表示越保守，要求传球目标周围存在的对手数目越少
	 * @return 己方队员号码
	 */
	private int passSelection(double cutAvoidance, double tgtSafeLevel) {
		Player []p = this.getOwnPlayers();
		return this.passSelection(p, cutAvoidance, tgtSafeLevel);
	}
	
	/**
	 * 传球对象选择。现在只考虑了1、避免在球运行过程中被截掉 2、离传球队员尽可能远。 diary4/11:仍然有问题
	 * 3、被传球者周围没有对手。diary4/23
	 * 
	 * @param cutAvoidance
	 * 		被截球防止等级：从1开始，数字越大表示越保守，需要防止的范围越大。现在规定1表示5M，2表示10M
	 * @param tgtSafeLevel
	 * 		要求传球目标安全等级：从0开始，数字越大表示越保守，要求传球目标周围存在的对手数目越少
	 * @return 己方队员号码
	 */
	// TODO
	private int passSelection(Player p[], double cutAvoidance, double tgtSafeLevel) {
		double standard = 10;
		double safeDistance = 0;
		safeDistance = cutAvoidance * 5;
		double[] preferenceForcutAvoidance = new double[p.length];
		for (int i = 0; i < p.length; i++) {
			if (p[i].getNO() != this.NO) {
				int numOfOppoCutter = 0;
				numOfOppoCutter = getNumOfOppoCutter(
						this.self.getPosition().getX(), this.self.getPosition().getY(), 
						p[i].getPosition().getX(), p[i].getPosition().getY(),
						safeDistance);
				// TODO delete
//				System.out.println(this.side + "," + this.NO + " to " + p[i].getNO() + " 's numOfOppoCutter : " + numOfOppoCutter);
				// TODO must improve
				// preferenceForcutAvoidance[i] = (11.0 - numOfOppoCutter) / 11.0 * standard ;
				if (numOfOppoCutter >= 1)
					preferenceForcutAvoidance[i] = 0;
				else
					preferenceForcutAvoidance[i] = standard * 2;
			} else
				// 表示不能传给自己
				preferenceForcutAvoidance[i] = -1000;
		}

		double[] preferenceForFront = new double[p.length];
		for (int i = 0; i < p.length; i++) {
			if (p[i].getNO() != this.NO) {
				if (this.side == Side.LEFT)
					preferenceForFront[i] = (this.pitchLength + p[i].getPosition().getY() - this.self.getPosition().getY())
							/ this.pitchLength * standard / 2;
				else
					preferenceForFront[i] = (this.pitchLength + this.self.getPosition().getY() - p[i].getPosition().getY())
							/ this.pitchLength* standard / 2;
			} else
				// 表示不能传给自己
				preferenceForFront[i] = -1000;
		}
		
		double[] preferenceForTgtNoOppo = new double[p.length];
		for (int i = 0; i < p.length; i++) {
			if (p[i].getNO() != this.NO) {
				double a1 = isSpaceNoOppo(p[i], 2)? 0: -1000;
				double a2 = isSpaceNoOppo(p[i], 4)? 3: -15;
				double a3 = isSpaceNoOppo(p[i], 6)? 8: -8;
				double a4 = isSpaceNoOppo(p[i], 10)? 15: 0;
				preferenceForTgtNoOppo[i] = (a1 + a2 + a3 + a4) * tgtSafeLevel;
			} else
				// 表示不能传给自己
				preferenceForTgtNoOppo[i] = -1000;
		}

		double[] preference = new double[p.length];
		int maxN = -1;
		double max = -1;
		for (int i = 0; i < p.length; i++) {
			preference[i] = preferenceForcutAvoidance[i] + preferenceForFront[i] + preferenceForTgtNoOppo[i];
			if (preference[i] > max) {
				max = preference[i];
				maxN = i;
			}
		}
		// TODO delete
		// System.out.println(this.side + "," + this.NO
		// +"'s pass preference is " + maxN);

		return maxN;

	}

	/**
	 * 根据计算判断是否应该射门，射门目标位tgtX，tgtY.包含了距离球门的计算
	 * @param tgtX
	 * @param tgtY
	 * @return
	 */
	private boolean shouldShoot(double tgtX, double tgtY){
		if(!isDistanceShootable(tgtX, tgtY))
			return false;
		else{
			double x = this.self.getPosition().getX();
			double y = this.self.getPosition().getY();
			int numOfOppoCutter = getNumOfOppoCutter(x, y, tgtX, tgtY, Values.SHOOT_CUTAVOIDANCE_DISTANCE);
			if(numOfOppoCutter > 0)
				return false;
			else
				return true;
		}
	}
	
	private int getNumOfOppoCutter(Player p1, Player p2, double safeDistance) {
		double x1 = p1.getPosition().getX();
		double y1 = p1.getPosition().getY();
		double x2 = p2.getPosition().getX();
		double y2 = p2.getPosition().getY();
		return this.getNumOfOppoCutter(x1, y1, x2, y2, safeDistance);
	}
	
	/**
	 * 获得传球路线上存在的对手可能截球者数量
	 * 
	 * @return
	 */
	private int getNumOfOppoCutter(double x1, double y1, double x2, double y2,
			double safeDistance) {
		int num = 0;
		double A = y2 - y1;
		double B = x1 - x2;
		double C = -x1 * y2 + x2 * y1;
		Player p[];
		if (this.side == Side.LEFT)
			p = this.player1;
		else
			p = this.player0;
		for (int i = 0; i < p.length; i++) {
			double x = p[i].getPosition().getX();
			double y = p[i].getPosition().getY();
			double distance = Util.calDistanceFromPointToLine(x, y, A, B, C);
			if (distance <= safeDistance) {
				double[] pos = Util.getIntersectionFromPotToLine(x, y, A, B, C);
				if (Util.isANumBetweenTwoNums(pos[0], x1, x2)
						&& Util.isANumBetweenTwoNums(pos[1], y1, y2))
					;
				num++;
			}
		}
		return num;
	}

	private boolean isFrontest() {
		Player[] p;
		if (this.side == Side.LEFT) {
			p = player0;
			for (int i = 0; i < p.length; i++) {
				if (p[i].getPosition().getY() > self.getPosition().getY())
					return false;
			}
			return true;
		} else {
			p = player1;
			for (int i = 0; i < p.length; i++) {
				if (p[i].getPosition().getY() < self.getPosition().getY())
					return false;
			}
			return true;
		}
	}

	private double[] getMyPos() {
		if(this.NO != 0)
			return getNotGoaliePosWithBallAttraction(Values.GAME_STATE_ATTACK);
		else
			return getGoaliePos();
	}
	
	private double[] getNotGoaliePosWithBallAttraction(int state) {
		// TODO 引力参数,1为无引力,越大则球的吸引力越强
		/*
		 * 事实证明，这个数值设定是合理的
		 */
		double k = Values.BALL_ATTRACTION_K;

		// TODO alternative
//		double[] p = getTestPos(this.side);
		 double[] p = getPos(this.side);
		double x = p[0];
		double y = p[1];
		double ballX = worldState.getBallPosition().getX();
		double ballY = worldState.getBallPosition().getY();
		x = ((k - 1) * ballX + x) / k;
		y = ((k - 1) * ballY + y) / k;
		double[] result = { x, y };
		return result;
	}

	private double[] getTestPos(Side side) {
		if (side == Side.LEFT)
			return getTestPosInLeftFormation();
		else
			return getTestPosInRightFormation();
	}

	private double[] getPos(Side side) {
		if (side == Side.LEFT)
			return getPosInLeftFormation();
		else
			return getPosInRightFormation();
	}

	private double[] getTestPosInLeftFormation() {
		double pitchWidth = worldState.getPitchWidth();
		double pitchLength = worldState.getPitchLength();
		double widthA = pitchWidth / 8;
		double lengthA = pitchLength / 12;
		double x = -1;
		double y = -1;
		switch (this.playerType) {
		case 0:
			x = widthA * 4;
			y = lengthA * 0;
			break;
		case 1:
			x = widthA * 7;
			y = lengthA * 3;
			break;
		case 2:
			x = widthA * 7;
			y = lengthA * 7;
			break;
		case 3:
			x = widthA * 3;
			y = lengthA * 6;
			break;
		case 4:
			x = widthA * 3;
			y = lengthA * 9;
			break;
		default:
			System.out.println("判断球员类型出错");
			break;
		}
		double[] result = { x, y };
		return result;
	}

	private double[] getTestPosInRightFormation() {
		double pitchWidth = worldState.getPitchWidth();
		double pitchLength = worldState.getPitchLength();
		double widthA = pitchWidth / 8;
		double lengthA = pitchLength / 12;
		double x = -1;
		double y = -1;
		switch (this.playerType) {
		case 0:
			x = widthA * 4;
			y = lengthA * 12;
			break;
		case 1:
			x = widthA * 1;
			y = lengthA * 9;
			break;
		case 2:
			x = widthA * 7;
			y = lengthA * 9;
			break;
		case 3:
			x = widthA * 5;
			y = lengthA * 6;
			break;
		case 4:
			x = widthA * 5;
			y = lengthA * 3;
			break;
		default:
			System.out.println("判断球员类型出错");
			break;
		}
		double[] result = { x, y };
		return result;
	}

	/**
	 * 在同队中是否离球最近
	 * 
	 * @return
	 */
	private boolean isClosestToBallInOwnTeam() {
		Player self;
		self = worldState.getSelf();
		Player[] player;
		if (this.side == Side.LEFT)
			player = worldState.getPlayer0();
		else
			player = worldState.getPlayer1();
		double ballX = worldState.getBall().getPosition().getX();
		double ballY = worldState.getBall().getPosition().getY();
		double myDistance = Util.calDistance(ballX, ballY, self.getPosition().getX(), self.getPosition().getY());
		double d;
		for (int i = 0; i < player.length; i++) {
			if(i != this.NO){
				d = Util.calDistance(ballX, ballY, player[i].getPosition().getX(), player[i].getPosition().getY());
				if (d < myDistance)
					return false;
			}
		}
		return true;
	}
	
	/**
	 * 球与自己距离是否小于给定值
	 * @param distance
	 * @return
	 */
	private boolean isBallCloserThan(double distance){
		double x = this.self.getPosition().getX();
		double y = this.self.getPosition().getY();
		double ballX = this.ball.getPosition().getX();
		double ballY = this.ball.getPosition().getY();
		double dis2ball = Util.calDistance(x, y, ballX, ballY);
		if(dis2ball < distance)
			return true;
		else
			return false;
	}

	/**
	 * 离球最近的球员是否本方球员
	 * 
	 * @return
	 */
	private boolean isPlayerClosestToBallOwn() {
		Player []p0;
		Player [] p1;
		p0 = this.player0;
		p1 = this.player1;
		double ballX = this.ball.getPosition().getX();
		double ballY = this.ball.getPosition().getY();
		ArrayList<Double> d0 = new ArrayList<Double>();
		ArrayList<Double> d1 = new ArrayList<Double>();
		for (int i = 0; i < p0.length; i++) {
			d0.add(Util.calDistance(ballX, ballY, p0[i].getPosition().getX(), p0[i].getPosition().getY()));
		}
		for (int i = 0; i < p1.length; i++) {
			d1.add(Util.calDistance(ballX, ballY, p1[i].getPosition().getX(), p1[i].getPosition().getY()));
		}
		double minD0 = Collections.min(d0);
		double minD1 = Collections.min(d1);
		if(this.side == Side.LEFT && minD0 < minD1)
			return true;
		else if(this.side == Side.RIGHT && minD0 > minD1)
			return true;
		else
			return false;
	}
	
	/**
	 * 将命令加上队伍、号码信息并在结尾加上分隔符后，推送进AgentOutputBuffer
	 * 
	 * @param commmand
	 */
	private void wrapCommandToBuffer(String commmand) {
		StringBuffer buffer = new StringBuffer();
		String sside;
		if (this.side == Side.LEFT)
			sside = "0";
		else
			sside = "1";

		buffer.append(sside + ",");
		buffer.append(this.NO + ",");
		buffer.append(commmand);
		// 结尾
		buffer.append("|");
		agentOutputBuffer.add(this.NO, buffer);
	}

	/*
	 * 
	 * 
	 * 以下是最基本的动作命令
	 */
	
	/**
	 * 这个kick意味着取消球原来的速度，而新加上一个速度。这虽然与实际情况不符，但实现起来似乎也不影响效果
	 * 
	 * @param angle
	 *            PI制
	 * @param power
	 * @return
	 */
	private String kick(double angle, double power) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("kick" + ",");
		buffer.append(angle + ",");
		buffer.append(power + ",");

		return buffer.toString();
	}

	/**
	 * 
	 * @param angle
	 *            PI制
	 * @param power
	 * @return
	 */
	private String dash(double angle, double power) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("dash" + ",");
		buffer.append(angle + ",");
		buffer.append(power + ",");
		return buffer.toString();
	}

	// private double changeAngle(double angle){
	// return angle / Math.PI * 180;
	// }

	public void stopRunning() {
		this.isRunning = false;
	}

	private Player[] getOwnPlayers() {
		if (this.side == Side.LEFT)
			return this.player0;
		else
			return this.player1;
	}
	
	private Player[] getOppoPlayers() {
		if (this.side == Side.LEFT)
			return this.player1;
		else
			return this.player0;
	}

	/**
	 * 
	 * 根据距离远近确定踢球力量大小
	 * 
	 * @param distance
	 * @return
	 */
	// TODO 待测试
	private double dstsToKickPower(double distance) {
		double max = Values.MAX_KICK_POWER;
		if (distance < 10)
			return max * 0.25;
		else if (distance < 20)
			return max * 0.35;
		else if (distance < 30)
			return max * 0.45;
		else if (distance < 40)
			return max * 0.55;
		else if (distance < 50)
			return max * 0.6;
		else if (distance < 60)
			return max * 0.7;
		else if (distance < 80)
			return max * 0.8;
		else
			return max * 0.9;
	}

	/**
	 * 根据当前距离判断是否可以射门，暂时设定为40M
	 * 
	 * @return
	 */
	private boolean isDistanceShootable(double tgtX, double tgtY) {
		double x = this.self.getPosition().getX();
		double y = this.self.getPosition().getY();
		double distance = Util.calDistance(x, y, tgtX, tgtY);
		if (distance <= Values.SHOOTABLE_DISTANCE)
			return true;
		else
			return false;
	}

	/**
	 * 球是否在可踢范围内
	 */
	private boolean isBallKickable(){
		double x = this.self.getPosition().getX();
		double y = this.self.getPosition().getY();
		double ballX = this.ball.getPosition().getX();
		double ballY = this.ball.getPosition().getY();
		double dis2ball = Util.calDistance(x, y, ballX, ballY);
		if (dis2ball <= this.kickableMargin)
			return true;
		else
			return false;
	}
	
	/**
	 * 判断对于自己在给定范围内有无对方球员
	 * 
	 * @param safeDistance
	 * @return
	 */
	private boolean isSpaceNoOppo(double safeDistance) {
		return isSpaceNoOppo(this.self, safeDistance);
	}
	
	/**
	 * 判断给定球员是否相对于自己更靠近对方球门
	 * @return
	 */
	private boolean isPlayerFronterThanSelf(Player p){
		double pY = p.getPosition().getY();
		if(this.side == Side.LEFT){
			if(pY >= this.selfY)
				return true;
			else
				return false;
		} else {
			if(pY <= this.selfY)
				return true;
			else
				return false;
		}
	}
	
	/**
	 * 判断p2是否在p1 前面
	 * @return
	 */
	private boolean isPlayerFronter(Player p1, Player p2){
		double pY1 = p1.getPosition().getY();
		double pY2 = p2.getPosition().getY();
		if(p1.getSide() == Side.LEFT){
			if(pY2 >= pY1)
				return true;
			else
				return false;
		} else {
			if(pY2 <= pY1)
				return true;
			else
				return false;
		}
	}
	
	/**
	 * 判断某一本方球员在给定范围内有无对方球员
	 * 
	 * @param safeDistance
	 * @return
	 */
	private boolean isSpaceNoOppo(Player player, double safeDistance) {
		Player[] p = (this.side == Side.LEFT) ? this.player1 : this.player0;
		double x = player.getPosition().getX();
		double y = player.getPosition().getY();
		double x1;
		double y1;
		double distance;
		for (int i = 0; i < p.length; i++) {
			x1 = p[i].getPosition().getX();
			y1 = p[i].getPosition().getY();
			distance = Util.calDistance(x, y, x1, y1);
			if (distance <= safeDistance)
				if(this.side == Side.LEFT && y1 > y)
					return false;
				else if(this.side == Side.RIGHT && y1 < y)
					return false;
		}
		return true;
	}
	
	private double[] produceShootTgt(){
		double goalX = 0;
		double goalY = 0;
		double sign = (Math.random() >= 0.5) ? -1 : 1;
//		double offset = Math.random() * this.goalWidth / 2;
		double offset = 0.9 * this.goalWidth / 2;
		goalX = this.pitchWidth / 2 + sign * offset;
		if (this.side == Side.LEFT) {
			goalY = this.pitchLength;
		} else
			goalY = 0;
		double []result = new double[2];
		result[0] = goalX;
		result[1] = goalY;
		return result;
	}
	
	/**
	 * 获得距离自己最近的对方球员
	 * @return
	 */
	private Player getClosestOppoPlayer(){
		Player []p = (this.side == Side.LEFT) ? this.player1 : this.player0;
		double d;
		double minD = 10000;
		int minDN = 0;
		for(int i = 0; i < p.length; i++){
			d = Util.calDistance(self, p[i]);
			if(d < minD){
				minD = d;
				minDN = i;
			}
		}
		return p[minDN];
	}
	
	/**
	 * 获得距离自己最近的本方球员
	 * @return
	 */
	private Player getClosestOwnPlayer(){
		Player []p = (this.side == Side.LEFT) ? this.player0 : this.player1;
		double d;
		double minD = 10000;
		int minDN = 0;
		for(int i = 0; i < p.length; i++){
			d = Util.calDistance(self, p[i]);
			if(d < minD){
				minD = d;
				minDN = i;
			}
		}
		return p[minDN];
	}
	
	/**
	 * 根据球当前位置，判断进攻状态
	 * @return
	 */
	private int getAttackState(){
		double ballX = this.ball.getPosition().getX();
		double ballY = this.ball.getPosition().getY();
		if(this.side == Side.LEFT){
			if(ballY < 40)
				return 0;
			else if(ballY < 70)
				return 1;
			else if(ballX < this.pitchWidth / 8 * 2 && ballY < 75)
				return 2;
			else if(ballX > this.pitchWidth / 8 * 6 && ballY < 75)
				return 3;
			else if(ballX < this.pitchWidth / 8 * 2 && ballY >= 75)
				return 6;
			else if(ballX > this.pitchWidth / 8 * 6 && ballY >= 75)
				return 7;
			else if(ballY < 90)
				return 4;
			else
				return 5;
		} else {
			if(ballY > 65)
				return 0;
			else if(ballY > 35)
				return 1;
			else if(ballX > this.pitchWidth / 8 * 6 && ballY > 30)
				return 2;
			else if(ballX < this.pitchWidth / 8 * 2 && ballY > 30)
				return 3;
			else if(ballX > this.pitchWidth / 8 * 6 && ballY <= 30)
				return 6;
			else if(ballX < this.pitchWidth / 8 * 2 && ballY <= 30)
				return 7;
			else if(ballY > 15)
				return 4;
			else
				return 5;
						
		}
	}
	
	/**
	 * 自己是否就位
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isInPosition(double x, double y){
		return this.isInArea(self, x, y, 4);
	}
	/**
	 * 球员是否在指定区域内，区域用中心坐标x,y和圆半径space表示
	 * @param player
	 * @param x
	 * @param y
	 * @param space
	 * @return
	 */
	private boolean isInArea(Player player, double x, double y, double space){
		double distance = Util.calDistance(selfX, selfY, x, y);
		if(distance <= space)
			return true;
		else
			return false;
	}
	
	private boolean isWingOverstep(){
		Player []oppo = this.getOppoPlayers();
		Player []own = this.getOwnPlayers();
		Player wing0 = own[5];
		Player wing1 = own[8];
		double d0 = Util.calDistance(self, wing0);
		double d1 = Util.calDistance(self, wing1);
		Player wing = (d0 <= d1)? wing0: wing1;
		int tgtNO = (wing.getNO() == 5)? 4: 1;
		Player tgt = oppo[tgtNO];
		return !this.isPlayerFronter(wing, tgt);
	}
	
	
	
	
	
	
	
	
	
	
	private double[] getPosInLeftFormation() {
		double pitchWidth = worldState.getPitchWidth();
		double pitchLength = worldState.getPitchLength();
		double widthA = pitchWidth / 8;
		double lengthA = pitchLength / 12;
		double x = -1;
		double y = -1;
		int attackState = this.getAttackState();
		int type = attackState * 100 + this.playerType;
		switch (type) {
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
			y = lengthA * 6;
			break;
		case 6:
			x = widthA * 5;
			y = lengthA * 5;
			break;
		case 7:
			x = widthA * 3;
			y = lengthA * 7;
			break;
		case 8:
			x = widthA * 1;
			y = lengthA * 6;
			break;
		case 9:
			x = widthA * 5;
			y = lengthA * 8;
			break;
		case 10:
			x = widthA * 3;
			y = lengthA * 8;
			break;
		case 100:
			x = widthA * 4;
			y = lengthA * 0;
			break;
		case 101:
			x = widthA * 7;
			y = lengthA * 3;
			break;
		case 102:
			x = widthA * 5;
			y = lengthA * 2;
			break;
		case 103:
			x = widthA * 3;
			y = lengthA * 2;
			break;
		case 104:
			x = widthA * 1;
			y = lengthA * 3;
			break;
		case 105:
			x = widthA * 7;
			y = lengthA * 7;
			break;
		case 106:
			x = widthA * 5;
			y = lengthA * 7;
			break;
		case 107:
			x = widthA * 3;
			y = lengthA * 7;
			break;
		case 108:
			x = widthA * 1;
			y = lengthA * 7;
			break;
		case 109:
			x = widthA * 5;
			y = lengthA * 8;
			break;
		case 110:
			x = widthA * 3;
			y = lengthA * 8;
			break;
		case 200:case 300:
			x = widthA * 4;
			y = lengthA * 0;
			break;
		case 201:case 301:
			x = widthA * 7;
			y = lengthA * 3;
			break;
		case 202:case 302:
			x = widthA * 5;
			y = lengthA * 2;
			break;
		case 203:case 303:
			x = widthA * 3;
			y = lengthA * 2;
			break;
		case 204:case 304:
			x = widthA * 1;
			y = lengthA * 3;
			break;
		case 205:case 305:
			x = widthA * 7;
			y = lengthA * 9.5;
			break;
		case 206:case 306:
			x = widthA * 5;
			y = lengthA * 7;
			break;
		case 207:case 307:
			x = widthA * 3;
			y = lengthA * 7;
			break;
		case 208:case 308:
			x = widthA * 1;
			y = lengthA * 9.5;
			break;
		case 209:case 309:
			x = widthA * 5;
			y = lengthA * 8;
			break;
		case 210:case 310:
			x = widthA * 3;
			y = lengthA * 8;
			break;
		case 400:case 500:case 600:case 700:
			x = widthA * 4;
			y = lengthA * 0;
			break;
		case 401:case 501:case 601:case 701:
			x = widthA * 7;
			y = lengthA * 3;
			break;
		case 402:case 502:case 602:case 702:
			x = widthA * 5;
			y = lengthA * 2;
			break;
		case 403:case 503:case 603:case 703:
			x = widthA * 3;
			y = lengthA * 2;
			break;
		case 404:case 504:case 604:case 704:
			x = widthA * 1;
			y = lengthA * 3;
			break;
		case 405:case 505:case 605:case 705:
			x = widthA * 7;
			y = lengthA * 9.5;
			break;
		case 406:case 506:case 606:case 706:
			x = widthA * 5;
			y = lengthA * 7;
			break;
		case 407:case 507:case 607:case 707:
			x = widthA * 3;
			y = lengthA * 7;
			break;
		case 408:case 508:case 608:case 708:
			x = widthA * 1;
			y = lengthA * 9.5;
			break;
		case 409:case 509:case 609:case 709:
			x = widthA * 5;
			y = lengthA * 10;
			break;
		case 410:case 510:case 610:case 710:
			x = widthA * 3;
			y = lengthA * 10;
			break;
		default:
			System.out.println("判断球员类型出错");
			break;
		}
		double[] result = { x, y };
		return result;
	}

	private double[] getPosInRightFormation() {
		double pitchWidth = worldState.getPitchWidth();
		double pitchLength = worldState.getPitchLength();
		double widthA = pitchWidth / 8;
		double lengthA = pitchLength / 12;
		double x = -1;
		double y = -1;
		int attackState = this.getAttackState();
		int type = attackState * 100 + this.playerType;
		switch (type) {
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
			y = lengthA * 5;
			break;
		case 6:
			x = widthA * 3;
			y = lengthA * 6;
			break;
		case 7:
			x = widthA * 5;
			y = lengthA * 4;
			break;
		case 8:
			x = widthA * 7;
			y = lengthA * 5;
			break;
		case 9:
			x = widthA * 3;
			y = lengthA * 3;
			break;
		case 10:
			x = widthA * 5;
			y = lengthA * 3;
			break;
		case 100:
			x = widthA * (8 - 4);
			y = lengthA * 11;
			break;
		case 101:
			x = widthA * (8 - 7);
			y = lengthA * (11 - 3);
			break;
		case 102:
			x = widthA * (8 - 5);
			y = lengthA * (11 - 2);
			break;
		case 103:
			x = widthA * (8 - 3);
			y = lengthA * (11 - 2);
			break;
		case 104:
			x = widthA * (8 - 1);
			y = lengthA * (11 - 3);
			break;
		case 105:
			x = widthA * (8 - 7);
			y = lengthA * (11 - 9.5);
			break;
		case 106:
			x = widthA * (8 - 5);
			y = lengthA * (11 - 7);
			break;
		case 107:
			x = widthA * (8 - 3);
			y = lengthA * (11 - 7);
			break;
		case 108:
			x = widthA * (8 - 1);
			y = lengthA * (11 - 9.5);
			break;
		case 109:
			x = widthA * (8 - 5);
			y = lengthA * (11 - 8);
			break;
		case 110:
			x = widthA * (8 - 3);
			y = lengthA * (11 - 8);
			break;
		case 200:case 300:
			x = widthA * (8 - 4);
			y = lengthA * (11 - 0);
			break;
		case 201:case 301:
			x = widthA * (8 - 7);
			y = lengthA * (11 - 3);
			break;
		case 202:case 302:
			x = widthA * (8 - 5);
			y = lengthA * (11 - 2);
			break;
		case 203:case 303:
			x = widthA * (8 - 3);
			y = lengthA * (11 - 2);
			break;
		case 204:case 304:
			x = widthA * (8 - 1);
			y = lengthA * (11 - 3);
			break;
		case 205:case 305:
			x = widthA * (8 - 7);
			y = lengthA * (11 - 9.5);
			break;
		case 206:case 306:
			x = widthA * (8 - 5);
			y = lengthA * (11 - 6);
			break;
		case 207:case 307:
			x = widthA * (8 - 3);
			y = lengthA * (11 - 6);
			break;
		case 208:case 308:
			x = widthA * (8 - 1);
			y = lengthA * (11 - 9.5);
			break;
		case 209:case 309:
			x = widthA * (8 - 5);
			y = lengthA * (11 - 8);
			break;
		case 210:case 310:
			x = widthA * (8 - 3);
			y = lengthA * (11 - 8);
			break;
		case 400:case 500:case 600:case 700:
			x = widthA * (8 - 4);
			y = lengthA * (11 - 0);
			break;
		case 401:case 501:case 601:case 701:
			x = widthA * (8 - 7);
			y = lengthA * (11 - 3);
			break;
		case 402:case 502:case 602:case 702:
			x = widthA * (8 - 5);
			y = lengthA * (11 - 2);
			break;
		case 403:case 503:case 603:case 703:
			x = widthA * (8 - 3);
			y = lengthA * (11 - 2);
			break;
		case 404:case 504:case 604:case 704:
			x = widthA * (8 - 1);
			y = lengthA * (11 - 3);
			break;
		case 405:case 505:case 605:case 705:
			x = widthA * (8 - 7);
			y = lengthA * (11 - 9.5);
			break;
		case 406:case 506:case 606:case 706:
			x = widthA * (8 - 5);
			y = lengthA * (11 - 7);
			break;
		case 407:case 507:case 607:case 707:
			x = widthA * (8 - 3);
			y = lengthA * (11 - 7);
			break;
		case 408:case 508:case 608:case 708:
			x = widthA * (8 - 1);
			y = lengthA * (11 - 9.5);
			break;
		case 409:case 509:case 609:case 709:
			x = widthA * (8 - 5);
			y = lengthA * (11 - 10);
			break;
		case 410:case 510:case 610:case 710:
			x = widthA * (8 - 3);
			y = lengthA * (11 - 10);
			break;
		default:
			System.out.println("判断球员类型出错");
			break;
		}
		double[] result = { x, y };
		return result;
	}
	

}
