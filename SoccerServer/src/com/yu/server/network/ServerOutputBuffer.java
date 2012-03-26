package com.yu.server.network;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerOutputBuffer {
	private ArrayList<double[]> buffer = null;

	private Lock lock = new ReentrantLock();

	private Condition notEmpty = lock.newCondition();

	public ServerOutputBuffer() {
		buffer = new ArrayList<double[]>();
	}

	public int getSize() {
		return buffer.size();
	}

	public void add(double a, double b, double c, double d) {
		lock.lock();
		double[] t = new double[] { a, b, c, d };
		buffer.add(t);
		// 唤醒
		notEmpty.signal();
		lock.unlock();
	}

	// TODO ?
	public double[] getThenRemove() {
		double[] t = null;
		lock.lock();
		try {
			while (buffer.isEmpty()) {
				System.out.println("Wait for ServerOutputBuffer's notEmpty condition!");
				// 等待
				notEmpty.await();
			}
			t = buffer.get(0);
			buffer.remove(0);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} finally {
			lock.unlock();
			return t;
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

	public double[] getSignal() {
		if (buffer.isEmpty())
			return null;
		else
			return buffer.get(0);
	}
}
