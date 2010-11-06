/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package engine;

/**
 *
 * @author dimko
 */
public class Node<T> {
    Field<T> input_field;
    Field<Boolean> output_field;
    Options opt;
    Sensor[] sensors;
    int quantity;

    Node(Field<T> input_field, Field<Boolean> output_field, Options opt){
        this.input_field = input_field;
        this.output_field = output_field;
        this.opt = opt;
        this.sensors = new Sensor[this.opt.SENSORS_COUNT];
        this.quantity = this.opt.SENSORS_QUANTITY;
        int items = this.input_field.size;
        for(int i = 0; i < this.opt.SENSORS_COUNT; ++i){
            sensors[i] = Sensor(Utils.sample(items, quantity));
        }
    }

   void sensory(){
        T[] sensor_field = new T[this.sensors.length]();
        for(int i = 0; i < sensor_field.length; ++i){

        }
   }
}
