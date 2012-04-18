package com.yu.server.controller;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.yu.server.network.ServerInputBuffer;
import com.yu.server.network.ServerInputBufferPool;

/**
 * 这里没有写成Pool的形式，不具有数量上的扩展性；若要扩展，只需进行少量修改
 * 
 * @author hElo
 * 
 */
public class ServerCommandBuffer {
	String buffer0;
	String buffer1;

	ServerInputBufferPool serverInputBufferPool;

	private long lastUpdateTimeA = -1;
	private long lastUpdateTimeB = -1;

	private Lock lock = new ReentrantLock();

	// private Condition notNull = lock.newCondition();

	public ServerCommandBuffer(ServerInputBufferPool serverInputBufferPool) {
		this.serverInputBufferPool = serverInputBufferPool;
		buffer0 = "";
		buffer1 = "";
	}

	public void setCommandFromServerInputBuffer() {
		setCommandFromServerInputBuffer(0);
		setCommandFromServerInputBuffer(1);
	}

	public void setCommandFromServerInputBuffer(int i) {
		if (i >= serverInputBufferPool.getPoolSize()) {
			System.out.println("ServerCommandBuffer Error::out of bound");
			return;
		}
		String s = "";
		long time = serverInputBufferPool.getBuffer(i).getLastUpdateTime();
		//如果最近一次更新的时间还是没有变，则说明客户端新的命令还没发过来，命令为空
		if (i == 0) {
			if (time == this.lastUpdateTimeA) {
				s = "";
			} else {
				s = serverInputBufferPool.getBuffer(i).getCommand();
				lastUpdateTimeA = time;
			}
		} else if (i == 1) {
			if (time == this.lastUpdateTimeB) {
				s = "";
			} else {
				s = serverInputBufferPool.getBuffer(i).getCommand();
				lastUpdateTimeB = time;
			}
		} else {
			System.out.println("ServerCommandBuffer::error");
		}
		
		lock.lock();
		if (i == 0)
			buffer0 = s;
		if (i == 1)
			buffer1 = s;
		lock.unlock();
	}

	public String getCommand() {
		StringBuffer buffer = new StringBuffer();
		lock.lock();
//		if (!buffer0.equals("")) {
			buffer.append(buffer0);
//			buffer0 = "";
//		} else {
			// TODO delete
			// System.out.println("ServerCommandBuffer::buffer0 unready");
//		}
//		if (!buffer1.equals("")) {
			buffer.append(buffer1);
//			buffer1 = "";
//		} else {
			// TODO delete
			// System.out.println("ServerCommandBuffer::buffer1 unready");
//		}
		lock.unlock();
		return buffer.toString();
	}

}
