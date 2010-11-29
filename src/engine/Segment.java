package engine;

import java.util.HashSet;

public class Segment {
	public int activeSizeConnected[] = new int[2];
	public boolean sequenceSegment;
	HashSet<Synapse> connectedSynapses = new HashSet<Synapse>();
	HashSet<Synapse> possibleSynapses = new HashSet<Synapse>();

	@Override
	public String toString() {
		return "seq="+sequenceSegment+"\nsyn="+connectedSynapses.toString();
	}
}
