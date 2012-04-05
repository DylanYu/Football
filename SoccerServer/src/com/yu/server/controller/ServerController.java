package com.yu.server.controller;

import java.util.Date;

import com.yu.basicelements.Side;
import com.yu.overallsth.Pitch;
import com.yu.overallsth.Player;
import com.yu.overallsth.Str;
import com.yu.server.network.ServerInputBuffer;
import com.yu.server.network.ServerInputBufferPool;
import com.yu.server.network.ServerOutputBuffer;

public class ServerController implements Runnable {

	double DASH_POWER_TO_ACC = 50;
	private Pitch pitch = null;
	private ServerOutputBuffer outputBuffer = null;
	private ServerInputBufferPool inputBufferPool = null;
	private ServerCommandBuffer serverCommandBuffer;
	private ServerCommand serverCommand;
	
	private int width;
	private int height;
	
	private int frequency = -1;

	boolean isRunning = true;
	
	public ServerController(Pitch pitch, ServerOutputBuffer outputBuffer,
			ServerInputBufferPool inputBufferPool, int width, int height, int frequency) {
		super();
		this.outputBuffer = outputBuffer;
		this.inputBufferPool = inputBufferPool;
		this.pitch = pitch;
		this.width = width;
		this.height = height;
		this.frequency = frequency;
		
		
	}

	@Override
	public void run() {
		//生成serverCommandBuffer，并执行serverCommand；
		serverCommandBuffer = new ServerCommandBuffer(inputBufferPool);
		serverCommand = new ServerCommand(serverCommandBuffer);
		new Thread(serverCommand).start();
				
		while (isRunning) {
			move();
			setOutput();
			//System.out.println("X:" + pitch.getBallPX() + ";Y:" + pitch.getBallPY() + ";SX:" + pitch.getBallSpeedX() + ";SY:" + pitch.getBallSpeedY());
			try {
				//TODO 这是全局频率，相当重要
				Thread.sleep(1000/frequency);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void move() {

		String t = serverCommandBuffer.getCommand();
		actCommands(t);
		
		
		
		pitch.calAllObjectsNextCycle();
		
	}
	
	private void actCommands(String commands){
		if(commands.equals("")){
			System.out.println("ServerController:: command is null");
			return;
		}
		String command[] = commands.split("\\|");
		for(int i = 0; i < command.length; i++){
			actOneCommand(command[i]);
		}
	}
	private void actOneCommand(String command){
		if(command.equals(Str.COMMAND_UP)){
			pitch.initPitchRandomly();
		}
		else {
			Side side;
			String s[] = command.split(",");
			if(s[0].equals("0"))
				side = Side.LEFT;
			else 
				side = Side.RIGHT;
			int NO = Integer.parseInt(s[1]);
			
			if(s[2].equals("dash")){
				double angle = Double.parseDouble(s[3]);
				double power = Double.parseDouble(s[4]);
				actDash(side, NO, angle, power);
			}
			//...
		}
	}

	private void actDash(Side side, int NO, double angle, double power){
		//TODO 50;
		double ac = power / DASH_POWER_TO_ACC; 
		double ax = ac * Math.cos(angle); 
		double ay = ac * Math.sin(angle);

		Player player = pitch.getPlayer(side, NO);
		player.makeAcc(ax, ay);
	}
	
	/**
	 * 输出信息
	 */
	private void setOutput() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(System.currentTimeMillis() + "|");
		buffer.append(pitch.getScore0() + "|");
		buffer.append(pitch.getScore1() + "|");
		//
		buffer.append(pitch.getBallPX() + ";");
		buffer.append(pitch.getBallPY() + ";");
		buffer.append(pitch.getBallSpeedX() + ";");
		buffer.append(pitch.getBallSpeedY() + ";");
		buffer.append("|");
		//
		Player player0[] = pitch.getAllPlayer(Side.LEFT);
		Player player1[] = pitch.getAllPlayer(Side.RIGHT);
		for(int i = 0; i < player0.length; i++){
			buffer.append("0" + ";");
			buffer.append(i + ";");
			buffer.append(player0[i].getPosition().getX() + ";");
			buffer.append(player0[i].getPosition().getY() + ";");
			buffer.append(player0[i].getSpeed().getSpeedX() + ";");
			buffer.append(player0[i].getSpeed().getSpeedY() + ";");
			//to be continued
			buffer.append("|");
		}
		for(int i = 0; i < player1.length; i++){
			buffer.append("0" + ";");
			buffer.append(i + ";");
			buffer.append(player1[i].getPosition().getX() + ";");
			buffer.append(player1[i].getPosition().getY() + ";");
			buffer.append(player1[i].getSpeed().getSpeedX() + ";");
			buffer.append(player1[i].getSpeed().getSpeedY() + ";");
			//to be continued
			buffer.append("|");
		}
		
		outputBuffer.add(buffer.toString());
	}
	
	public void stopRunning(){
		isRunning = false;
		
		serverCommand.stoRunning();
	}
	
}
