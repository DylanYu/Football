package com.yu.network;

import java.io.DataOutputStream;
import java.io.IOException;

import com.yu.overallsth.Str;

public class ClientNetworkOut implements Runnable{
	DataOutputStream outputToServer = null;
	ClientOutputBuffer outputBuffer = null;

	private String noCommand = Str.NO_COMMAND;
	
	public ClientNetworkOut(DataOutputStream outputToServer, ClientOutputBuffer outputBuffer){
		this.outputToServer = outputToServer;
		this.outputBuffer = outputBuffer;
	}
	
	public void run(){
		try{
			while(true){
				//如果输出队列不空则向服务器传输上升命令，否则休息，这是为了节省资源（考虑到游戏数据的实际情况）
				if(!outputBuffer.isEmpty()){
					//TODO synchronized
					String s = outputBuffer.getThenRemove();
					if (s == null)
						System.out.println("ClientOutputBuffer error");
					outputToServer.writeUTF(s);
					outputToServer.flush();
				}else {
					//TODO
					Thread.sleep(80);
				}
			}
		}catch(IOException ex){
			ex.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
