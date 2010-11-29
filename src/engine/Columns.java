package engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import util.Renderer;
import util.Utils;

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
	ArrayList<SegmentUpdate>[][] segmentUpdateList;
	private boolean prediction;

	public boolean isLearning() {
		return isLearning;
	}

	boolean prediction() {
		return prediction;
	}

	@SuppressWarnings("unchecked")
	public Columns(Options opt, Field output) {
		this.opt = opt;
		this.output = output;
		this.time = 0;
		isLearning = true;
		active = new boolean[opt.SENSORS][opt.NEURON_CELLS][2];
		predictive = new boolean[opt.SENSORS][opt.NEURON_CELLS][2];
		learn = new boolean[opt.SENSORS][opt.NEURON_CELLS][2];
		learningCells = new ArrayList[2];
		learningCells[0] = new ArrayList<Synapse>();
		segments = new ArrayList[opt.SENSORS][opt.NEURON_CELLS];
		segmentUpdateList = new ArrayList[opt.SENSORS][opt.NEURON_CELLS];
		for (int b = 0; b < opt.SENSORS; b++) {
			for (int c = 0; c < opt.NEURON_CELLS; c++) {
				segmentUpdateList[b][c] = new ArrayList<SegmentUpdate>();
				segments[b][c] = new ArrayList<Segment>();
			}
		}
	}

	private void init() {
		learningCells[time] = new ArrayList<Synapse>();
		for (int b = 0; b < opt.SENSORS; b++) {
			for (int c = 0; c < opt.NEURON_CELLS; c++) {
				active[b][c][time] = false;
				learn[b][c][time] = false;
				predictive[b][c][time] = false;
			}
		}
	}

	public NetState learn(int[] activeColumns) {
		int oldt = time;
		time = 1 - time;
		init();

		for (int b : activeColumns) {
			boolean lcChosen = false;
			boolean buPredicted = false;
			for (int c = 0; c < opt.NEURON_CELLS; c++) {
				if (predictive[b][c][oldt]) {
					Segment s = getActiveSegment(b, c, oldt);
					if (s != null && s.sequenceSegment) {
						buPredicted = true;
						active[b][c][time] = true;
						if (getLearnActiveSize(s, oldt) >= opt.NEURON_ACTIVATION_THRESHOLD) {
							lcChosen = true;
							learn[b][c][time] = true;
							learningCells[time].add(new Synapse(b, c));
						}
					}
				}
			}
			if (!buPredicted) {
				for (int c = 0; c < opt.NEURON_CELLS; c++) {
					active[b][c][time] = true;
				}
			}
			if (!lcChosen) {
				int c = getBestMatchingCell(b, oldt);
				learn[b][c][time] = true;
				learningCells[time].add(new Synapse(b, c));
				SegmentUpdate sUpdate = getSegmentActiveSynapses(null, oldt, true);
				sUpdate.sequenceSegment = true;
				segmentUpdateList[b][c].add(sUpdate);
			}
		}
		for (int b = 0; b < opt.SENSORS; b++) {
			for (int c = 0; c < opt.NEURON_CELLS; c++) {
				for (Segment s : segments[b][c]) {
					if (getActiveSizeOfConnectedSynapses(s, time) >= opt.NEURON_ACTIVATION_THRESHOLD) {
						predictive[b][c][time] = true;
						SegmentUpdate activeUpdate = getSegmentActiveSynapses(s, time, false);
						segmentUpdateList[b][c].add(activeUpdate);
						Segment predSegment = getBestMatchingSegment(b, c, oldt);
						SegmentUpdate predUpdate = getSegmentActiveSynapses(predSegment, oldt, true);
						segmentUpdateList[b][c].add(predUpdate);
					}
				}
			}
		}
		for (int b = 0; b < opt.SENSORS; b++) {
			for (int c = 0; c < opt.NEURON_CELLS; c++) {
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
		return NetState.LEARNING;
	}

	private void set_output() {
		prediction = false;
		for (int b = 0; b < opt.SENSORS; b++) {
			for (int c = 0; c < opt.NEURON_CELLS; c++) {
				boolean pr = predictive[b][c][time];
				boolean ac = active[b][c][time];
				if (pr && !ac) {
					prediction = true;
				}
				output.set(b, c, ac || pr);
			}
		}
	}

	public NetState run(int[] activeColumns) {
		int oldt = time;
		time = 1 - time;
		init();
		for (int b : activeColumns) {
			boolean buPredicted = false;
			for (int c = 0; c < opt.NEURON_CELLS; c++) {
				if (predictive[b][c][oldt]) {
					Segment s = getActiveSegment(b, c, oldt);
					if (s != null && s.sequenceSegment) {
						buPredicted = true;
						active[b][c][time] = true;
					}
				}
			}
			if (!buPredicted) {
				for (int c = 0; c < opt.NEURON_CELLS; c++) {
					active[b][c][time] = true;
				}
			}
		}
		for (int b = 0; b < opt.SENSORS; b++) {
			for (int c = 0; c < opt.NEURON_CELLS; c++) {
				for (Segment s : segments[b][c]) {
					if (getActiveSizeOfConnectedSynapses(s, time) >= opt.NEURON_ACTIVATION_THRESHOLD) {
						predictive[b][c][time] = true;
					}
				}
			}
		}
		set_output();
		return NetState.TESTING;
	}

	private void adaptSegments(int b, int c, boolean positiveReinforcement) {
		for (SegmentUpdate segmentUpdate : segmentUpdateList[b][c]) {
			Segment targetSegment = segmentUpdate.segment;
			if (targetSegment == null) {
				if(positiveReinforcement){
					addSegment(b, c, segmentUpdate);
				}
				continue;
			}
			HashSet<Synapse> connectedSynapses = targetSegment.connectedSynapses;
			HashSet<Synapse> possibleSynapses = targetSegment.possibleSynapses;
			HashSet<Synapse> updates = new HashSet<Synapse>(segmentUpdate.updatedSynapses);
			if (positiveReinforcement) {
				for (Synapse s : possibleSynapses) {
					if (updates.contains(s)) {
						int permanence = s.addPermanence(opt.NEURON_PERMANENCE_INC);
						if (permanence >= opt.NEURON_PERMANENCE_CONNECTED) {
							connectedSynapses.add(s);
						}
						updates.remove(s);
					} else {
						int permanence = s.decPermanence(opt.NEURON_PERMANENCE_DEC);
						if (permanence < opt.NEURON_PERMANENCE_CONNECTED) {
							connectedSynapses.remove(s);
						}
					}
				}
				for (Synapse new_synapse : updates) {
					if (new_synapse.isNew()) {
						new_synapse.setPermanence(opt.NEURON_PERMANENCE_INITIAL);
						connectedSynapses.add(new_synapse);
						possibleSynapses.add(new_synapse);
					}
				}
			} else {
				for (Synapse s : possibleSynapses) {
					if (updates.contains(s)) {
						int permanence = s.decPermanence(opt.NEURON_PERMANENCE_DEC);
						if (permanence < opt.NEURON_PERMANENCE_CONNECTED) {
							connectedSynapses.remove(s);
						}
					}
				}
			}
		}
	}

	private void addSegment(int b, int c, SegmentUpdate segmentUpdate) {
		if (!segmentUpdate.updatedSynapses.isEmpty()) {
			Segment segment = new Segment();
			segment.sequenceSegment = segmentUpdate.sequenceSegment;
			segment.connectedSynapses = segmentUpdate.updatedSynapses;
			segment.possibleSynapses = new HashSet<Synapse>(segmentUpdate.updatedSynapses);
			for (Synapse s : segment.connectedSynapses) {
				s.setPermanence(opt.NEURON_PERMANENCE_INITIAL);
			}
			segments[b][c].add(segment);
		}
	}

	private Segment getBestMatchingSegment(int b, int c, int time) {
		int pretender_size = opt.NEURON_MIN_THRESHOLD;
		Segment pretender = null;
		for (Segment segment : segments[b][c]) {
			// RECHECK THIS
			int size = getActiveSize(segment, time);
			if (size > pretender_size) {
				pretender = segment;
				pretender_size = size;
			}
		}
		return pretender;
	}

	private int getActiveSize(Segment segment, int time) {
		int count = 0;
		for (Synapse synapse : segment.possibleSynapses) {
			if (active[synapse.bit][synapse.cell][time]) {
				count += 1;
			}
		}
		return count;
	}

	private int getBestMatchingCell(int b, int time) {
		int champion_size = opt.NEURON_MIN_THRESHOLD;
		int champion_cell = -1;
		for (int c = 0; c < opt.NEURON_CELLS; c++) {
			for (Segment segment : segments[b][c]) {
				int size = getActiveSize(segment, time);
				if (size > champion_size) {
					champion_cell = c;
					champion_size = size;
				}
			}
		}
		if (champion_cell == -1) {
			return getCellWithFewestSegments(b);
		}
		return champion_cell;
	}

	private int getCellWithFewestSegments(int b) {
		int champion_size = segments[b][0].size();
		int champion_cell = 0;
		for (int c = 1; c < opt.NEURON_CELLS; c++) {
			int pretender_size = segments[b][c].size();
			if (pretender_size < champion_size) {
				champion_cell = c;
				champion_size = pretender_size;
			}
		}
		return champion_cell;
	}

	private SegmentUpdate getSegmentActiveSynapses(Segment segment, int time, boolean newSynapses) {
		SegmentUpdate update = new SegmentUpdate();
		HashSet<Synapse> items = new HashSet<Synapse>();
		if (segment != null) {
			for (Synapse synapse : segment.connectedSynapses) {
				if (active[synapse.bit][synapse.cell][time]) {
					items.add(synapse);
				}
			}
		}
		update.segment = segment;
		update.updatedSynapses = items;
		if (!newSynapses) {
			return update;
		}
		int to_add = opt.NEURON_NEW_SYNAPSES - items.size();
		ArrayList<Synapse> choices = learningCells[time];
		if (to_add > 0) {
			Collection<Integer> samples = Utils.sample(choices.size(), Math.min(to_add, choices.size()));
			for (int index : samples) {
				Synapse proto = choices.get(index);
				items.add(new Synapse(proto.bit, proto.cell));
			}
		}
		return update;
	}

	private int getActiveSizeOfConnectedSynapses(Segment segment, int time) {
		if(segment.activeSizeConnected[time] != -1)
			return segment.activeSizeConnected[time];
		int count = 0;
		for (Synapse synapse : segment.connectedSynapses) {
			if (active[synapse.bit][synapse.cell][time]) {
				count += 1;
			}
		}
		segment.activeSizeConnected[time] = count;
		return count;
	}

	private int getLearnActiveSize(Segment segment, int time) {
		int count = 0;
		for (Synapse synapse : segment.connectedSynapses) {
			if (learn[synapse.bit][synapse.cell][time]) {
				count += 1;
			}
		}
		return count;
	}

	private Segment getActiveSegment(int bit, int cell, int time) {
		Segment pretender = null;
		int pretender_value = -1;
		boolean pretender_sequence = false;
		for (Segment segment : segments[bit][cell]) {
			int value = getActiveSizeOfConnectedSynapses(segment, time);
			if (value >= opt.NEURON_ACTIVATION_THRESHOLD) {
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
//		if(pretender == null){
//			System.out.println("Weird");
//		}
		return pretender;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int s = 0; s < 2; s++) {
			final int moment = (s == 0) ? time : 1 - time;
			builder.append(String.format("Active %s:\n", (s == 0) ? "now" : "before"));
			builder.append(Utils.render(opt.SENSORS, opt.NEURON_CELLS, new Renderer() {
				public char paint(int position) { // y * width + x
					int b = position % opt.SENSORS;
					int c = position / opt.SENSORS;
					boolean sa = active[b][c][moment];
					boolean sl = learn[b][c][moment];
					boolean sp = predictive[b][c][moment];
					int val = (sa ? 1 : 0) + (sl ? 2 : 0) + (sp ? 4 : 0);
					char flags[] = { Utils.BLACK__0, // ---
							Utils.BLACK_50, // A
							'l', // L
							'L', // LA
							'P', // P
							'*', // P A
							'&', // PL
							Utils.BLACK100, // PLA
					};
					return flags[val];
				}
			}));
		}
		return builder.toString();
	}
}
