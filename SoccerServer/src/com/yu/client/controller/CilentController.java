package com.yu.client.controller;

import com.yu.basicelements.Side;
import com.yu.client.network.ClientInputBuffer;
import com.yu.client.network.ClientOutputBuffer;
import com.yu.overallsth.Ball;
import com.yu.overallsth.Pitch;
import com.yu.overallsth.Player;
import com.yu.overallsth.Str;

public class CilentController implements Runnable {

	private String COMMAND_UP = Str.COMMAND_UP;

	Pitch pitch;
	//TODO finally remove
	ClientOutputBuffer outputBuffer;
	//
	ClientInputBuffer inputBuffer;
	int width;
	int height;

	boolean isRunning = true;

	public CilentController(Pitch pitch, ClientOutputBuffer outputBuffer,
			ClientInputBuffer inputBuffer, int width, int height) {
		super();
		this.pitch = pitch;
		this.outputBuffer = outputBuffer;
		this.inputBuffer = inputBuffer;
		this.width = width;
		this.height = height;
	}

	public void run() {
		while (isRunning) {
			//内有自动sleep，不用显示睡眠
			updateState();
		}
	}

	/**
	 * 根据服务器传过来的信息更新当前屏幕状态
	 */
	private void updateState() {
		String in = inputBuffer.getThenRemove();
		if (in == null)
			System.out.println("ClientInputBuffer error in controller");
		String s[] = in.split("\\|");
		// state
		
		long gameTime = Long.parseLong(s[0]);
		
		int score0 = Integer.parseInt(s[1]);
		int score1 = Integer.parseInt(s[2]);
		pitch.setGameTime(gameTime);
		pitch.setScore0(score0);
		pitch.setScore1(score1);

		// ball
		String b[] = s[3].split(";");
		double ballPX = Double.parseDouble(b[0]);
		double ballPY = Double.parseDouble(b[1]);
		double ballSpeedX = Double.parseDouble(b[2]);
		double ballSpeedY = Double.parseDouble(b[3]);
		Ball ball = new Ball();
		ball.setPosition(ballPX, ballPY);
		ball.setSpeed(ballSpeedX, ballSpeedY);
		pitch.setBall(ball);
		
		for (int i = 4; i < s.length - pitch.getNumOfPlayer(); i++) {
			String p[] = s[i].split(";");
			int side= Integer.parseInt(p[0]);
			int NO = Integer.parseInt(p[1]);
			double px = Double.parseDouble(p[2]);
			double py = Double.parseDouble(p[3]);
			double speedX = Double.parseDouble(p[4]);
			double speedY = Double.parseDouble(p[5]);
			Player player = new Player();
			if(side == 0)
				player.setSide(Side.LEFT);
			else player.setSide(Side.RIGHT);
			player.setNO(NO);
			player.setPosition(px, py);
			player.setSpeed(speedX, speedY);
			pitch.setPlayer(Side.LEFT, NO, player);
		}
		for (int i = 4 + pitch.getNumOfPlayer(); i < s.length ; i++) {
			String p[] = s[i].split(";");
			int side= Integer.parseInt(p[0]);
			int NO = Integer.parseInt(p[1]);
			double px = Double.parseDouble(p[2]);
			double py = Double.parseDouble(p[3]);
			double speedX = Double.parseDouble(p[4]);
			double speedY = Double.parseDouble(p[5]);
			Player player = new Player();
			if(side == 0)
				player.setSide(Side.LEFT);
			else player.setSide(Side.RIGHT);
			player.setNO(NO);
			player.setPosition(px, py);
			player.setSpeed(speedX, speedY);
			pitch.setPlayer(Side.RIGHT, NO, player);
		}
	}

	/**
	 * 变化一次
	 */
	public void upOnce() {
		outputBuffer.add(COMMAND_UP);
	}


	public void stopRunning() {
		isRunning = false;
	}
}
