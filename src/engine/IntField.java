package engine;

import util.Renderer;
import util.Utils;

public class IntField implements Field {
	int size;
	int width;
	int height;
	int data[];

	public IntField(int width, int height) {
		this.size = width * height;
		this.width = width;
		this.height = height;
		this.data = new int[size];
	}

	public int get(int pos) {
		return data[pos];
	}

	public int get(int x, int y) {
		return data[y * width + x];
	}

	public void reset() {
		for (int i = 0; i < size; i++) {
			data[i] = 0;
		}
	}

	public int size() {
		return size;
	}

	public boolean test(int pos) {
		return data[pos] >= 128;
	}

	public void set(int x, int y, int value) {
		data[y * width + x] = value;
	}

	public void set(int x, int y, boolean value) {
		data[y * width + x] = value ? 255 : 0;
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
