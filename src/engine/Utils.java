/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

/**
 *
 * @author dimko
 */
public class Utils {
    public static int[] binarize(ArrayList<Integer> sensors, int winners){
        // gets 'winners' winners from sensors, by max values
        return null;
    }

    public static Collection sample(int count, int quantity){
        Random rand = new Random();
        HashSet values = new HashSet();
        while(values.size() < quantity){
            Object element = rand.nextInt(count);
            values.add(element);
        }
        return values;
    }
}
