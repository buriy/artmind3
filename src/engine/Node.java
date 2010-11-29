package engine;

public class Node {
	protected Field input;
	protected Options opt;
	protected int learnTime;

	public Node(Field input_field, Options opt) {
        this.input = input_field;
        this.opt = opt;
        this.learnTime = 0;
	}

	public NetState operate() {
		return NetState.TESTING;
	}
	
	boolean isLearning(){
		return false;
	}

	public int learnTime() {
		return learnTime;
	}

	protected void incLearnTime() {
		learnTime ++;
	}

}