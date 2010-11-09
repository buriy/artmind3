package engine;

import java.util.Random;


public class Rand {
	static final Rand INSTANCE = new Rand();
	private Random rand;
	
	private Rand() {
		rand = new Random();
	}
	
	public static int nextInt(int n){
		return INSTANCE.rand.nextInt(n);
	}

}
