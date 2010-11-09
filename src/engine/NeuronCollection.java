/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package engine;

import java.util.Collection;


/**
 * 
 * @author dimko
 */
public class NeuronCollection {
	private final Options opt;
	private final IntField output_field;
	private int[] last_bits;
	private int learned;
	private boolean learning;
	private int max_learned;
	SampleSet[][] patterns;

	public boolean isLearning() {
		return learning;
	}
	
	public NeuronCollection(Options opt, IntField output_field) {
		patterns = new SampleSet[opt.SENSORS_COUNT][opt.NEURONS_PER_SENSOR];
		for (int i = 0; i < opt.SENSORS_COUNT; i++) {
			for (int j = 0; j < opt.NEURONS_PER_SENSOR; j++) {
				patterns[i][j] = new SampleSet(opt.SAMPLES_PER_NEURON, opt.NEURON_NEIGHBOURS);
			}
		}
		this.opt = opt;
		this.output_field = output_field;
		learned = 0;
		learning = true;
		max_learned = opt.SENSORS_COUNT 
					* opt.SAMPLES_PER_NEURON
					* opt.NEURONS_PER_SENSOR;
	}

	public State operate(int[] bits) {
		if (learning) {
			if (learned >= max_learned) {
				learning = false;
				System.out.println("Restart!");
				return State.RESTART;
			}
			train(bits);
			return State.TRAIN;
		} else {
			run(bits);
			return State.LEARNED;
		}
	}

	private void run(int[] bits) {
		output_field.reset();
		for (int i = 0; i < bits.length; ++i) {
			for (int j = 0; j < opt.NEURONS_PER_SENSOR; ++j) {
				if (patterns[bits[i]][j].find(last_bits)) {
					output_field.set(bits[i], j, 1);
				}
			}
		}
		last_bits = bits;
	}

	private void train(int[] bits) {
		if(last_bits != null){
			int maxSamples = opt.SAMPLES_PER_NEURON;
			int neur = opt.NEURONS_PER_SENSOR;
			int neigh = opt.NEURON_NEIGHBOURS;
			for (int i = 0; i < bits.length; i++) {
				int nextInt = Rand.nextInt(neur);
				SampleSet sampleSet = patterns[bits[i]][nextInt];
				int[] pattern = extractN(neigh);
				boolean found = sampleSet.find(pattern);
				if(!found){
					if(sampleSet.size() < maxSamples){
						sampleSet.add(pattern);
						learned++;
					}
				}
			}
		}
		last_bits = bits;
	}

	private int[] extractN(int neigh) {
		Collection<Integer> sample = Utils.sample(last_bits.length, neigh);
		int[] pattern = new int[neigh];
		int pos = 0;
		for (Integer integer : sample) {
			pattern[pos++] = last_bits[integer];
		}
		return pattern;
	}
}
