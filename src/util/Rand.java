package util;

import java.util.Random;

public class Rand {
	static final Rand INSTANCE = new Rand();
	private Random rand;

	private Rand() {
		rand = new Random();
	}

	/**
	 * @return random value in range [0, n)
	 */
	public static int range(int n) {
		return INSTANCE.rand.nextInt(n);
	}

	/**
	 * @return random value in range [from, to]
	 */
	public static int range(int from, int to) {
		return range(to - from + 1) + from;
	}

}
