package engine;

import java.util.HashSet;

public class SegmentUpdate {
	public Segment segment;
	public boolean sequenceSegment;
	public HashSet<Synapse> synapses;
	
	@Override
	public String toString() {
		return "seq="+sequenceSegment+"\nsyn="+synapses.toString()+"\nfor segment:\n"+segment;
	}
}
