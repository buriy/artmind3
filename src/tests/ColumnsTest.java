package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.Utils;

import engine.ByteField;
import engine.Columns;
import engine.Options;

public class ColumnsTest {

	@Test
	public void Test1() {
		Options options = new Options();
		options.LAYERS = 1;
//		options.SENSOR_PERMANENCE_DEC = 0;
//		options.SENSOR_PERMANENCE_INC = 0;
//		options.SENSOR_PERMANENCE_CONNECTED = 2;
//		options.SENSORS_RADIUS = 2;
//		options.SENSORS_WINNERS = 2;
		options.NEURON_PERMANENCE_DEC = 15;
		options.NEURON_PERMANENCE_INC = 20;
		options.REMOVE_ZERO_CONNECTIONS = false;
		options.NEURON_ACTIVATION_THRESHOLD = 1;
		options.NEURON_MIN_THRESHOLD = 1;
		options.NEURON_CELLS = 3;
		options.SENSORS = 2;
		int N = 20;
		int M = 20;
		int[][] sequence = { { 1, 1 }, { 1, 0 }, { 0, 1 } };
		System.out.println(sequence.length);
		System.out.println(sequence[0].length);
		runSequence(options, N, M, sequence);
	}

	private void runSequence(Options options, int N, int M, int[][] sequence) {
		ByteField field = new ByteField(options.SENSORS, options.NEURON_CELLS);
		Columns columns = new Columns(options, field, 1);
		for (int i = 0; i < M; i++) {
			int[] goodSequenceCount = new int[N * sequence.length];
			int[] badSequenceCount = new int[N * sequence.length];
			int[][] sequenceMap = new int[options.SENSORS][options.NEURON_CELLS];
			for (int a = 0; a < goodSequenceCount.length; a++) {
				goodSequenceCount[a] = 0;
				badSequenceCount[a] = 0;
			}
			for (int n = 0; n < options.SENSORS; n++) {
				for (int m = 0; m < options.NEURON_CELLS; m++) {
					sequenceMap[n][m] = 0;
				}
			}
			for (int j = 0; j < N; j++) {
				for (int k = 0; k < sequence.length; k++) {
					int[] indexes = Utils.binarize(sequence[k], 2);
					columns.learn(indexes);

					for (int n = 0; n < options.SENSORS; n++) {
						for (int m = 0; m < options.NEURON_CELLS; m++) {
							if (columns.getPredicted(n, m, columns.getTime())) {
								sequenceMap[n][m] += 1;
							} else if (columns.getActive(n, m, columns.getTime())) {
								if (sequenceMap[n][m] > 0) {
									goodSequenceCount[sequenceMap[n][m]] += 1;
								}
								sequenceMap[n][m] = 0;
							} else {
								if (sequenceMap[n][m] > 0) {
									badSequenceCount[sequenceMap[n][m]] += 1;
								}
								sequenceMap[n][m] = 0;
							}
						}
					}
				}
			}
			System.out.println("cycle: " + (i + 1));
			for (int a = 1; a < goodSequenceCount.length; a++) {
				if (goodSequenceCount[a] != 0) {
					System.out.println("good sequences of size " + a + ": " + goodSequenceCount[a]);
				}
				if (badSequenceCount[a] != 0) {
					System.out.println("bad  sequences of size " + a + ": " + badSequenceCount[a]);
				}
			}
			System.out.println("----------------------------");
		}
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

}