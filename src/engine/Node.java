/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package engine;

import java.util.Random;

/**
 *
 * @author dimko
 */
public class Node {
    IntField input_field;
    IntField output_field;
    Options opt;
    Sensor[] sensors;
	NeuronCollection neurons;
	private Random rand;

    Node(IntField input_field, IntField output_field, Options opt){
        this.input_field = input_field;
        this.output_field = output_field;
        this.opt = opt;
        rand = new Random();
        create_sensors();
        this.neurons = new NeuronCollection(opt, output_field);
    }

	private void create_sensors() {
		int sensor_inputs = this.opt.SENSORS_QUANTITY;
        sensors = new Sensor[this.opt.SENSORS_COUNT];
        for(int i = 0; i < this.opt.SENSORS_COUNT; ++i){
            sensors[i] = new Sensor(input_field, sensor_inputs, rand);
        }
	}


   int[] sensory(){
        int[] values = new int[sensors.length];
        for(int i = 0; i < values.length; ++i){
        	values[i] = sensors[i].sum();
        }
        return Utils.binarize(values, this.opt.SENSOR_TO_PATTERNS_WINNERS());
   }

	public State operate() {
        int[] bits = sensory();
        return neurons.operate(bits);
	}
}
