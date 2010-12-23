package engine;

import util.Renderer;
import util.Utils;

public abstract class Sensor {
	protected final Field field;
	protected final Options opt;

	private double boost;
	private double activeDutyCycle;
	private double overlapDutyCycle;
	private double revTime;

	public Sensor(Options opt, Field field) {
		this.activeDutyCycle = 0;
		this.field = field;
		this.opt = opt;
		this.revTime = opt.boostTimeRev();
		boost = 1;
	}

	public int sum() {
		int overlap = getOverlap();

		if (overlap < opt.SENSOR_MIN_OVERLAP) {
			overlap = 0;
		} else {
			overlap *= boost;
		}
		activeDutyCycle = activeDutyCycle * (1 - revTime);
		overlapDutyCycle = overlapDutyCycle * (1 - revTime);
		if (overlap > 0) {
			overlapDutyCycle += revTime;
		}
		return overlap;
	}

	public void updateWinner() {
		updatePermanence();
		activeDutyCycle += revTime;
	}

	double activeDutyCycle() {
		return activeDutyCycle;
	}

	public void updateSensor(double maxDutyCycle) {
		double minDutyCycle = 0.01 * maxDutyCycle;
		if (activeDutyCycle < minDutyCycle) {
			boost = boost + opt.SENSOR_BOOST;
		} else {
			boost = 1;
		}
		if (overlapDutyCycle < minDutyCycle) {
			boostPermanence();
		}
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
