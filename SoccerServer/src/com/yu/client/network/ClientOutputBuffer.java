package com.yu.client.network;

import java.util.ArrayList;

public class ClientOutputBuffer {
	private ArrayList<String> buffer = null;
	public ClientOutputBuffer(){
		buffer = new ArrayList<String>();
	}
	
	public void add(String s){
		buffer.add(s);
	}
	
	public int getSize(){
		return buffer.size();
	}
	
	public String getThenRemove(){
		if(buffer.isEmpty())
			return null;
		else {
			String s = buffer.get(0);
			buffer.remove(0);
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
