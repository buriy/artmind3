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
		for (int y = 0; y < height; y++) {
			if(y > 0)
				result.append(' ');
			result.append("[");
			for (int x = 0; x < width; x++) {
				boolean value = test(x, y);
				result.append(value ? '*': ' ');
			}
			result.append("]");
			if(y != height-1)
				result.append('\n');
		}
		return result + ")";
	}

	public int get(int item) {
		return data[item] & 0xFF;
	}

	public boolean test(int item) {
		return data[item] < 0;
	}

	public boolean test(int x, int y) {
		return data[y * width + x] < 0;
	}

	public int get(int x, int y) {
		return data[y * width + x] & 0xFF;
	}

	public void reset() {
		for (int i = 0; i < size; i++) {
			data[i] = 0;
		}
	}

	public void set(int x, int y, int value) {
		data[y * width + x] = (byte)value;
	}

	public void set(int x, int y, boolean value) {
		data[y * width + x] = (byte) (value ? -1 : 0);
	}

	public int size() {
		return size;
	}

	@Override
	public int width() {
		return width;
	}

	@Override
	public int height() {
		return height;
	}
}
