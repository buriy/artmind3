package engine;

import java.util.ArrayList;

public class Segment {
	public boolean sequenceSegment;
	ArrayList<Synapse> synapses = new ArrayList<Synapse>();

	@Override
	public String toString() {
		return "seq="+sequenceSegment+"\nsyn="+synapses.toString();
	}
}
