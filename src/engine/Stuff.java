package engine;

import java.util.Arrays;
import java.util.HashSet;

public class Stuff {
	int[][] values;
	int size;
	private final int width;

	public Stuff(int max, int width) {
		this.width = width;
		values = new int[max][width];
		size = 0;
	}

	public boolean findAny(HashSet<Integer> pattern) {
		for (int i = 0; i < size; ++i) {
			int matches = 0;
			for (int j = 0; j < width; j++) {
				if(pattern.contains(values[i][j])){
					matches += 1;
					if(matches == width)
						return true;
				}
			}
		}
		return false;
	}

	boolean findExact(int[] pattern) {
		for (int i = 0; i < size; ++i) {
			int matches = 0;
			for (int j = 0; j < pattern.length; j++) {
				int testing = pattern[j];
				for (int k = 0; k < width; k++) {
					if(testing == values[i][k]){
						matches += 1;
						break;
					}
				}
				if(matches != j) break;
			}
			if(matches == width)
				return true;
		}
		return false;
	}

	void add(int[] pattern) {
		values[size++] = Arrays.copyOf(pattern, width);
	}

	int size() {
		return size;
	}
}