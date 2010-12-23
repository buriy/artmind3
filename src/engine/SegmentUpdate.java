package engine;

import java.util.HashSet;

public class SegmentUpdate {
	public Segment segment;
	public boolean sequenceSegment;
	public HashSet<Integer> updatedSynapses;

	@Override
	public String toString() {
		return "seq=" + sequenceSegment + "\nsyn=" + updatedSynapses + "\nfor segment:\n"
				+ segment;
	}
}
