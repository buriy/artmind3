package engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class Columns {
	private final Options opt;
	private final Field output;
	private boolean isLearning;
	private ArrayList<Segment>[][] segments;
	private boolean[][][] active;
	private boolean[][][] learn;
	private boolean[][][] predictive;
	private ArrayList<Synapse>[] learningCells;
	private int time;
	private static int minThreshold = 1;
	ArrayList<SegmentUpdate>[][] segmentUpdateList;

	public boolean isLearning() {
		return isLearning;
	}

	@SuppressWarnings("unchecked")
	public Columns(Options opt, Field output) {
		this.opt = opt;
		this.output = output;
		this.time = 0;
		isLearning = true;
		active = new boolean[opt.SENSORS][opt.CELLS][2];
		predictive = new boolean[opt.SENSORS][opt.CELLS][2];
		learn = new boolean[opt.SENSORS][opt.CELLS][2];
		learningCells = new ArrayList[2];
		learningCells[0] = new ArrayList<Synapse>();
		segments = new ArrayList[opt.SENSORS][opt.CELLS];
		segmentUpdateList = new ArrayList[opt.SENSORS][opt.CELLS];
		for (int b = 0; b < opt.SENSORS; b++) {
			for (int c = 0; c < opt.CELLS; c++) {
				segmentUpdateList[b][c] = new ArrayList<SegmentUpdate>();
				segments[b][c] = new ArrayList<Segment>();
				// for (int s = 0; s < opt.SEGMENTS; s++) {
				// segments[b][c].add(new Segment());
				// }
			}
		}
	}

	public State operate(int[] activeColumns, boolean learnMode) {
		int oldt = time;
		time = 1 - time;

		learningCells[time] = new ArrayList<Synapse>();
		for (int i = 0; i < opt.SENSORS; i++) {
			for (int j = 0; j < opt.CELLS; j++) {
				active[i][j][time] = false;
				learn[i][j][time] = false;
				predictive[i][j][time] = false;
				// for(Segment s: segments[i][j]){
				// s.sequenceSegment = false;
				// }
			}
		}

		if (!learnMode) {
			return run(activeColumns);
		}

		for (int b : activeColumns) {
			boolean lcChosen = false;
			boolean buPredicted = false;
			for (int c = 0; c < opt.CELLS; c++) {
				if (predictive[b][c][oldt]) {
					Segment s = getActiveSegment(b, c, oldt, active);
					if (s != null && s.sequenceSegment) {
						buPredicted = true;
						active[b][c][time] = true;
						if (segmentActive(s, oldt, learn)) {
							lcChosen = true;
							learn[b][c][time] = true;
							learningCells[time].add(new Synapse(b, c));
						}
					}
				}
			}
			if (!buPredicted) {
				for (int c = 0; c < opt.CELLS; c++) {
					active[b][c][time] = true;
				}
			}
			if (!lcChosen) {
				int c = getBestMatchingCell(b, oldt);
				learn[b][c][time] = true;
				learningCells[time].add(new Synapse(b, c));
				SegmentUpdate sUpdate = getSegmentActiveSynapses(b, c, null, oldt, true);
				sUpdate.sequenceSegment = true;
				segmentUpdateList[b][c].add(sUpdate);
			}
		}
		for (int b = 0; b < opt.SENSORS; b++) {
			for (int c = 0; c < opt.CELLS; c++) {
				for (Segment s : segments[b][c]) {
					if (segmentActive(s, time, active)) {
						predictive[b][c][time] = true;
						SegmentUpdate activeUpdate = getSegmentActiveSynapses(b, c, s, time, false);
						segmentUpdateList[b][c].add(activeUpdate);
						Segment predSegment = getBestMatchingSegment(b, c, oldt);
						SegmentUpdate predUpdate = getSegmentActiveSynapses(b, c, predSegment, oldt, true);
						segmentUpdateList[b][c].add(predUpdate);
					}
				}
			}
		}
		for (int b = 0; b < opt.SENSORS; b++) {
			for (int c = 0; c < opt.CELLS; c++) {
				if (learn[b][c][time]) {
					adaptSegments(b, c, true);
					segmentUpdateList[b][c].clear();
				} else if (!predictive[b][c][oldt] && predictive[b][c][time]) {
					adaptSegments(b, c, false);
					segmentUpdateList[b][c].clear();
				}
			}
		}
		set_output();
		return State.TRAIN;
	}

	private void set_output() {
		for (int b = 0; b < opt.SENSORS; b++) {
			for (int c = 0; c < opt.CELLS; c++) {
				output.set(b, c, active[b][c][time]);
			}
		}
	}

	private State run(int[] activeColumns) {
		int oldt = 1 - time;
		for (int b : activeColumns) {
			boolean buPredicted = false;
			for (int c = 0; c < opt.CELLS; c++) {
				if (predictive[b][c][time]) {
					Segment s = getActiveSegment(b, c, oldt, active);
					if(s != null && s.sequenceSegment){
						buPredicted = true;
						active[b][c][time] = true;
					}
				}
			}
			if(!buPredicted){
				for (int c = 0; c < opt.CELLS; c++) {
					active[b][c][time] = true;
				}				
			}
		}
		for (int b = 0; b < opt.SENSORS; b++) {
			for (int c = 0; c < opt.CELLS; c++) {
				for (Segment s: segments[b][c]) {
					if(segmentActive(s, time, active)){
						predictive[b][c][time] = true;
					}
				}
			}
		}
		set_output();
		return State.LEARNED;
	}

	private void adaptSegments(int b, int c, boolean positiveReinforcement) {
		for (SegmentUpdate segmentUpdate : segmentUpdateList[b][c]) {
			if (segmentUpdate.segment == null) {
				addSegment(b, c, segmentUpdate);
				continue;
			}
			ArrayList<Synapse> synapses = segmentUpdate.segment.synapses;
			HashSet<Synapse> updates = new HashSet<Synapse>(segmentUpdate.synapses);
			if (positiveReinforcement) {
				Iterator<Synapse> iterator = synapses.iterator();
				while (iterator.hasNext()) {
					Synapse s = iterator.next();
					if (updates.contains(s)) {
						s.addPermanence(opt.PERMANENCE_INC);
						updates.remove(s);
					} else {
						boolean zero = s.decPermanence(opt.PERMANENCE_DEC, opt.PERMANENCE_CONNECTED);
						if (zero) {
							iterator.remove();
						}
					}
				}
				for (Synapse new_synapse : updates) {
					if (new_synapse.isNew()) {
						new_synapse.setPermanence(opt.PERMANENCE_INITIAL);
						synapses.add(new_synapse);
					}
				}
			} else {
				Iterator<Synapse> iterator = synapses.iterator();
				while (iterator.hasNext()) {
					Synapse s = iterator.next();
					if (updates.contains(s)) {
						boolean zero = s.decPermanence(opt.PERMANENCE_DEC, opt.PERMANENCE_CONNECTED);
						if (zero) {
							iterator.remove();
						}
					}
				}
			}
		}
	}

	private void addSegment(int b, int c, SegmentUpdate segmentUpdate) {
		if (!segmentUpdate.synapses.isEmpty()) {
			Segment segment = new Segment();
			segment.sequenceSegment = segmentUpdate.sequenceSegment;
			segment.synapses = segmentUpdate.synapses;
			for (Synapse s : segment.synapses) {
				s.setPermanence(opt.PERMANENCE_INITIAL);
			}
			segments[b][c].add(segment);
		}
	}

	private Segment getBestMatchingSegment(int b, int c, int time) {
		int pretender_size = minThreshold;
		Segment pretender = null;
		for (Segment segment : segments[b][c]) {
			// RECHECK THIS
			int size = getActiveSize(segment, time, active);
			if (size > pretender_size) {
				pretender = segment;
				pretender_size = size;
			}
		}
		return pretender;
	}

	private int getBestMatchingCell(int b, int time) {
		int pretender_size = minThreshold;
		int pretender_cell = -1;
		for (int c = 0; c < opt.CELLS; c++) {
			for (Segment segment : segments[b][c]) {
				int size = getActiveSize(segment, time, active);
				if (size > pretender_size) {
					pretender_cell = c;
					pretender_size = size;
				}
			}
		}
		if (pretender_cell == -1) {
			return getLessEducatedCell(b);
		}
		return pretender_cell;
	}

	private int getLessEducatedCell(int b) {
		int pretender_size;
		int pretender_cell;
		pretender_cell = 0;
		pretender_size = segments[b][0].size();
		for (int c = 1; c < opt.CELLS; c++) {
			int s = segments[b][c].size();
			if (s < pretender_size) {
				pretender_cell = c;
				pretender_size = s;
			}
		}
		return pretender_cell;
	}

	private SegmentUpdate getSegmentActiveSynapses(int _b, int _c, Segment s, int time, boolean newSynapses) {
		// TODO Auto-generated method stub
		// activeSynapses <==> synapses where active[b][c][time] == 1
		SegmentUpdate update = new SegmentUpdate();
		ArrayList<Synapse> items = new ArrayList<Synapse>();
		if (s != null) {
			for (Synapse synapse : s.synapses) {
				if (active[synapse.bit()][synapse.cell()][time]) {
					items.add(synapse);
				}
			}
		}
		update.segment = s;
		update.synapses = items;
		if (!newSynapses) {
			return update;
		}
		int to_add = opt.NEW_SYNAPSES - items.size();
		if (to_add > 0) {
			ArrayList<Synapse> choices = learningCells[time];
			Collection<Integer> samples = Utils.sample(choices.size(), Math.min(to_add, choices.size()));
			for (int index : samples) {
				Synapse proto = choices.get(index);
				items.add(new Synapse(proto.bit(), proto.cell()));
			}
		}
		return update;
	}

	private boolean segmentActive(Segment s, int time, boolean[][][] state) {
		int count = getActiveSize(s, time, state);
		return count > opt.ACTIVATION_THRESHOLD;
	}

	private int getActiveSize(Segment s, int time, boolean[][][] state) {
		int count = 0;
		for (Synapse synapse : s.synapses) {
			if (state[synapse.bit()][synapse.cell()][time]) {
				count += 1;
			}
		}
		return count;
	}

	// private int getActiveSize(Segment s, int time) {
	// int count = 0;
	// for (Synapse synapse : s.synapses) {
	// if (synapse.permanence() > opt.PERMANENCE_CONNECTED) {
	// count += 1;
	// }
	// }
	// return count;
	// }

	private Segment getActiveSegment(int bit, int cell, int time, boolean[][][] state) {
		Segment pretender = null;
		int pretender_value = -1;
		boolean pretender_sequence = false;
		for (Segment segment : segments[bit][cell]) {
			int value = getActiveSize(segment, time, state);
			if (value > opt.ACTIVATION_THRESHOLD) {
				if (pretender_sequence && !segment.sequenceSegment)
					continue;
				if (segment.sequenceSegment && !pretender_sequence) {
					pretender = segment;
					pretender_value = value;
					pretender_sequence = true;
				} else if (value > pretender_value) {
					pretender = segment;
					pretender_value = value;
				}
			}
		}
		return pretender;
	}
}
