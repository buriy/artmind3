package engine;

public class InternalNode extends Node {
	protected Columns neurons;
	protected Field output_field;
	protected Sensor[] sensors;

	public InternalNode(Field input_field, Field output_field, Options opt) {
		super(input_field, opt);
		this.output_field = output_field;
		this.neurons = new Columns(opt, output_field);
		create_sensors();
	}

	protected void create_sensors() {
		sensors = new Sensor[this.opt.SENSORS];
		for (int i = 0; i < this.opt.SENSORS; ++i) {
			sensors[i] = new Sensor(opt, input);
		}
	}

	protected int[] sensory() {
		int[] values = new int[sensors.length];
		double maxDutyCycle = 0;
		for (int i = 0; i < values.length; ++i) {
			values[i] = sensors[i].sum();
			if (sensors[i].activeDutyCycle() > maxDutyCycle) {
				maxDutyCycle = sensors[i].activeDutyCycle();
			}
		}
		int[] winners = Utils.binarize(values, this.opt.SENSOR_WINNERS);
		for (int i = 0; i < winners.length; i++) {
			sensors[winners[i]].updateWinner();
		}
		for (int i = 0; i < sensors.length; i++) {
			sensors[i].updateSensor(maxDutyCycle);
		}
		return winners;
	}

	@Override
	public State operate() {
		if(learnTime == Options.LEARN_TIME){
			incLearnTime();
			return State.RESTART;
		}
		int[] bits = sensory();
		boolean learning = learnTime<Options.LEARN_TIME;
		State state = neurons.operate(bits, learning);
		if(!learning){
			return State.LEARNED;
		}
		if (state == State.TRAIN)
			incLearnTime();
//		System.out.println(output_field);
		return state;
	}

	public boolean isLearning() {
		return neurons.isLearning();
	}
}
