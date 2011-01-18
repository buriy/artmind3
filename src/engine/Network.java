package engine;

import util.Utils;

public class Network {
	private ByteField input;
	private ByteField[] fields;
	private StringField output;
	private ByteField superfield;
	private InnerNode[] nodes;
	private UpperNode supervisor;
	
	private final Options opt;

	public Network(Options options) {
		this.opt = options;
		this.input = new ByteField(32, 32);
		this.fields = new ByteField[options.LAYERS];
		this.superfield = new ByteField(options.MAX_SAMPLES, 1);
		this.nodes = new InnerNode[options.LAYERS];
		Field input_layer = input;
		for (int layer = 0; layer < options.LAYERS; layer++) {
			fields[layer] = new ByteField(options.SENSORS, options.NEURON_CELLS);
			nodes[layer] = new InnerNode(input_layer, fields[layer], options, layer);
			input_layer = fields[layer];
		}

		for(int layer = 1; layer < options.LAYERS; layer++){
			nodes[layer-1].addSecondaryInput(fields[layer]);
		}

		this.output = new StringField();
		this.supervisor = new UpperNode(input_layer, this.output, options);

		if(opt.SUPERVISOR_SIGNAL){
			nodes[options.LAYERS - 1].addSecondaryInput(superfield);
		}
}

	public NetState train(byte[] data, String supervised) {
		input.data = data;
		NetState state;
		if(opt.SUPERVISOR_SIGNAL){
			int id = supervisor.getId(supervised);
			superfield.reset();
			superfield.set(id, 0, true);
		}
		for (InnerNode node : nodes) {
			state = node.operate();
			if (opt.SEQUENTIAL_LEARNING) {
				if (state == NetState.LEARNING || state == NetState.RESTART)
					return state;
			}
		}
		// int maxLearnTime = nodes[options.LAYERS - 1].learnTime();
		return supervisor.train(opt.ROUND_TIME, supervised);
	}

	public String run(byte[] data) {
		input.data = data;
		if(opt.SUPERVISOR_SIGNAL){
			superfield.reset();
		}
		for (Node node : nodes) {
			node.operate();
		}
		supervisor.operate();
		return output.toString();
	}

	@Override
	public String toString() {
		byte[] source = fields[opt.LAYERS - 1].data;
		int[] values = new int[source.length];
		for (int i = 0; i < source.length; i++) {
			values[i] = source[i] & 0xFF;
		}
//		values[9] = 255;
		for (int layer = opt.LAYERS - 1; layer >= 0; layer--) {
			InnerNode node = nodes[layer];
			values = node.restore(values);
		}
		return Utils.renderValues(input.width(), input.height(), values).toString() + " -> "+supervisor.toString();
	}

	public void addLearnRounds(int rounds) {
		opt.LEARN_ROUNDS += rounds;
		supervisor.restart();
	}
}
