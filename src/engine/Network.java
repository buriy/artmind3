/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package engine;

/**
 *
 * @author dimko
 */
public class Network {
	private IntField field1;
	private IntField field2;
	private IntField field3;
	private Node nodes1;
	private Node nodes2;

	public Network(Options options){
        this.field1 = new IntField(32, 32);
        this.field2 = new IntField(128, 8);
        this.field3 = new IntField(128, 8);
        this.nodes1 = new Node(this.field1, this.field2, options);
        this.nodes2 = new Node(this.field2, this.field3, options);
    }
	
	public State operate(){
        State state1 = nodes1.operate();
        State state2 = nodes2.operate();
        if(state1 == State.RESTART || state2 == State.RESTART)
        	return State.RESTART;
        if(state1 == State.TRAIN || state2 == State.TRAIN)
        	return State.TRAIN;
        return State.LEARNED;
        //int min = Math.min(state1.ordinal(), state2.ordinal());
        //return State.values()[min];
	}

	public void set_input(int[] data) {
		field1.data = data;
	}
}
