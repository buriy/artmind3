package engine;

import java.util.Map.Entry;
import java.util.TreeMap;

public class UpperNode extends Node{
	private StringField output_field;
	private TreeMap<String, double[]> types;

	public UpperNode(Field input_field, StringField output_field, Options opt) {
		super(input_field, opt);
		this.output_field = output_field;
		this.types = new TreeMap<String, double[]>();
	}
	
	@Override
	protected void create_sensors() {
		int sensor_inputs = this.opt.SUPERVISED_SENSORS_QUANTITY;
		sensors = new Sensor[this.opt.SUPERVISED_SENSORS_COUNT];
		for(int i = 0; i < this.opt.SUPERVISED_SENSORS_COUNT; ++i){
		    sensors[i] = new Sensor(input_field, sensor_inputs);
		    if(sensor_inputs == 1){
			    sensors[i].replace_sample(0, i);
		    }
		}
	}

	public State train(int maxLearnTime, String supervised){
		int timeLeft = maxLearnTime - learnTime();
		if(timeLeft >= 0){
			incLearnTime();
			if(timeLeft == 0){
	    		return State.RESTART;
			}
			learn(supervised);
    		return State.TRAIN;
    	}else {
    		return State.LEARNED;
    	}
	}
	
	public State operate() {
		int[] active_bits = sensory();
		for (Entry<String, double[]> entry : types.entrySet()) {
			String key = entry.getKey();
			double[] value = entry.getValue();
			double sum = 0;
			for (int i : active_bits) {
				sum += value[i];
			}
			output_field.set(key, sum);
		}
		return State.LEARNED;
	}

	public void learn(String supervised) {
		int[] active_bits = sensory();
		int sensor_count = this.opt.SUPERVISED_SENSORS_COUNT;
		double[] array = types.get(supervised);
		if(array == null){
			array = new double[sensor_count];
			types.put(supervised, array);
		}
		for (int bit : active_bits) {
			array[bit] += 1;
		}
	}
}