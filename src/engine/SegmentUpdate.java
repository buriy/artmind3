package engine;

import java.util.HashSet;

public class SegmentUpdate {
	public Segment segment;
	public boolean sequenceSegment;
	public HashSet<Integer> updatedSynapses;

	@Override
	public String toString() {
		return "SU<seq:" + sequenceSegment + ", syn:" + updatedSynapses + ", segment:"
				+ segment+">";
	}
}
