package engine;

public class ByteField implements Field {
	private int size;
	int width;
	int height;
	byte data[];

	public ByteField(int width, int height) {
		this(width, height, new byte[width * height]);
	}

	public ByteField(int width, int height, byte[] data) {
		this.size = width * height;
		this.width = width;
		this.height = height;
		this.data = data;
	}

	public String toString() {
		StringBuilder result = new StringBuilder("(");
		for (int i = 0; i < height; i++) {
			result.append("[");
			for (int j = 0; j < width; j++) {
				int value = get(i, j);
				result.append(value);
			}
			result.append("]\n");
		}
		return result + ")";
	}

	public int get(int item) {
		return data[item];
	}

	public int get(int x, int y) {
		return data[y * width + x];
	}

	public void reset() {
		for (int i = 0; i < size; i++) {
			data[i] = 0;
		}
	}

	public void set(int x, int y, int value) {
		data[y * width + x] = (byte)value;
	}

	public int getSize() {
		return size;
	}
}
