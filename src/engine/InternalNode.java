package engine;

public class InternalNode extends Node {
	protected Columns neurons;
	protected Field output_field;
	protected Sensors sensors;

	public InternalNode(Field input_field, Field output_field, Options opt) {
		super(input_field, opt);
		this.output_field = output_field;
		this.neurons = new Columns(opt, output_field);
		this.sensors = new Sensors(opt, input_field);
	}

	@Override
	public State operate() {
		if(learnTime == Options.LEARN_TIME){
			incLearnTime();
			return State.RESTART;
		}
		int[] bits = sensors.operate();
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
