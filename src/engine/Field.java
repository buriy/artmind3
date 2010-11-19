package engine;

public interface Field {
	int size();
	int width();
	int height();

	int get(int x, int y);
	int get(int item);
	boolean test(int item);
	void set(int x, int y, int value);
	void set(int b, int c, boolean d);
	
	void reset();

	String toString();

}
