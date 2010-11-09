/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package engine;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

/**
 *
 * @author dimko
 */
public class Utils {
    public static int[] binarize(final int[] values, int quantity){
    	Integer[] ids = new Integer[values.length];
    	for(int i=0; i<ids.length; i++)
    		ids[i] = i;
    	Arrays.sort(ids, new Comparator<Integer>(){
    		@Override
    		public int compare(Integer lhs, Integer rhs) {
    			return values[rhs] - values[lhs];
    		}
    	});
    	int[] winners = new int[quantity];
    	for(int i=0; i<winners.length; i++){
    		winners[i] = ids[i];
    	}
		return winners;
    }

    public static Collection<Integer> sample(int count, int quantity){
        HashSet<Integer> values = new HashSet<Integer>();
        while(values.size() < quantity){
            values.add(Rand.nextInt(count));
        }
        return values;
    }
}
