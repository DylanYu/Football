package com.yu.client.agent;

import com.yu.basicelements.Side;
import com.yu.basicelements.Util;
import com.yu.overallsth.Ball;
import com.yu.overallsth.Pitch;
import com.yu.overallsth.Player;
import com.yu.overallsth.Values;

/**
 * diary4/6:增加kick，固有意识kickableMargin
 * 	增加keepFormation，在没有其他事情做时保持队形
 * 
 * diary4/7:为了解决misskick现象，在agent端将kickableMargin变小
 * 限制agent更加接近球才可以踢到，这样server端可以“宽容”agent的踢球miss情况
 * @author hElo
 * 
 */
public class Agent implements Runnable {

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
	
	
	
	// TODO 约定type的数值
	// 在队伍中的作用,用于确定大约的位置
	private int playerType;

	// 一些固有意识
	//TODO 同步kickableMargin
	private double kickableMargin = 2;

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
//			ownScore = worldState.ge;
//			oppoScore;
			
			act();

			// TODO !!!client agent sleep a while
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void act() {
		String command = null;
		//不是离球最近的本方球员，则保持队形
		if(!isClosestToBall()){
			command = keepFormation();
		}else {
			double x = worldState.getOwnPosition().getX();
			double y = worldState.getOwnPosition().getY();
			double ballX = worldState.getBallPosition().getX();
			double ballY = worldState.getBallPosition().getY();
			double dis2ball = Util.calDistance(x, y, ballX, ballY);
			if (dis2ball <= this.kickableMargin) {
				if(isFrontest())
					command = shootToCenterWithMaxPower();
				else
//					command = passForward();
					command = passWithSelection();
			} else {
				command = chaseBall();
			}
		}
		
		// //////////////////////////
		if(command != null)
			wrapCommandToBuffer(command);
		else{
			System.out.println("Agent:" + this.side + "," + this.NO +", missed a chance to act");
			return;
		}
	}
	
	private String passWithSelection(){
		double cutAvoidance = 0.5;
		int NO = passSelection(cutAvoidance);
		Player []p = this.getOwnPlayers();
		double ballX = this.ball.getPosition().getX();
		double ballY = this.ball.getPosition().getY();
//		double x1 = this.self.getPosition().getX();
//		double y1 = this.self.getPosition().getX();
		double x2 = p[NO].getPosition().getX();
		double y2 = p[NO].getPosition().getY();
		double distance = Util.calDistance(ballX, ballY, x2, y2);
		double angle = Util.calAngle(ballX, ballY, x2, y2);
		double kickPower = dstsToKickPower(distance);
		return kick(angle, kickPower);
	}
	
	private String passForward(){
		double maxKickPower = Values.MAX_KICK_POWER;
		double kickPower = maxKickPower;
		
//		double kickPower = Values.MAX_KICK_POWER / 3;
		Player []p;
		Player self = worldState.getSelf();
		if(this.side == Side.LEFT)
			//这里的get有clone，应该有效
			p = worldState.getPlayer0();
		else 
			p = worldState.getPlayer1();
		
		for(int i = 0; i < p.length; i++){
			if(this.side == Side.LEFT){
				if(p[i].getPosition().getY() <= self.getPosition().getY()){
					p[i] = null;
				}
			}
			if(this.side == Side.RIGHT){
				if(p[i].getPosition().getY() >= self.getPosition().getY()){
					p[i] = null;
				}
			}
		}
		int sNO = 0;
		double sDistance = 1000;
		double x = self.getPosition().getX();
		double y = self.getPosition().getY();
		//找到最前面的队友，排除最前面是自己的情况
		for(int i = 0; i < p.length; i++){
			if(p[i] != null && i != this.NO){
				double distance = Util.calDistance(x, y, p[i].getPosition().getX(), p[i].getPosition().getY());
				if(distance < sDistance){
					sDistance = distance;
					sNO = i;
				}
			}
		}
		if(sDistance != 1000){
			double angle = Util.calAngle(x, y, p[sNO].getPosition().getX(), p[sNO].getPosition().getY());
			kickPower = dstsToKickPower(sDistance);
			return kick(angle, kickPower);
		}
		else 
			return null;
	}

	private String shootToCenterWithMaxPower() {
		double shootCutAvoidanceSafeDistance = 2;
		double kickPower = Values.MAX_KICK_POWER / 4 * 3;
//		double kickPower = Values.MAX_KICK_POWER / 2;
		double goalCenterX = 0;
		double goalCenterY = 0;
		goalCenterX = worldState.getPitchWidth() / 2;
		if (this.side == Side.LEFT) {
			goalCenterY = worldState.getPitchLength();
		} else
			goalCenterY = 0;
		double ballX = worldState.getBallPosition().getX();
		double ballY = worldState.getBallPosition().getY();
		//shoot cut avoidance
		int num = getNumOfOppoCutter(ballX, ballY, goalCenterX, goalCenterY, shootCutAvoidanceSafeDistance);
		if(num > 0){
			//TODO 无法完成射门，则随意回传
			kickPower = kickPower / 2;
			double angle = 0;
			if(this.side == Side.LEFT)
				angle = Math.PI * -1 * 0.75;
			else 
				angle = Math.PI * 0.75;
			return kick(angle, kickPower);
		}
		else{
			double angle = Util.calAngle(ballX, ballY, goalCenterX, goalCenterY);
			return kick(angle, kickPower);
		}
		
	}

	private String chaseBall() {
		// double dashPower = Str.MAX_DASH_POWER;
		double dashPower = Values.NORMAL_DASH_POWER;

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
	 * @return
	 */
	private String keepFormation() {
		double[] p = getPosWithBallAttraction();
//		double[] p = getPos(this.side);
		double x0 = worldState.getOwnPosition().getX();
		double y0 = worldState.getOwnPosition().getY();
		double x1 = p[0];
		double y1 = p[1];
		double distance = Util.calDistance(x0, y0, x1, y1);
		
		//TODO 参数待测试 距离与dashPower
		double dashPower = distance / 2.5;
		double angle = Util.calAngle(x0, y0, x1, y1);
		return dash(angle, dashPower);
	}
	
	/**
	 * 传球对象选择。现在只考虑了1、避免在球运行过程中被截掉 2、离传球队员尽可能近。
	 * diary4/11:仍然有问题
	 * @param cutAvoidance等级：从1开始，数字越大表示越保守，需要防止的范围越大。现在规定1表示5M，2表示10M
	 * @return 己方队员号码
	 */
	//TODO
	private int passSelection(double cutAvoidance){
		double standard = 10;
		double safeDistance = 0;
		safeDistance = cutAvoidance * 5;
		Player p[] = this.getOwnPlayers();
		double []preferenceForcutAvoidance = new double[p.length];
		for(int i = 0; i < p.length; i++){
			if(p[i].getNO() != this.NO){
				int numOfOppoCutter = 0;
				numOfOppoCutter = getNumOfOppoCutter(this.self.getPosition().getX(), this.self.getPosition().getY(),
						p[i].getPosition().getX(), p[i].getPosition().getY(), safeDistance);
				//TODO delete
				System.out.println(this.side + "," + this.NO + " to " + p[i].getNO()+" 's numOfOppoCutter : " + numOfOppoCutter);
				//TODO must improve
//				preferenceForcutAvoidance[i] = (11.0 - numOfOppoCutter) / 11.0 * standard ;
				if(numOfOppoCutter >= 1)
					preferenceForcutAvoidance[i] = 0;
				else 
					preferenceForcutAvoidance[i] = standard * 2;
			}
			else
				//表示不能传给自己
				preferenceForcutAvoidance[i] = -1000;
		}
		
		double []preferenceForFront = new double[p.length];
		for(int i = 0; i < p.length; i++){
			if(p[i].getNO() != this.NO){
				if(this.side == Side.LEFT)
					preferenceForFront[i] = (p[i].getPosition().getY() - this.self.getPosition().getY() ) / this.pitchLength * standard;
				else
					preferenceForFront[i] = (this.self.getPosition().getY() - p[i].getPosition().getY()) / this.pitchLength * standard;
			}
			else 
				//表示不能传给自己
				preferenceForFront[i] = -1000;
		}
		
		double []preference = new double[p.length];
		int maxN = -1;
		double max = -1;
		for(int i = 0; i < p.length; i++){
			preference[i] = preferenceForcutAvoidance[i] + preferenceForFront[i];
			if(preference[i] > max){
				max = preference[i];
				maxN = i;
			}
		}
		//TODO delete
//		System.out.println(this.side + "," + this.NO +"'s pass preference is " + maxN);
		
		return maxN;
		
		
	}
	
	/**
	 * 获得传球路线上存在的对手可能截球者数量
	 * @return
	 */
	private int getNumOfOppoCutter(double x1, double y1, double x2, double y2, double safeDistance){
		int num = 0;
		double A = y2 - y1;
		double B = x1 - x2;
		double C = - x1 * y2 + x2 * y1;
		Player p[];
		if(this.side == Side.LEFT)
			p = this.player1;
		else 
			p = this.player0;
		for(int i = 0; i < p.length; i++){
			double x = p[i].getPosition().getX();
			double y = p[i].getPosition().getY();
			double distance = Util.calDistanceFromPointToLine(x, y, A, B, C);
			if(distance <= safeDistance){
				double []pos = Util.getIntersectionFromPotToLine(x, y, A, B, C);
				if(Util.isANumBetweenTwoNums(pos[0], x1, x2) && Util.isANumBetweenTwoNums(pos[1], y1, y2));
					num++;
			}
		}
		return num;
	}
	

	
	private boolean isFrontest(){
		Player []p;
		if(this.side == Side.LEFT){
			p = player0;
			for(int i = 0; i < p.length; i++){
				if(p[i].getPosition().getY() > self.getPosition().getY())
					return false;
			}
			return true;
		}
		else{
			p = player1;
			for(int i = 0; i < p.length; i++){
				if(p[i].getPosition().getY() < self.getPosition().getY())
					return false;
			}
			return true;
		}
	}
	
	private double[] getPosWithBallAttraction(){
		//TODO 引力参数
		/*
		 * 事实证明，这个数值设定是合理的
		 */
		double k = 1.2;
		
		//TODO alternative
		double[] p = getTestPos(this.side);
//		double[] p = getPos(this.side);
		double x = p[0];
		double y = p[1];
		double ballX = worldState.getBallPosition().getX();
		double ballY = worldState.getBallPosition().getY();
		x = ((k - 1) * ballX + x) / k;
		y = ((k - 1) * ballY + y) / k;
		double []result = {x, y};
		return result;
	}

	private double[] getTestPos(Side side){
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
			y = lengthA * 1;
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
			y = lengthA * 11;
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
	
	private double[] getPosInLeftFormation() {
		double pitchWidth = worldState.getPitchWidth();
		double pitchLength = worldState.getPitchLength();
		double widthA = pitchWidth / 8;
		double lengthA = pitchLength / 12;
		double x = -1;
		double y = -1;
		switch (this.playerType) {
		case 0:
			x = widthA * 4;
			y = lengthA * 1;
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
			y = lengthA * 6;
			break;
		case 7:
			x = widthA * 3;
			y = lengthA * 6;
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
		switch (this.playerType) {
		case 0:
			x = widthA * 4;
			y = lengthA * 11;
			break;
		case 1:
			x = widthA * 1;
			y = lengthA * 9;
			break;
		case 2:
			x = widthA * 3;
			y = lengthA * 10;
			break;
		case 3:
			x = widthA * 5;
			y = lengthA * 10;
			break;
		case 4:
			x = widthA * 7;
			y = lengthA * 9;
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
			y = lengthA * 6;
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
	private boolean isClosestToBall() {
		Player self;
		self = worldState.getSelf();
		Player[] player;
		if (this.side == Side.LEFT)
			player = worldState.getPlayer0();
		else
			player = worldState.getPlayer1();
		double ballX = worldState.getBall().getPosition().getX();
		double ballY = worldState.getBall().getPosition().getY();
		double myDistance = Util.calDistance(ballX, ballY, self.getPosition()
				.getX(), self.getPosition().getY());
		double d;
		for (int i = 0; i < player.length; i++) {
			d = Util.calDistance(ballX, ballY, player[i].getPosition().getX(),
					player[i].getPosition().getY());
			if (d < myDistance)
				return false;
		}
		return true;
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
	 * @param angle
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

	
	
	private Player[] getOwnPlayers(){
		if(this.side == Side.LEFT)
			return this.player0;
		else
			return this.player1;
	}
	/**
	 *
	 * 根据距离远近确定踢球力量大小
	 * @param distance
	 * @return
	 */
	//TODO 待测试
	private double dstsToKickPower(double distance){
		double max = Values.MAX_KICK_POWER;
		if(distance < 10)
			return max / 4;
		else if(distance < 20)
			return max / 3;
		else if(distance < 50)
			return max / 2;
		else
			return max / 3 * 2;
	}
}
