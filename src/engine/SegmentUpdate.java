package engine;

import java.util.ArrayList;

public class SegmentUpdate {
	public Segment segment;
	public boolean sequenceSegment;
	public ArrayList<Synapse> synapses;
	
	@Override
	public String toString() {
		return "seq="+sequenceSegment+"\nsyn="+synapses.toString()+"\nfor segment:\n"+segment;
	}
}
