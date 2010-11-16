/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package engine;

public class Network {
	private ByteField input;
	private Field[] fields;
	private StringField output;
	private InternalNode[] nodes;
	private UpperNode supervisor;
	private final Options options;

	public Network(Options options){
        this.options = options;
		this.input = new ByteField(32, 32);
		this.fields = new ByteField[options.LAYERS];
		this.nodes = new InternalNode[options.LAYERS];
		Field input_layer = input;
		for (int layer = 0; layer < options.LAYERS; layer++) {
			fields[layer] = new ByteField(options.SENSORS_COUNT, options.NEURONS_PER_SENSOR);
			nodes[layer] = new InternalNode(input_layer, fields[layer], options);
		}
        this.output = new StringField();
        this.supervisor = new UpperNode(input_layer, this.output, options);
    }
	
	public State train(byte[] data, String supervised){
		input.data = data;
		State state;
		for (Node node : nodes) {
			state = node.operate();
			if(options.SEQUENTIAL_LEARNING){
				if(state == State.TRAIN || state == State.RESTART)
					return state;
			}
		}
		int maxLearnTime = nodes[options.LAYERS - 1].learnTime();
		return supervisor.train(maxLearnTime, supervised);
	}

	public String run(byte[] data) {
		input.data = data;
		for (Node node : nodes) {
			node.operate();
		}
		supervisor.operate();
		return output.toString();
	}
}
