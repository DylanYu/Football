package com.yu.client.network;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientOutputBuffer {
	private ArrayList<String> buffer = null;
	
	private Lock lock = new ReentrantLock();

	private Condition notEmpty = lock.newCondition();
	
	public ClientOutputBuffer(){
		buffer = new ArrayList<String>();
	}
	
	public void add(String s){
		lock.lock();
		buffer.add(s);
		notEmpty.signal();
		lock.unlock();
		
		//TODO delete
		//		System.out.println("ClientOutPutBuffer::"+buffer.size());
	}
	
	public int getSize(){
		return buffer.size();
	}
	
	public String getThenRemove(){
		String s = null;
		lock.lock();
		try{
			while (buffer.isEmpty()){
//				System.out.println("Wait for ClientOutputBuffer's notEmpty condition");
				notEmpty.await();
			}
			s = buffer.get(0);
			buffer.remove(0);
		} catch (InterruptedException ex){
			ex.printStackTrace();
		} finally {
			lock.unlock();
			return s;
		}
	}
	
	public boolean isEmpty(){return buffer.isEmpty();}

	public boolean remove(){
		if(buffer.isEmpty())
			return false;
		else {
			buffer.remove(0);
			return true;
		}
	}
	
	public String getCommand(){
		if(buffer.isEmpty())
			return null;
		else
			return buffer.get(0);
	}
}
