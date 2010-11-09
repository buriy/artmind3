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
    final int SUPERVISED_SENSORS_COUNT = 1024;
    final int SUPERVISED_SENSORS_QUANTITY = 1;
    final int SENSORS_COUNT = 128;
    final int SENSORS_QUANTITY = 80;
    final double SENSORS_TO_PATTERNS = 0.125;
    final int NEURONS_PER_SENSOR = 8;
    final int SAMPLES_PER_NEURON = 40;
    final int NEURON_NEIGHBOURS = 2;
    final int MOVIE_FRAMES = 25;
    final int NUMBER_OF_TRAINING_SAMPLES = 300;
    final double TEMPORAL_GROUP_DEVIATION_SQUARE = 4.0;
    final int TRAIN_CYCLE_REMOVE_FREQ = 2;
    final double FINISH_TRAINING_THRESHOLD = 0.01;
    final int TEMPORAL_GROUP_NEIGHBOURS = 3;
    final int TEMPORAL_GROUP_DEPTH = 2;
    final int TEMPORAL_GROUP_SIZE = 20;
    final boolean SUPERVISED = false;
        
    public int TRAIN_CYCLE_STEPS(){
        return NUMBER_OF_TRAINING_SAMPLES * MOVIE_FRAMES;
    }

    public int SENSOR_TO_PATTERNS_WINNERS(){
        return (int) (SENSORS_TO_PATTERNS * SENSORS_COUNT);
    }

}
