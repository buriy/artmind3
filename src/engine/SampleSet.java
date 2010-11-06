package engine;

import java.util.Arrays;

public class SampleSet {
	int[][] values;
	int size;
	private final int width;

	public SampleSet(int max, int width) {
		this.width = width;
		values = new int[max][width];
		size = 0;
	}

	boolean find(int[] pattern) {
		for (int i = 0; i < size; ++i) {
			int matches = 0;
			for (int j = 0; j < pattern.length; j++) {
				for (int k = 0; k < width; k++) {
					if(pattern[j] == values[i][k]){
						matches += 1;
						break;
					}
				}
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