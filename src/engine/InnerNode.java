package engine;

import java.util.ArrayList;

import util.Utils;

public class InnerNode extends Node {
	protected Columns neurons;
	protected Field output;
	protected Sensors sensors;
	private final int layer;

	public InnerNode(Field input, Field output, Options opt, int number) {
		super(input, opt);
		this.output = output;
		this.layer = number;
		this.neurons = new Columns(opt, output);
		if (number == 0) {
			this.sensors = new ZoneSensors(opt, input);
		} else {
			this.sensors = new Sensors(opt, input);
		}
	}

	@Override
	public NetState operate() {
		NetState state;
		if (learnTime < opt.learnTime()) {
			int[] bits = sensors.learn();
			incLearnTime();
			state = neurons.learn(bits);
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
		int[] data = sensors.restoreWinners(firedSensors());
		return Utils.renderValues(input.width(), input.height(), data).toString();
	}

	private int[] firedSensors() {
		ArrayList<Integer> active = new ArrayList<Integer>();
		for (int i = 0; i < output.width(); i++) {
			int actives = 0;
			for (int j = 0; j < output.height(); j++) {
				actives += output.get(i, j);
			}
			if (actives != 0)
				active.add(i);
		}
		int[] values = new int[active.size()];
		for (int i = 0; i < values.length; i++)
			values[i] = active.get(i);
		return values;
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
		int[] data = sensors.restore(active, divider);
		return data;
	}
}
