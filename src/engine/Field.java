package engine;

public interface Field {
	int getSize();

	int get(int x, int y);
	int get(int item);
	void set(int x, int y, int value);
	
	void reset();

	String toString();
}
