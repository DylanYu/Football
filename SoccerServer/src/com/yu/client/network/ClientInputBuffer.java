package com.yu.client.network;

import java.util.ArrayList;

public class ClientInputBuffer {
	private ArrayList<double[]> buffer = null;

	public ClientInputBuffer() {
		buffer = new ArrayList<double[]>();
	}

	public int getSize(){
		return buffer.size();
	}
	
	public void add(double a, double b, double c, double d) {
		double[] t = { a, b, c, d };
		buffer.add(t);
	}

	public double[] getThenRemove() {
		if (buffer.isEmpty())
			return null;
		else {
			double[] t = buffer.get(0);
			buffer.remove(0);
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
