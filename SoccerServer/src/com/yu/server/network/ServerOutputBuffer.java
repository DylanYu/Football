package com.yu.server.network;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//TODO !!!!!!!!!
/**
 * 每当有输出请求时，验证当前缓存内信息是否已被读取一次。若是，则读取当前信息并删除，并且置读取次数为0，若没有没读过，则读取并置读取次数为1.
 * 初始化numOfClient为2
 * @author hElo
 *
 */
public class ServerOutputBuffer {
	private ArrayList<String> buffer = null;

	private int numOfClient = 0;
	
	private Lock lock = new ReentrantLock();

	private Condition notEmpty = lock.newCondition();

	public ServerOutputBuffer() {
		buffer = new ArrayList<String>();
		
		this.numOfClient = 0;
	}

	public void addAClient(){
		this.numOfClient ++;
	}
	public int getSize() {
		return buffer.size();
	}

	public void add(String s) {
		lock.lock();
		buffer.add(s);
		// 唤醒
		notEmpty.signal();
		lock.unlock();
		
		//TODO delete
		//System.out.println("ServerOutputBuffer::"+buffer.size());
	}

	public String getThenRemove() {
		String s = null;
		lock.lock();
		try {
			while (buffer.isEmpty()) {
//				System.out.println("Wait for ServerOutputBuffer's notEmpty condition!");
				// 等待
				notEmpty.await();
			}
			s = buffer.get(0);
			buffer.remove(0);
			
			//TODO delete
//			System.out.println("ServerOutput Once");
		
		
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} finally {
			lock.unlock();
			return s;
		}
	}

	public boolean isEmpty() {
		return buffer.isEmpty();
	};

	public boolean remove() {
		if (buffer.isEmpty())
			return false;
		else {
			buffer.remove(0);
			return true;
		}
	}

	public String getSignal() {
		if (buffer.isEmpty())
			return null;
		else
			return buffer.get(0);
	}

	public int getNumOfClient() {
		return numOfClient;
	}

	public void setNumOfClient(int numOfClient) {
		this.numOfClient = numOfClient;
	}
}
