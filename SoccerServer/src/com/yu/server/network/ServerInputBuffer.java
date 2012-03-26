package com.yu.server.network;

import java.util.ArrayList;

public class ServerInputBuffer {
	private ArrayList<String> buffer = null;

//	private Lock lock = new ReentrantLock();
//
//	private Condition notEmpty = lock.newCondition();

	
	public ServerInputBuffer() {
		buffer = new ArrayList<String>();
	}

	public int getSize(){
		return buffer.size();
	}
	
	public void add(String s) {
		buffer.add(s);
	}

	public String getThenRemove() {
		if (buffer.isEmpty())
			return null;
		else {
			String s = buffer.get(0);
			buffer.remove(0);
			return s;
		}
	}

	public boolean isEmpty() {
		return buffer.isEmpty();
	}

	public boolean remove() {
		if (buffer.isEmpty())
			return false;
		else {
			buffer.remove(0);
			return true;
		}
	}

	public String getCommand() {
		if (buffer.isEmpty())
			return null;
		else
			return buffer.get(0);
	}
}
