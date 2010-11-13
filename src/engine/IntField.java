package engine;


/**
 *
 * @author dimko
 */
public class IntField implements Field{
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
    
    public String toString(){
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
		return data[y*width + x];
	}

	public void reset() {
        for (int i = 0; i < size; i++) {
        	data[i] = 0;
        }
	}

	public void set(int x, int y, int value) {
		data[y*width + x] = value;
	}

	public int getSize() {
		return size;
	}
}
