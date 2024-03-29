package ilrp.assist;

import java.util.ArrayList;
import java.util.List;

public class Situation {
	
	private List<Float> packets = new ArrayList<Float>();
	private float min = Float.MAX_VALUE;
	private float max = Float.MIN_VALUE;
	private float myPacket = -1;
	private boolean isOver = false;
	private boolean rushedByOthers = false;
	
	public boolean isRushedByOthers() {
		return rushedByOthers;
	}

	public void setRushedByOthers(boolean rushedByOthers) {
		this.rushedByOthers = rushedByOthers;
	}

	public Situation () {
		
	}
	
	public void addRedpacket(float number) {
		packets.add(number);
		if (number <= min) {
			min = number;
		}
		if (number >= max) {
			max = number;
		}
	}
	
	public Float getMax() {
		return this.max;
	}
	
	public Float getMin() {
		return this.min;
	}
	
	public List<Float> getRedpackets() {
		return this.packets;
	}
	
	public int getPacketsCount() {
		return this.packets.size();
	}
	
	public void setMyPacket(float number) {
		this.myPacket = number;
	}
	
	public float getMyPacket() {
		return this.myPacket;
	}
	
	public void setOver(boolean over) {
		this.isOver = over;
	}
	
	public boolean getOver() {
		return this.isOver;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("My packet:"+this.myPacket+"\n");
		for (float number : packets) {
			sb.append(number + " ");
		}
		sb.append("\n");
		sb.append("min packet:"+this.min + "\n");
		sb.append("max packet:"+this.max+"\n");
		sb.append("is over:"+this.isOver+"\n");
		sb.append("packets count:"+this.packets.size()+"\n");
		return sb.toString();
	}
	
	public void clearPackets() {
		this.packets.clear();
	}
}
