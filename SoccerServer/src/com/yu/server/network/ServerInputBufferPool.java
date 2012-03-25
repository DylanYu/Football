package com.yu.server.network;

import java.util.ArrayList;

public class ServerInputBufferPool {
	private ArrayList<ServerInputBuffer> pool = null;
	
	public ServerInputBufferPool(){
		pool = new ArrayList<ServerInputBuffer>();
		/**
		 * 这里为了方便在初始化过程中就给予两条buffer
		 */
		pool.add(new ServerInputBuffer());
		pool.add(new ServerInputBuffer());
	}

	public void addABuffer(){
		pool.add(new ServerInputBuffer());
	}
	
	public ServerInputBuffer getBuffer(int i){
		return pool.get(i);
	}
	
	public ServerInputBuffer getFirstBuffer(){
		return pool.get(0);
	}
	
	public ServerInputBuffer getSecondBuffer(){
		return pool.get(1);
	}
}
