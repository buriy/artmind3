package engine;

public class InnerNode extends Node {
	protected Columns neurons;
	protected Field output;
	protected Sensors sensors;

	public InnerNode(Field input, Field output, Options opt) {
		super(input, opt);
		this.output = output;
		this.neurons = new Columns(opt, output);
		this.sensors = new Sensors(opt, input);
	}

	@Override
	public State operate() {
		int[] bits = sensors.operate();
		State state;
		if (learnTime < opt.LEARN_TIME){
			incLearnTime();
			state = neurons.learn(bits);
			if (learnTime == opt.LEARN_TIME - 1) {
				state = State.RESTART;
			}
		}else{
			state = neurons.run(bits);
		}
		return state;
	}

	public boolean isLearning() {
		return neurons.isLearning();
	}
}
