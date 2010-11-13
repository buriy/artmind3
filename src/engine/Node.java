package engine;

public class Node {
	protected Field input_field;
	protected Options opt;
	protected Sensor[] sensors;
	protected int learnTime;

	public Node(Field input_field, Options opt) {
        this.input_field = input_field;
        this.opt = opt;
        create_sensors();
        this.learnTime = 0;
	}

	protected void create_sensors() {
		int sensor_inputs = this.opt.SENSORS_QUANTITY;
	    sensors = new Sensor[this.opt.SENSORS_COUNT];
	    for(int i = 0; i < this.opt.SENSORS_COUNT; ++i){
	        sensors[i] = new Sensor(input_field, sensor_inputs);
	    }
	}

	protected int[] sensory() {
	        int[] values = new int[sensors.length];
	        for(int i = 0; i < values.length; ++i){
	        	values[i] = sensors[i].sum();
	        }
	        return Utils.binarize(values, this.opt.SENSOR_TO_PATTERNS_WINNERS());
	   }

	public State operate() {
		return State.LEARNED;
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