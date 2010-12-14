package engine;

import java.util.HashMap;
import java.util.HashSet;

public class Segment {
	public int cell;
	public int activeSizeConnected[] = new int[2];
	public boolean sequenceSegment;
	HashSet<Integer> connected = new HashSet<Integer>();
	HashMap<Integer, Integer> synapses = new HashMap<Integer, Integer>();

	public Segment(int cell) {
		this.cell = cell;
	}
	
	@Override
	public String toString() {
		return "seq="+sequenceSegment+"\nsyn="+connected.toString();
	}
}
