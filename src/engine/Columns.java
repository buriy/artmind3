package engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import util.Renderer;
import util.Utils;

public class Columns {
	private final Options opt;
	private final Field output;
	private boolean isLearning;
	private ArrayList<Segment>[][] segments;
	private boolean[][][] active;
	private boolean[][] learn;
	private boolean[][][] predictive;
	private ArrayList<Integer>[] learningCells;
	ArrayList<SegmentUpdate>[][] segmentUpdateList;
	private boolean prediction;
	private int time;
	private SynapseMapper smap;

	public boolean isLearning() {
		return isLearning;
	}

	boolean prediction() {
		return prediction;
	}

	@SuppressWarnings("unchecked")
	public Columns(Options opt, Field output) {
		this.opt = opt;
		this.smap = new SynapseMapper(opt);
		this.output = output;
		this.time = 0;
		isLearning = true;
		this.active = (new boolean[opt.SENSORS][opt.NEURON_CELLS][2]);
		predictive = new boolean[opt.SENSORS][opt.NEURON_CELLS][2];
		learn = new boolean[smap.synapses()][2];
		learningCells = new ArrayList[2];
		learningCells[time] = new ArrayList<Integer>();
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
		learningCells[time] = new ArrayList<Integer>();
		for (int b = 0; b < opt.SENSORS; b++) {
			for (int c = 0; c < opt.NEURON_CELLS; c++) {
				setActive(b, c, time, false);
				setLearn(b, c, time, false);
				setPredicted(b, c, time, false);
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
				if (getPredicted(b, c, oldt)) {
					Segment s = getActiveSegment(b, c, oldt);
					if (s != null && s.sequenceSegment) {
						buPredicted = true;
						setActive(b, c, time, true);
						if (getLearnActiveSize(s, oldt) >= opt.NEURON_ACTIVATION_THRESHOLD) {
							lcChosen = true;
							setLearn(b, c, time, true);
						}
					}
				}
			}
			if (!buPredicted) {
				for (int c = 0; c < opt.NEURON_CELLS; c++) {
					setActive(b, c, time, true);
				}
			}
			if (!lcChosen) {
				Segment segment = getBestMatchingCell(b, oldt);
				int c = segment.cell;
				setLearn(b, c, time, true);
				SegmentUpdate sUpdate = getSegmentActiveSynapses(segment, oldt, true);
				sUpdate.sequenceSegment = true;
				segmentUpdateList[b][c].add(sUpdate);
			}
		}
		for (int b = 0; b < opt.SENSORS; b++) {
			for (int c = 0; c < opt.NEURON_CELLS; c++) {
				for (Segment s : segments[b][c]) {
					if (getActiveSizeOfConnectedSynapses(s, time) >= opt.NEURON_ACTIVATION_THRESHOLD) {
						setPredicted(b, c, time, true);
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
				if (getLearn(b, c, time)) {
					adaptSegments(b, c, true);
					segmentUpdateList[b][c].clear();
				} else if (!getPredicted(b, c, time) && getPredicted(b, c, oldt)) {
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
				boolean pr = getPredicted(b, c, time);
				boolean ac = getActive(b, c, time);
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
				if (getPredicted(b, c, oldt)) {
					Segment s = getActiveSegment(b, c, oldt);
					if (s != null && s.sequenceSegment) {
						buPredicted = true;
						setActive(b, c, time, true);
					}
				}
			}
			if (!buPredicted) {
				for (int c = 0; c < opt.NEURON_CELLS; c++) {
					setActive(b, c, time, true);
				}
			}
		}
		for (int b = 0; b < opt.SENSORS; b++) {
			for (int c = 0; c < opt.NEURON_CELLS; c++) {
				for (Segment s : segments[b][c]) {
					if (getActiveSizeOfConnectedSynapses(s, time) >= opt.NEURON_ACTIVATION_THRESHOLD) {
						setPredicted(b, c, time, true);
					}
				}
			}
		}
		set_output();
		return NetState.TESTING;
	}

	/**
	 * This function iterates through a list of segmentUpdate's and reinforces each
	 * segment. For each segmentUpdate element, the following changes are performed. If
	 * positiveReinforcement is true then synapses on the active list get their permanence
	 * counts incremented by permanenceInc. All other synapses get their permanence counts
	 * decremented by permanenceDec. If positiveReinforcement is false, then synapses on
	 * the active list get their permanence counts decremented by permanenceDec. After
	 * this step, any synapses in segmentUpdate that do yet exist get added with a
	 * permanence count of initialPerm.
	 */
	private void adaptSegments(int bit, int cell, boolean positiveReinforcement) {
		for (SegmentUpdate segmentUpdate : segmentUpdateList[bit][cell]) {
			Segment targetSegment = segmentUpdate.segment;
			HashSet<Integer> connected = targetSegment.connected;
			HashMap<Integer, Integer> synapses = targetSegment.synapses;
			HashSet<Integer> updates = new HashSet<Integer>(segmentUpdate.updatedSynapses);
			if (segmentUpdate.sequenceSegment) {
				targetSegment.sequenceSegment = true;
			}
			if (positiveReinforcement) {
				for (Integer s : synapses.keySet()) {
					if (updates.contains(s)) { // synapses & updates
						addPermanence(synapses, connected, s);
						updates.remove(s);
					} else { // synapses & !updates
						decPermanence(synapses, connected, s);
					}
				}
				for (Integer key : updates) { // !synapses & updates
					setPermanence(synapses, connected, key);
				}
			} else {
				for (Integer s : updates) {
					if (synapses.containsKey(s)) {
						decPermanence(synapses, connected, s);
					}
				}
			}
		}
	}

	private void setPermanence(HashMap<Integer, Integer> possibleSynapses,
			HashSet<Integer> connectedSynapses, Integer key) {
		possibleSynapses.put(key, opt.NEURON_PERMANENCE_INITIAL);
		if (opt.NEURON_PERMANENCE_INITIAL > opt.NEURON_PERMANENCE_CONNECTED) {
			connectedSynapses.add(key);
		}
	}

	private void addPermanence(HashMap<Integer, Integer> synapses, HashSet<Integer> connectedSynapses, int key) {
		Integer perm = synapses.get(key);
		if (perm == null)
			throw new IllegalStateException("Should use setPermanence on new Synapse");
		perm = Math.min(perm + opt.NEURON_PERMANENCE_INC, 100);
		synapses.put(key, perm);
		if (perm > opt.NEURON_PERMANENCE_CONNECTED) {
			connectedSynapses.add(key);
		}
	}

	private void decPermanence(HashMap<Integer, Integer> synapses, HashSet<Integer> connected, int key) {
		Integer perm = synapses.get(key);
		if (perm == null)
			throw new IllegalStateException("Should use setPermanence on new Synapse");
		perm = Math.max(perm - opt.NEURON_PERMANENCE_DEC, 0);
		synapses.put(key, perm);
		if (perm <= opt.NEURON_PERMANENCE_CONNECTED) {
			connected.remove(key);
		}
	}

	/**
	 * For the given column c cell i at time t, find the segment with the largest number
	 * of active synapses. This routine is aggressive in finding the best match. The
	 * permanence value of synapses is allowed to be below connectedPerm. The number of
	 * active synapses is allowed to be below activationThreshold, but must be above
	 * minThreshold. The routine returns the segment index. If no segments are found, then
	 * an index of -1 is returned.
	 */
	private Segment getBestMatchingSegment(int bit, int cell, int time) {
		int pretender_size = opt.NEURON_MIN_THRESHOLD;
		Segment pretender = null;
		for (Segment segment : segments[bit][cell]) {
			// RECHECK THIS
			int size = getActiveSize(segment, time);
			if (size > pretender_size) {
				pretender = segment;
				pretender_size = size;
			}
		}
		return pretender;
	}

	/**
	 * This routine returns true if the number of connected synapses on segment s that are
	 * active due to the given state at time t is greater than activationThreshold. The
	 * parameter state can be activeState, or learnState.
	 */
	private int getActiveSize(Segment segment, int time) {
		int count = 0;
		for (Integer synapse : segment.synapses.keySet()) {
			if (getActive(synapse, time)) {
				count += 1;
			}
		}
		return count;
	}

	/**
	 * For the given column, return the cell with the best matching segment (as defined in
	 * {@code getBestMatchingSegment}. If no cell has a matching segment, then return the
	 * cell with the fewest number of segments.
	 */
	private Segment getBestMatchingCell(int bit, int time) {
		int champion_size = opt.NEURON_MIN_THRESHOLD;
		Segment champion_segment = null;
		for (int cell = 0; cell < opt.NEURON_CELLS; cell++) {
			// should be equal to getBestMatchingSegment(bit, cell, time):
			for (Segment segment : segments[bit][cell]) {
				int size = getActiveSize(segment, time);
				if (size > champion_size) {
					champion_segment = segment;
					champion_size = size;
				}
			}
		}
		if (champion_segment == null) {
			champion_segment = getCellWithFewestSegments(bit);
		}
		return champion_segment;
	}

	private Segment getCellWithFewestSegments(int bit) {
		int champion_size = segments[bit][0].size();
		int champion_cell = 0;
		for (int c = 1; c < opt.NEURON_CELLS; c++) {
			int pretender_size = segments[bit][c].size();
			if (pretender_size < champion_size) {
				champion_cell = c;
				champion_size = pretender_size;
			}
		}
		Segment segment = new Segment(bit, champion_cell);
		segments[bit][champion_cell].add(segment);
		return segment;
	}

	/**
	 * Return a segmentUpdate data structure containing a list of proposed changes to
	 * segment s. Let activeSynapses be the list of active synapses where the originating
	 * cells have their activeState output = 1 at time step t. (This list is empty if s =
	 * -1 since the segment doesn't exist.) newSynapses is an optional argument that
	 * defaults to false. If newSynapses is true, then newSynapseCount -
	 * count(activeSynapses) synapses are added to activeSynapses. These synapses are
	 * randomly chosen from the set of cells that have learnState output = 1 at time step
	 * t.
	 */
	private SegmentUpdate getSegmentActiveSynapses(Segment segment, int time, boolean newSynapses) {
		SegmentUpdate update = new SegmentUpdate();
		HashSet<Integer> items = new HashSet<Integer>();
		if (segment != null) {
			for (Integer synapse : segment.connected) {
				if (getActive(synapse, time)) {
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
		ArrayList<Integer> choices = learningCells[time];
		if (to_add > 0) {
			Collection<Integer> samples = Utils.sample(choices.size(), Math.min(to_add, choices.size()));
			for (int index : samples) {
				Integer proto = choices.get(index);
				items.add(proto);
			}
		}
		return update;
	}

	/**
	 * This routine returns true if the number of connected synapses on segment s that are
	 * active due to the given state at time t is greater than activationThreshold. The
	 * parameter state can be activeState, or learnState.
	 */
	private int getActiveSizeOfConnectedSynapses(Segment segment, int time) {
		// if (segment.activeSizeConnected[time] != -1)
		// return segment.activeSizeConnected[time];
		int count = 0;
		for (Integer synapse : segment.connected) {
			if (getActive(synapse, time)) {
				count += 1;
			}
		}
		// segment.activeSizeConnected[time] = count;
		return count;
	}

	/**
	 * This routine returns true if the number of connected synapses on segment s that are
	 * active due to the given state at time t is greater than activationThreshold. The
	 * parameter state can be activeState, or learnState.
	 */
	private int getLearnActiveSize(Segment segment, int time) {
		int count = 0;
		for (Integer synapse : segment.connected) {
			if (getLearn(synapse, time)) {
				count += 1;
			}
		}
		return count;
	}

	/**
	 * For the given column c cell i, return a segment index such that segmentActive(s,t,
	 * state) is true. If multiple segments are active, sequence segments are given
	 * preference. Otherwise, segments with most activity are given preference.
	 */
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
		// if(pretender == null){
		// System.out.println("Weird");
		// }
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
					boolean sa = getActive(b, c, moment);
					boolean sl = getLearn(b, c, moment);
					boolean sp = getPredicted(b, c, moment);
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

	public boolean getLearn(int bit, int cell, int time) {
		int synapse = smap.bc2s(bit, cell);
		return learn[synapse][time];
	}

	private boolean getLearn(int synapse, int time) {
		return learn[synapse][time];
	}

	private void setLearn(int bit, int cell, int time, boolean value) {
		int synapse = smap.bc2s(bit, cell);
		learn[synapse][time] = value;
		if (value) {
			learningCells[time].add(synapse);
		}
	}

	public boolean getActive(int synapse, int time) {
		return active[smap.s2bit(synapse)][smap.s2cell(synapse)][time];
	}

	public boolean getActive(int bit, int cell, int time) {
		return active[bit][cell][time];
	}

	private void setActive(int bit, int cell, int time, boolean value) {
		active[bit][cell][time] = value;
	}

	public boolean getPredicted(int bit, int cell, int time) {
		return predictive[bit][cell][time];
	}

	private void setPredicted(int bit, int cell, int time, boolean value) {
		predictive[bit][cell][time] = value;
		prediction = value;
	}

}
