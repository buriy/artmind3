package engine;

public abstract class Sensor {
	protected final Options opt;

	protected double boost;
	private double activeDutyCycle;
	private double overlapDutyCycle;
	private double revTime;

	protected SensorZone zone;

	public Sensor(Options opt) {
		this.activeDutyCycle = 0;
		this.opt = opt;
		this.revTime = opt.boostTimeRev();
		boost = 1;
	}

	public int sum() {
		int overlap = getOverlap();

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
		StringBuilder result = new StringBuilder(zone.toString());
		result.append("\nboost=" + boost);
		result.append("\nactiveDutyCycle=" + activeDutyCycle);
		result.append("\noverlapDutyCycle=" + overlapDutyCycle + "\n");
		return result.toString();
	}

	protected abstract int getOverlap();

	protected abstract void updatePermanence();

	protected abstract void boostPermanence();

	public abstract int distanceTo(Sensor rhs);

	public abstract int getDebugPermanence(int i);

	public abstract int getPermanence(int i);
}
