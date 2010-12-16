package engine;

import util.Renderer;
import util.Utils;

public abstract class Sensor {
	protected final Field field;
	protected final Options opt;

	private double boost;
	private double activeDutyCycle;
	private double overlapDutyCycle;

	public Sensor(Options opt, Field field) {
		this.activeDutyCycle = 0;
		this.field = field;
		this.opt = opt;
		boost = 1;
	}

	public int sum() {
		int overlap = getOverlap();

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
		updatePermanence();
		activeDutyCycle += 0.001;
	}

	double activeDutyCycle() {
		return activeDutyCycle;
	}

	public void updateSensor(double maxDutyCycle) {
		double minDutyCycle = 0.01 * maxDutyCycle;
		boost = boostFunction(activeDutyCycle, minDutyCycle);
		if (overlapDutyCycle < minDutyCycle) {
			boostPermanence();
		}
	}

	private double boostFunction(double activeDutyCycle, double minDutyCycle) {
		if (activeDutyCycle > minDutyCycle)
			return 1;
		return boost + opt.SENSOR_BOOST;
	}

	public String toString() {
		StringBuilder result = Utils.render(field.width(), field.height(), new Renderer() {
			public char paint(int position) {
				return Utils.color100(getPermanence(position));
			}
		});
		result.append("\nboost=" + boost);
		result.append("\nactiveDutyCycle=" + activeDutyCycle);
		result.append("\noverlapDutyCycle=" + overlapDutyCycle);
		return result.toString();
	}

	protected abstract int getOverlap();
	protected abstract int getPermanence(int position);
	protected abstract void updatePermanence();
	protected abstract void boostPermanence();
	public abstract int distanceTo(Sensor rhs);
}

