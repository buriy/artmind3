package util;

import java.util.Comparator;

public class UtilsTests {
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
		int[] actual = topK(source, 3, USE_NEW_TOPK);
		check(actual, expected);
	}

	private static int[] topK(final int[] source, int winners, boolean use_new) {
		if(use_new){
			return Utils.topK(Utils.shuffle(source.length), winners, new Comparator<Integer>() {
				public int compare(Integer o1, Integer o2) {
					return source[o2] - (o1 == null ? 1 : source[o1]);
				}
			});
		}else{
			return Utils.binarize(source, winners);
		}
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
		int cycles = 10000;
		long start = System.currentTimeMillis();
		int[] actual = null;
		for (int i = 0; i < cycles; i++) {
			actual = topK(source, winners, use_new);
		}
		check(actual, expected);
		long spent = System.currentTimeMillis() - start;
		System.out.println("Spent " + spent + " ms for " + cycles + " cycles");
	}

	private static void testSort1() {
		final int[] source = { 9, 7, 0, 0, 0 };
		final int[] expected = { 0, 1 };
		int[] actual = topK(source, 2, USE_NEW_TOPK);
		check(actual, expected);
	}

	private static void testSort2() {
		final int[] source = { 9, 5, 0, 7, 0, 4, 0 };
		final int[] expected = { 0, 3, 1 };
		int[] actual = topK(source, 3, USE_NEW_TOPK);
		check(actual, expected);
	}

	private static void testSort3() {
		final int[] source = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		final int[] expected = { 8, 7, 6, 5 };
		int[] actual = topK(source, 4, USE_NEW_TOPK);
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
