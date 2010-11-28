package util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

import engine.Options;


public class Utils {
	/**
	 * @param values is input array
	 * @param quantity is the max length of returned array
	 * @return ids of the biggest values (excluding zeroes!)
	 */
	public static int[] binarize(final int[] values, int quantity) {
		int size = values.length;
		Integer[] ids = new Integer[size];
		for (int i = 0; i < size; i++) {
			ids[i] = i;
		}
		for (int i = 0; i < size * 2; i++) {
			int r1 = Rand.range(size);
			int r2 = Rand.range(size);
			if (r1 != r2) {
				int t = ids[r1];
				ids[r1] = ids[r2];
				ids[r2] = t;
			}
		}
		Arrays.sort(ids, new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return values[o2] - values[o1];
			};
		});
		int real_quantity = quantity;
		for(int i=0; i<quantity; i++){
			if(values[ids[i]] == 0){
				real_quantity = i;
				break;
			}
		}
		int[] winners = new int[real_quantity];
		for (int i = 0; i < real_quantity; i++) {
			winners[i] = ids[i];
		}
		return winners;
	}

	/**
	 * Warning: range should be >= size, and typically range should be >> size !
	 * @param range
	 * @param size
	 * @return selects few random but different numbers from the given range
	 */
	public static Collection<Integer> sample(int range, int size) {
		HashSet<Integer> values = new HashSet<Integer>();
		while (values.size() < size) {
			values.add(Rand.range(range));
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

	public static char BLACK100 = '\u2588';
	public static char BLACK_75 = '\u2593';
	public static char BLACK_50 = '\u2592';
	public static char BLACK_25 = '\u2591';
	public static char BLACK__0 = ' ';

	public static char color2(boolean value) {
		return value ? BLACK100 : BLACK__0;
	}
	
	public static char color100(int value) {
		return  value >= 80 ? BLACK100 : (
			    value >= 50 ? BLACK_75 : (
			    value >= 25 ? BLACK_50 : (
			    value >= 10 ? BLACK_25 : 
				              BLACK__0 )));
	}

	public static char color255(int value) {
		return  value >= 80 ? BLACK100 : (
				value >= 60 ? BLACK_75 : (
				value >= 40 ? BLACK_50 : (
				value >= 20 ? BLACK_25 : 
							  BLACK__0 )));
	}

	public static StringBuilder render(int width, int height, Renderer renderer) {
		return render(width, height, Options.DEBUG_STRIP, renderer);
	}

	public static StringBuilder render(int width, int height, int strip, Renderer renderer) {
		StringBuilder result = new StringBuilder("(");
		int strips = 1 + (width - 1) / strip;
		for(int s = 0; s < strips; s++){	
			for (int y = 0; y < height; y++) {
				if ((y > 0 && s == 0) || (s > 0)) {
					result.append(' ');
				}
				result.append("[");
				for (int x = 0; x < strip; x++) {
					int rx = x + s * strip;
					if(rx >= width) break;
					result.append(renderer.paint(y * width + rx));
				}
				result.append("]");
				result.append('\n');
			}
		}
		result.append(")");
		return result;
	}

	public static int maximum(int[] values, int min) {
		int max = min;
		for (int i = 0; i < values.length; i++) {
			if (values[i] > max)
				max = values[i];
		}
		return max;
	}

	public static StringBuilder renderValues(int width, int height, final int[] values) {
		final double divider = maximum(values, 1) / 120;
		return render(width, height, new Renderer(){
			public char paint(int position) {
				int value = (int) (values[position] / divider);
				return color100(value);
			}
		});
	}
}
