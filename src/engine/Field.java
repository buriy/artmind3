package engine;

public interface Field {
	int size();

	int width();

	int height();

	int get(int pos);

	int get(int x, int y);

	boolean test(int pos);

	boolean test(int x, int y);

	void set(int x, int y, int value);

	void set(int x, int y, boolean value);

	void reset();

	String toString();
}
