package util;

import java.util.Random;


public class Rand {
	static final Rand INSTANCE = new Rand();
	private Random rand;
	
	private Rand() {
		rand = new Random();
	}
	
	public static int range(int n){
		return INSTANCE.rand.nextInt(n);
	}

	public static int range(int from, int to){
		return range(to-from+1) + from;
	}

}
