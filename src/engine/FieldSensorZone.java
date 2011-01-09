package engine;

import util.Rand;
import util.Renderer;
import util.Utils;

public class FieldSensorZone implements SensorZone {
	int[] permanence;
	private final Field field;
	private final Options opt;

	public FieldSensorZone(Field field, Options opt) {
		this.field = field;
		this.opt = opt;
		this.permanence = new int[field.size()];
		for (int i = 0; i < permanence.length; i++) {
			permanence[i] = Rand.range(100);
		}
	}

	public int getOverlap() {
		int overlap = 0;

		for (int i = 0; i < permanence.length; i++) {
			if (permanence[i] >= opt.SENSOR_PERMANENCE_CONNECTED) {
				overlap += field.get(i);
			}
		}
		return overlap;
	}

	public int getPermanence(int position) {
		return permanence[position];
	}

	public void updatePermanence() {
		for (int i = 0; i < permanence.length; i++) {
			if (field.test(i)) {
				permanence[i] = Math.min(permanence[i] + opt.SENSOR_PERMANENCE_INC, 100);
			} else {
				permanence[i] = Math.max(permanence[i] - opt.SENSOR_PERMANENCE_DEC, 0);
			}
		}
	}

	public void boostPermanence() {
		for (int i = 0; i < permanence.length; i++) {
			double increase = permanence[i] + 0.1 * opt.SENSOR_PERMANENCE_CONNECTED;
			permanence[i] = (int) Math.min(increase, 100);
		}
	}

	public String toString() {
		return Utils.render(field.width(), field.height(), new Renderer() {
			public char paint(int position) {
				return Utils.color100(getPermanence(position));
			}
		}).toString();
	}

	public Field field() {
		return field;
	}
}
