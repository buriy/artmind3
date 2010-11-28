package engine;

import util.Rand;
import util.Renderer;
import util.Utils;

public class Sensor {
	int[] permanence;
	private final Field field;

	double boost;
	private final Options opt;
	private double activeDutyCycle;
	private double overlapDutyCycle;

	public Sensor(Options opt, Field field) {
		this.activeDutyCycle = 0;
		this.field = field;
		this.opt = opt;
		this.permanence = new int[field.size()];
		for (int i = 0; i < permanence.length; i++) {
			permanence[i] = Rand.range(100);
		}
		boost = 1;
	}

	public int sum() {
		int overlap = 0;

		for (int i = 0; i < permanence.length; i++) {
			if (permanence[i] >= opt.SENSOR_PERMANENCE_CONNECTED) {
				overlap += field.get(i);
			}
		}
		if (overlap < opt.SENSOR_MIN_OVERLAP) {
			overlap = 0;
		} else {
			overlap *= boost;
		}
		activeDutyCycle = activeDutyCycle * 0.999;
		overlapDutyCycle = overlapDutyCycle * 0.999;
		if (overlap > 0) {
			overlapDutyCycle += 0.001;
		}
		return overlap;
	}

	public void updateWinner() {
		for (int i = 0; i < permanence.length; i++) {
			if (field.test(i)) {
				permanence[i] = Math.min(permanence[i] + opt.SENSOR_PERMANENCE_INC, 100);
			} else {
				permanence[i] = Math.max(permanence[i] - opt.SENSOR_PERMANENCE_DEC, 0);
			}
		}
		activeDutyCycle += 0.001;
	}

	double activeDutyCycle() {
		return activeDutyCycle;
	}

	public void updateSensor(double maxDutyCycle) {
		double minDutyCycle = 0.01 * maxDutyCycle;
		boost = boostFunction(activeDutyCycle, minDutyCycle);
		if (overlapDutyCycle < minDutyCycle) {
			for (int i = 0; i < permanence.length; i++) {
				double increase = permanence[i] + 0.1 * opt.SENSOR_PERMANENCE_CONNECTED;
				permanence[i] = (int) Math.min(increase, 100);
			}
		}
	}

	private double boostFunction(double activeDutyCycle, double minDutyCycle) {
		if (activeDutyCycle > minDutyCycle)
			return 1;
		return boost + opt.SENSOR_BOOST;
	}

	public String toString() {
		StringBuilder result = Utils.render(field.width(), field.height(), new Renderer(){
			public char paint(int position) {
				return Utils.color100(permanence[position]);
			}
		});
		result.append("\nboost="+boost);
		result.append("\nactiveDutyCycle="+activeDutyCycle);
		result.append("\noverlapDutyCycle="+overlapDutyCycle);
		return result.toString();
	}
}
