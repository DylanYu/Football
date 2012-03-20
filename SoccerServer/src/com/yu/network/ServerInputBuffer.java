package com.yu.network;

import java.util.ArrayList;

import com.yu.overallsth.Str;

public class ServerInputBuffer {
	private ArrayList<String> buffer = null;
	private String commandUp = Str.COMMAND_UP;

	public ServerInputBuffer() {
		buffer = new ArrayList<String>();
	}

	public int getSize(){
		return buffer.size();
	}
	
	public void add() {
		buffer.add(commandUp);
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
