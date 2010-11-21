package engine;

import java.util.Map.Entry;
import java.util.TreeMap;

public class UpperNode extends Node {
	private StringField output;
	private TreeMap<String, double[]> types;
	private TreeMap<String, Integer> displayed;

	public UpperNode(Field input, StringField output, Options opt) {
		super(input, opt);
		this.output = output;
		this.types = new TreeMap<String, double[]>();
		this.displayed = new TreeMap<String, Integer>();
	}

	public State train(int maxLearnTime, String supervised) {
		int learnTimeLeft = maxLearnTime - learnTime();
		if (learnTimeLeft >= 0) {
			learn(supervised);
			incLearnTime();
			if (learnTimeLeft == 1) {
				return State.RESTART;
			}
			return State.TRAIN;
		} else {
			return State.LEARNED;
		}
	}

	public State operate() {
		for (Entry<String, double[]> entry : types.entrySet()) {
			String key = entry.getKey();
			double[] value = entry.getValue();
			int count = displayed.get(key);
			double sum = 0;
			for (int i = 0; i < input.size(); i++) {
				if(input.test(i)){
					sum += value[i] / count;
				}
			}
			output.set(key, sum * 100);
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
			if (input.test(i)) {
				array[i] += 1;
			}
		}
	}
}