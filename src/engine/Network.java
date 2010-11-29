package engine;

import util.Utils;

public class Network {
	private ByteField input;
	private ByteField[] fields;
	private StringField output;
	private InnerNode[] nodes;
	private UpperNode supervisor;
	private final Options opt;

	public Network(Options options) {
		this.opt = options;
		this.input = new ByteField(32, 32);
		this.fields = new ByteField[options.LAYERS];
		this.nodes = new InnerNode[options.LAYERS];
		Field input_layer = input;
		for (int layer = 0; layer < options.LAYERS; layer++) {
			fields[layer] = new ByteField(options.SENSORS, options.NEURON_CELLS);
			nodes[layer] = new InnerNode(input_layer, fields[layer], options, layer);
			input_layer = fields[layer];
		}
		this.output = new StringField();
		this.supervisor = new UpperNode(input_layer, this.output, options);
	}

	public NetState train(byte[] data, String supervised) {
		input.data = data;
		NetState state;
		for (Node node : nodes) {
			state = node.operate();
			if (opt.SEQUENTIAL_LEARNING) {
				if (state == NetState.LEARNING || state == NetState.RESTART)
					return state;
			}
		}
		// int maxLearnTime = nodes[options.LAYERS - 1].learnTime();
		return supervisor.train(opt.LEARN_TIME, supervised);
	}

	public String run(byte[] data) {
		input.data = data;
		for (Node node : nodes) {
			node.operate();
		}
		supervisor.operate();
		return output.toString();
	}
	
	@Override
	public String toString() {
		byte[] source = fields[opt.LAYERS-1].data;
		int[] values = new int[source.length];
		for(int i=0; i<source.length; i++){
			values[i] = source[i] & 0xFF;
		}
		for(int layer=opt.LAYERS-1; layer>=0; layer--){
			InnerNode node = nodes[layer];
			values = node.restore(values);
		}
		return Utils.renderValues(input.width(), input.height(), values).toString();
	}
}
