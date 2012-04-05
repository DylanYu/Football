package com.yu.server.network;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 为了实现的准确性，将这个buffer改造成两条队列，内容相同。
 * 这里不用写成pool的原因是，传入的内容是一样的
 * 
 * diary:
 * 一开始准备用轮询的方式：首先0读，然后1读，如此循环，然而效果不好，再加上同步没有办法做成。
 * 后来改成引入第二条buffer，每次读入都同时更新这两条buffer，然后分别由不同ServerOut负责传输。
 * 这样做产生的效果很好。
 * 
 * @author hElo
 *
 */
public class ServerOutputBuffer {
	private ArrayList<String> buffer0 = null;
	private ArrayList<String> buffer1 = null;

	private int numOfClient = 0;
	
	private int readTime = 0;
	
	private Lock lock = new ReentrantLock();

	private Condition notEmpty0 = lock.newCondition();
	private Condition notEmpty1 = lock.newCondition();

	public ServerOutputBuffer() {
		buffer0 = new ArrayList<String>();
		buffer1 = new ArrayList<String>();
		
		this.numOfClient = 0;
	}

	public void addAClient(){
		this.numOfClient ++;
	}
	public int getSize0() {
		return buffer0.size();
	}
	
	public int getSize1() {
		return buffer1.size();
	}

	public void add(String s) {
		lock.lock();
		buffer0.add(s);
		buffer1.add(s);
		// 唤醒
		notEmpty0.signal();
		notEmpty1.signal();
		lock.unlock();
		
		//TODO delete
		//System.out.println("ServerOutputBuffer::"+buffer.size());
	}

	public String getThenRemove(int noOfClient) {
		if(noOfClient == 0 )
			return getThenRemove0();
		else if(noOfClient == 1){
			return getThenRemove1();
		} else {
			System.out.println("ServerOutputBuffer::getThenRemove() Error"); 
			return null;
		}
	}
	public String getThenRemove0() {
		String s = null;
		lock.lock();
		try {
			while (buffer0.isEmpty()) {
//				System.out.println("Wait for ServerOutputBuffer's notEmpty condition!");
				// 等待
				notEmpty0.await();
			}
			s = buffer0.get(0);
			buffer0.remove(0);
			//TODO delete
			System.out.println("0::ServerOutput Once");
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} finally {
			lock.unlock();
			return s;
		}
	}
	
	public String getThenRemove1() {
		String s = null;
		lock.lock();
		try {
			while (buffer1.isEmpty()) {
//				System.out.println("Wait for ServerOutputBuffer's notEmpty condition!");
				// 等待
				notEmpty1.await();
			}
			s = buffer1.get(0);
			buffer1.remove(0);
			//TODO delete
			System.out.println("1::ServerOutput Once");
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} finally {
			lock.unlock();
			return s;
		}
	}

	public boolean isEmpty0() {
		return buffer0.isEmpty();
	};

	public boolean remove0() {
		if (buffer0.isEmpty())
			return false;
		else {
			buffer0.remove(0);
			return true;
		}
	}
	
	public boolean isEmpty1() {
		return buffer1.isEmpty();
	};

	public boolean remove1() {
		if (buffer1.isEmpty())
			return false;
		else {
			buffer1.remove(0);
			return true;
		}
	}

	public String getSignal() {
		if (buffer0.isEmpty())
			return null;
		else
			return buffer0.get(0);
	}

	public int getNumOfClient() {
		return numOfClient;
	}

	public void setNumOfClient(int numOfClient) {
		this.numOfClient = numOfClient;
	}
}
