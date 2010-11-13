/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package engine;


/**
 *
 * @author dimko
 */
public class InternalNode extends Node {
    NeuronCollection neurons;
	protected Field output_field;

    public InternalNode(Field input_field, Field output_field, Options opt){
    	super(input_field, opt);
    	this.output_field = output_field;
        this.neurons = new NeuronCollection(opt, output_field);
    }

    @Override
    public State operate() {
	    int[] bits = sensory();
	    State state = neurons.operate(bits);
	    if(state == State.TRAIN)
	    	incLearnTime();
	    return state;
    }

	public boolean isLearning() {
		return neurons.isLearning();
	}
}
