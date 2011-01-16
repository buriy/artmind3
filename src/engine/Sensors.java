package engine;

public interface Sensors {
	int[] operate();

	int[] getWinners(int[] values);

	String toString();

	String restoreWinners(int[] winners, Field input);

	int[] restore(int[] source, double divider, Field input);

	int[] learn();

	int[] run();

	void addSecondaryInput(Field input);
}
