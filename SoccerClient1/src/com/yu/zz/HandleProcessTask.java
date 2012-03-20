package com.yu.zz;

import java.io.DataOutputStream;
import java.io.IOException;

public class HandleProcessTask implements Runnable {
//	class processTask extends TimerTask{
//		
//	}
	private DataOutputStream outputToServer;
	private String outString;
	private static final int processTime = 10;
	
	public HandleProcessTask(DataOutputStream outputToServer, String outString){
		this.outputToServer = outputToServer;
		this.outString = outString;
	}
	public void run(){
		try {
			Thread.sleep(processTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			outputToServer.writeUTF(outString);
			outputToServer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("HandleProcess: " + outString);
	}
}
