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
	public static int[] binarize(final int[] values, int quantity) {
		int size = values.length;
		Integer[] ids = new Integer[size];
		for (int i = 0; i < size; i++) {
			ids[i] = i;
		}
		for (int i = 0; i < size * 2; i++) {
			int r1 = Rand.nextInt(size);
			int r2 = Rand.nextInt(size);
			if (r1 != r2) {
				int t = ids[r1];
				ids[r1] = ids[r2];
				ids[r2] = t;
			}
		}
		Arrays.sort(ids, new Comparator<Integer>() {
			@Override
			public int compare(Integer lhs, Integer rhs) {
				return values[rhs] - values[lhs];
			}
		});
		int[] winners = new int[quantity];
		for (int i = 0; i < winners.length; i++) {
			winners[i] = ids[i];
		}
		return winners;
	}

	public static Collection<Integer> sample(int count, int quantity) {
		HashSet<Integer> values = new HashSet<Integer>();
		while (values.size() < quantity) {
			values.add(Rand.nextInt(count));
		}
		return values;
	}

	static HashSet<Integer> toHashSet(int[] bits) {
		HashSet<Integer> search = new HashSet<Integer>();
		for (int bit : bits) {
			search.add(bit);
		}
		return search;
	}
}
