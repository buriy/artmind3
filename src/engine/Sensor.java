/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package engine;

import java.util.Collection;

/**
 *
 * @author dimko
 */
public class Sensor {
	private int[] samples;
	private final IntField field;

	public Sensor(IntField field, int quantity) {
		this.field = field;
		Collection<Integer> sample = Utils.sample(field.size, quantity);
		this.samples = new int[quantity];
		int i=0;
		for (Integer value : sample) {
			this.samples[i++] = value;
		}
	}

	public void replace_sample(int position, int value) {
		this.samples[position] = value;
	}

	public int sum() {
		int value = 0;
		for (int item : samples) {
			value += field.get(item);
		}
		return value;
	}
}
