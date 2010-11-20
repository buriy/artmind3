package engine;

import java.util.Map.Entry;
import java.util.TreeMap;

public class UpperNode extends Node {
	private StringField output_field;
	private TreeMap<String, double[]> types;
	private TreeMap<String, Integer> displayed;

	public UpperNode(Field input_field, StringField output_field, Options opt) {
		super(input_field, opt);
		this.output_field = output_field;
		this.types = new TreeMap<String, double[]>();
		this.displayed = new TreeMap<String, Integer>();
	}

	public State train(int maxLearnTime, String supervised) {
		int timeLeft = maxLearnTime - learnTime();
		if (timeLeft >= 0) {
			incLearnTime();
			if (timeLeft == 0) {
				return State.RESTART;
			}
			learn(supervised);
			return State.TRAIN;
		} else {
			return State.LEARNED;
		}
	}

	public State operate() {
		for (Entry<String, double[]> entry : types.entrySet()) {
			String key = entry.getKey();
			double[] value = entry.getValue();
			double sum = 0;
			for (int i = 0; i < input.size(); i++) {
				if (input.get(i) >= 128) {
					sum += value[i];
				}
			}
			output_field.set(key, sum * 100 / displayed.get(key));
		}
		return State.LEARNED;
	}

	public void learn(String supervised) {
		double[] array = types.get(supervised);
		if (array == null) {
			array = new double[input.size()];
			types.put(supervised, array);
			displayed.put(supervised, 0);
		}
		displayed.put(supervised, displayed.get(supervised) + 1);

		for (int i = 0; i < input.size(); i++) {
			if (input.get(i) >= 128) {
				array[i] += 1;
			}
		}
	}
}