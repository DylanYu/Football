package com.yu.client.agent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AgentOutputBuffer {
	int numOfSlot;
	StringBuffer buffer[];

	private Lock lock = new ReentrantLock();

	private Condition notFull = lock.newCondition();

	public AgentOutputBuffer(int numOfSlot) {
		super();
		this.numOfSlot = numOfSlot;
		buffer = new StringBuffer[numOfSlot];
		for (int i = 0; i < numOfSlot; i++) {
			buffer[i] = new StringBuffer("");
		}
	}

	public void add(int location, StringBuffer sb) {
		if (location < 0 || location >= numOfSlot) {
			System.out.println("AgentOutputBuffer ERROR!!!!");
			return;
		}
		lock.lock();
		if (!buffer[location].toString().equals("")){
			//TODO delete
//			System.out.println(location + "--------AgentOutputBuffer::COVER ORIGIN---------");
		}
//		System.out.println(sb);
		buffer[location] = sb;
		notFull.signal();
		lock.unlock();
	}

	public String getAllThenRemove() {
		StringBuffer b = new StringBuffer();
		lock.lock();
		try{
			while (!this.isReady()){
//				System.out.println("Wait for AgentOutputBuffer's notFull condition");
				notFull.await();
			}
			for (int i = 0; i < numOfSlot; i++) {
				b.append(this.buffer[i]);
				this.buffer[i] = new StringBuffer("");
//				System.out.println("Remove Once------------");
			}
		}catch (InterruptedException ex){
			ex.printStackTrace();
		} finally {
			lock.unlock();
			return b.toString();
		}
		
	}

	public boolean isReady() {
		lock.lock();
		try {
			for (int i = 0; i < numOfSlot; i++)
				if (buffer[i].toString().equals(""))
					return false;
			return true;
		} finally {
			lock.unlock();
		}
	}

}
