package engine;

import java.util.HashSet;

import util.Utils;

public class InnerNode extends Node {
	protected final Columns neurons;
	protected final Field output;
	protected final Sensors sensors;
	private final int layer;

	public InnerNode(Field input, Field output, Options opt, int layer) {
		super(input, opt);
		this.output = output;
		this.layer = layer;
		this.neurons = new Columns(opt, output, layer);
		if (layer == 0) {
			this.sensors = new ZoneSensors(opt, input);
		} else {
			this.sensors = new FieldSensors(opt, input);
		}
	}

	@Override
	public NetState operate() {
		NetState state;
		if (learnTime < opt.learnTime()) {
			int[] bits = sensors.learn();
			incLearnTime();
			state = neurons.learn(bits);
			if (layer == 1) {
				state = NetState.LEARNING;
			}
			if (layer == 2) {
				state = NetState.LEARNING;
			}
			if (layer == 0) {
				if (neurons.prediction()) {
					state = NetState.LEARNING;
				}
			}
			if (learnTime == opt.learnTime() - 1) {
				state = NetState.RESTART;
			}
		} else {
			int[] bits = sensors.run();
			state = neurons.run(bits);
		}
		return state;
	}

	public boolean isLearning() {
		return neurons.isLearning();
	}

	@Override
	public String toString() {
		int[] active = firedSensors();
		return sensors.restoreWinners(active, input);
	}

	private int[] firedSensors() {
		HashSet<Integer> active = new HashSet<Integer>();
		for (int i = 0; i < output.width(); i++) {
			for (int j = 0; j < output.height(); j++) {
				if(output.test(i, j)){
					active.add(i);
				}
			}
		}
		return Utils.toIntArray(active);
	}

	public int[] restore(int[] field) {
		int width = output.width();
		int active[] = new int[width];
		for (int i = 0; i < width; i++) {
			int actives = 0;
			for (int j = 0; j < output.height(); j++) {
				actives += field[i + j * width];
			}
			active[i] = actives;
		}
		int divider = Utils.maximum(active, 1);
		int[] data = sensors.restore(active, divider, input);
		return data;
	}
}
