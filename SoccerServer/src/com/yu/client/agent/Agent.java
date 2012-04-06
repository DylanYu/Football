package com.yu.client.agent;

import com.yu.basicelements.Side;
import com.yu.basicelements.Util;
import com.yu.overallsth.Pitch;
import com.yu.overallsth.Player;
import com.yu.overallsth.Str;

/**
 * diary4/6:增加kick，固有意识kickableMargin
 * 	增加keepFormation，在没有其他事情做时保持队形
 * 
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

	// TODO 约定type的数值
	// 在队伍中的作用,用于确定大约的位置
	private int playerType;

	// 一些固有意识
	private double kickableMargin = 5;

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
				command = shootToCenterWithMaxPower();
			} else {
				command = chaseBall();
			}
		}
		

		// //////////////////////////
		wrapCommandToBuffer(command);
	}

	private String shootToCenterWithMaxPower() {
		double kickPower = Str.MAX_KICK_POWER / 2;
		double goalCenterX = 0;
		double goalCenterY = 0;
		goalCenterX = worldState.getPitchWidth() / 2;
		if (this.side == Side.LEFT) {
			goalCenterY = worldState.getPitchLength();
		} else
			goalCenterY = 0;
		double ballX = worldState.getBallPosition().getX();
		double ballY = worldState.getBallPosition().getY();
		double angle = Util.calAngle(ballX, ballY, goalCenterX, goalCenterY);
		return kick(angle, kickPower);
	}

	private String chaseBall() {
		// double dashPower = Str.MAX_DASH_POWER;
		double dashPower = Str.NORMAL_DASH_POWER;

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
		//TODO 参数待测试
		double dashPower = distance / 0.9;
		double angle = Util.calAngle(x0, y0, x1, y1);
		return dash(angle, dashPower);
	}
	
	private double[] getPosWithBallAttraction(){
		//TODO 引力参数
		/*
		 * 事实证明，这个数值设定是合理的
		 */
		double k = 1.2;
		
		double[] p = getPos(this.side);
		double x = p[0];
		double y = p[1];
		double ballX = worldState.getBallPosition().getX();
		double ballY = worldState.getBallPosition().getY();
		x = ((k - 1) * ballX + x) / k;
		y = ((k - 1) * ballY + y) / k;
		double []result = {x, y};
		return result;
	}

	private double[] getPos(Side side) {
		if (side == Side.LEFT)
			return getPosInLeftFormation();
		else
			return getPosInRightFormation();
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

}
