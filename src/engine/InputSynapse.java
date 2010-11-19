package engine;

public class InputSynapse {
	private int position;
	private int permanence;

	public InputSynapse(int position, int permanence) {
		this.position = position;
		this.permanence = permanence;
	}

	public int position() {
		return position;
	}
	
	public int permanence() {
		return permanence;
	}
}
