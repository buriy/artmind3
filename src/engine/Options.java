/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package engine;

/**
 *
 * @author dimko
 */
public class Options {
    final int MAX_RECOGNIZED_TYPES = 128;
//    final int SUPERVISED_SENSORS_COUNT = 1024;
//    final int SUPERVISED_SENSORS_QUANTITY = 1;
//XXX: if SUPERVISED_SENSORS_QUANTITY is "1", special fix is applied
//XXX: so each sensor is connected to input with the same id.
    final int SUPERVISED_SENSORS_COUNT = 1024;
    final int SUPERVISED_SENSORS_QUANTITY = 1;
    final int SENSORS_COUNT = 128;
    final int SENSORS_QUANTITY = 80;
    final double SENSORS_TO_PATTERNS = 0.125;
    final int NEURONS_PER_SENSOR = 8;
    final int SAMPLES_PER_NEURON = 80;
    final int NEURON_NEIGHBOURS = 3;
        
    public int SENSOR_TO_PATTERNS_WINNERS(){
        return (int) (SENSORS_TO_PATTERNS * SENSORS_COUNT);
    }

}
