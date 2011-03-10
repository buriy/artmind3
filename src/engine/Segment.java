package engine;

import java.util.HashMap;
import java.util.HashSet;

public class Segment {
	public final int bit;
	public final int cell;
	// public int activeSizeConnected[] = new int[2];
	public boolean sequenceSegment;
	HashSet<Integer> connected = new HashSet<Integer>();
	
	// permanences
	HashMap<Integer, Integer> synapses = new HashMap<Integer, Integer>();

	public Segment(int bit, int cell) {
		this.bit = bit;
		this.cell = cell;
		this.connected = new HashSet<Integer>();
		this.synapses = new HashMap<Integer, Integer>();
	}

	@Override
	public String toString() {
		return "\nS<b:" + bit + ",c:" + cell + ",seq:" + sequenceSegment + "; conn:" + connected.size()
				+ ",syn:" + synapses.size()+">";
	}
}
