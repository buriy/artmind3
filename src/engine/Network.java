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
	private StringField field4;
	private InternalNode nodes1;
	private InternalNode nodes2;
	private UpperNode nodes3;

	public Network(Options options){
        this.field1 = new IntField(32, 32);
        this.field2 = new IntField(128, 8);
        this.field3 = new IntField(128, 8);
        this.field4 = new StringField();
        this.nodes1 = new InternalNode(this.field1, this.field2, options);
        this.nodes2 = new InternalNode(this.field2, this.field3, options);
        this.nodes3 = new UpperNode(this.field3, this.field4, options);
    }
	
	public State train(int[] data, String supervised){
        State state1 = nodes1.operate();
        State state2 = State.TRAIN;
        State state3 = State.TRAIN;
		field1.data = data;
        if(state1 == State.LEARNED){
        	state2 = nodes2.operate();
        }
        if(state2 == State.LEARNED){
        	int timeLeft = nodes2.learnTime() - nodes3.learnTime();
			if(timeLeft > 0){
        		nodes3.train(supervised);
        		state3 = State.TRAIN;
        	}else if(timeLeft == 0){
        		nodes3.incLearnTime();
        		state3 = State.RESTART;
        	}else{
        		state3 = State.LEARNED;
        	}
        }
        
        int min2 = Math.min(state1.ordinal(), state2.ordinal());
		int min3 = Math.min(min2, state3.ordinal());
        return State.values()[min3];
	}

	public String run(int[] data) {
		field1.data = data;
		nodes1.operate();
		nodes2.operate();
		nodes3.operate();
		return field4.toString();
	}
}
