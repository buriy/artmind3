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
    final static int SUPERVISED_SENSORS_COUNT = 128;
    final static int SUPERVISED_SENSORS_QUANTITY = 80;
    final static int SENSORS_COUNT = 128;
    final static int SENSORS_QUANTITY = 80;
    final static double SENSORS_TO_PATTERNS = 0.125;
    final static int NEURONS_PER_SENSOR = 8;
    final static int SAMPLES_PER_NEURON = 40;
    final static int NEURON_NEIGHBOURS = 2;
    final static int MOVIE_FRAMES = 25;
    final static int NUMBER_OF_TRAINING_SAMPLES = 300;
    final static double TEMPORAL_GROUP_DEVIATION_SQUARE = 4.0;
    final static int TRAIN_CYCLE_REMOVE_FREQ = 2;
    final static double FINISH_TRAINING_THRESHOLD = 0.01;
    final static int TEMPORAL_GROUP_NEIGHBOURS = 3;
    final static int TEMPORAL_GROUP_DEPTH = 2;
    final static int TEMPORAL_GROUP_SIZE = 20;
    final static boolean SUPERVISED = false;
        
    public int TRAIN_CYCLE_STEPS(){
        return NUMBER_OF_TRAINING_SAMPLES * MOVIE_FRAMES;
    }

    public int SENSOR_TO_PATTERNS_WINNERS(){
        return (int) (SENSORS_TO_PATTERNS * SENSORS_COUNT);
    }

}
