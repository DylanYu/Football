package com.yu.client.network;

import java.io.DataOutputStream;
import java.io.IOException;


public class ClientNetworkOut implements Runnable{
	DataOutputStream outputToServer = null;
	ClientOutputBuffer outputBuffer = null;

	boolean isRunning = true;
	//private String noCommand = Str.NO_COMMAND;
	
	public ClientNetworkOut(DataOutputStream outputToServer, ClientOutputBuffer outputBuffer){
		this.outputToServer = outputToServer;
		this.outputBuffer = outputBuffer;
	}
	
	public void run(){
		try{
			while(isRunning){
				//如果输出队列不空则向服务器传输上升命令，否则休息，这是为了节省资源（考虑到游戏数据的实际情况）
					String s = outputBuffer.getThenRemove();
					if (s == null)
						System.out.println("ClientOutputBuffer error");
					outputToServer.writeUTF(s);
					outputToServer.flush();
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void stopRunning(){
		isRunning = false;
	}
}
