package util;


public class UtilsTests {
	private static final int SPEED_TEST_CYCLES = 10000;
	public static boolean USE_NEW_TOPK = true;
	public static int SPEED_TEST_SIZE = 1024;
	public static int SPEED_TEST_WINNERS = 5;
	
	public static void main(String[] args) {
		testSort0();
		testSort1();
		testSort2();
		testSort3();
		System.out.println("With new sort:");
		testSortSpeed(true);
		System.out.println("With old sort:");
		testSortSpeed(false);
	}

	private static void testSort0() {
		final int[] source = { 0, 0, 0 };
		final int[] expected = {};
		int[] actual = Utils.binarize2(source, 3);
		check(actual, expected);
	}

	private static void testSortSpeed(boolean use_new) {
		final int[] source = new int[SPEED_TEST_SIZE];
		final int[] expected = new int[SPEED_TEST_WINNERS];
		int winners = expected.length;
		for (int i = 0; i < winners; i++) {
			int number = 3 + i * 3;
			source[number] = number;
			expected[winners - i - 1] = number;
		}
		int cycles = SPEED_TEST_CYCLES;
		long start = System.currentTimeMillis();
		int[] actual = null;
		if(use_new){
			for (int i = 0; i < cycles; i++) {
				actual = Utils.binarize2(source, winners);
			}
		}else{
			for (int i = 0; i < cycles; i++) {
				actual = Utils.binarize(source, winners);
			}
		}
		check(actual, expected);
		long spent = System.currentTimeMillis() - start;
		System.out.println("Spent " + spent + " ms for " + cycles + " cycles");
	}

	private static void testSort1() {
		final int[] source = { 9, 7, 0, 0, 0 };
		final int[] expected = { 0, 1 };
		int[] r;
		if(USE_NEW_TOPK){
			r = Utils.binarize2(source, 2);
		}else{
			r = Utils.binarize(source, 2);
		}
		int[] actual = r;
		check(actual, expected);
	}

	private static void testSort2() {
		final int[] source = { 9, 5, 0, 7, 0, 4, 0 };
		final int[] expected = { 0, 3, 1 };
		int[] r;
		if(USE_NEW_TOPK){
			r = Utils.binarize2(source, 3);
		}else{
			r = Utils.binarize(source, 3);
		}
		int[] actual = r;
		check(actual, expected);
	}

	private static void testSort3() {
		final int[] source = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		final int[] expected = { 8, 7, 6, 5 };
		int[] r;
		if(USE_NEW_TOPK){
			r = Utils.binarize2(source, 4);
		}else{
			r = Utils.binarize(source, 4);
		}
		int[] actual = r;
		check(actual, expected);
	}

	private static void check(int[] actual, final int[] expected) {
		if (actual.length != expected.length) {
			throw new IllegalStateException("actual length != expected");
		}
		for (int i = 0; i < actual.length; i++) {
			if (actual[i] != expected[i]) {
				throw new IllegalStateException("difference at position " + i + ": " + actual[i] + " != "
						+ expected[i]);
			}
		}
	}
}
