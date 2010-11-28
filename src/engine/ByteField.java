package engine;

import util.Renderer;
import util.Utils;

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
		data[y * width + x] = (byte) value;
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

	public String toString() {
		return Utils.render(width, height, new Renderer() {
			public char paint(int position) {
				return Utils.color255(get(position));
			}
		}).toString();
	}
}
