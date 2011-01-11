package util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import engine.Options;

public class Utils {
	/**
	 * @return indexes of the biggest values (excluding zeroes!)
	 */
	public static int[] topK(int[] ids, int quantity, Comparator<Integer> cmp) {
		Integer[] top = new Integer[quantity];
		int topSize = 0;
		Integer bottom = null;
		for (int id : ids) {
			if (cmp.compare(bottom, id) >= 0) {
				int pos = Arrays.binarySearch(top, 0, topSize, id, cmp);
				if (pos < 0) {
					pos = ~pos;
				}
				if (topSize < quantity) { // enlarge top if we can
					topSize++;
				}
				if (pos < topSize) { // got into top?
					System.arraycopy(top, pos, top, pos + 1, topSize - (pos + 1));
					top[pos] = id;
					if (topSize == quantity) {
						bottom = top[topSize - 1];
					}
				}
			}
		}
		return toIntArray(top, topSize);
	}

	public static int[] shuffle(int size) {
		int[] ids = new int[size];
		for (int i = 0; i < size; i++) {
			ids[i] = i;
		}
		for (int i = size - 1; i > 1; i--) {
			int pair = Rand.range(i);
			int temp = ids[i];
			ids[i] = ids[pair];
			ids[pair] = temp;
		}
		return ids;
	}

	public static int[] binarize(final int[] values, int quantity) {
		int size = values.length;
		Integer[] ids = new Integer[size];
		for (int i = 0; i < size; i++) {
			ids[i] = i;
		}
		for (int i = size - 1; i > 1; i--) {
			int pair = Rand.range(i);
			int temp = ids[i];
			ids[i] = ids[pair];
			ids[pair] = temp;
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
		return toIntArray(ids, real_quantity);
	}

	/**
	 * Warning: range should be >= size, and typically range should be >> size !
	 * 
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
		return value >= 80 ? BLACK100 : (value >= 50 ? BLACK_75 : (value >= 25 ? BLACK_50
				: (value >= 10 ? BLACK_25 : BLACK__0)));
	}

	public static char color255(int value) {
		return value >= 80 ? BLACK100 : (value >= 60 ? BLACK_75 : (value >= 40 ? BLACK_50
				: (value >= 20 ? BLACK_25 : BLACK__0)));
	}

	public static StringBuilder render(int width, int height, Renderer renderer) {
		return render(width, height, Options.DEBUG_STRIP, renderer);
	}

	public static StringBuilder render(int width, int height, int strip, Renderer renderer) {
		StringBuilder result = new StringBuilder("(");
		int stripes = 1 + (width - 1) / strip;
		for (int s = 0; s < stripes; s++) {
			for (int y = 0; y < height; y++) {
				if ((y > 0 && s == 0) || (s > 0)) {
					result.append(' ');
				}
				result.append("[");
				for (int x = 0; x < strip; x++) {
					int rx = x + s * strip;
					if (rx >= width)
						break;
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
		return render(width, height, new Renderer() {
			public char paint(int position) {
				int value = (int) (values[position] / divider);
				return color100(value);
			}
		});
	}

	public static int[] toIntArray(Collection<Integer> active) {
		return toIntArray(active, null);
	}

	public static int[] toIntArray(Collection<Integer> active, Integer maxLength) {
		int size = active.size();
		if (maxLength != null && maxLength < size) {
			size = maxLength;
		}
		int[] values = new int[size];
		Iterator<Integer> iterator = active.iterator();
		for (int i = 0; i < size; i++) {
			values[i] = (Integer) iterator.next();
		}
		return values;
	}

	public static int[] toIntArray(Integer[] active, Integer maxLength) {
		int size = active.length;
		if (maxLength != null && maxLength < size) {
			size = maxLength;
		}
		int[] values = new int[size];
		for (int i = 0; i < size; i++) {
			values[i] = active[i];
		}
		return values;
	}

	public static int[] binarize2(final int[] source, int winners) {
		return topK(shuffle(source.length), winners, new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return source[o2] - (o1 == null ? 1 : source[o1]);
			}
		});
	}
}
