package engine;

public class FieldSensor extends Sensor {
	public FieldSensor(Options opt, Field field) {
		super(opt);
		zone = new FieldSensorZone(field, opt);
	}

	@Override
	protected int getOverlap() {
		int overlap = zone.getOverlap();

		if (overlap < opt.SENSOR_MIN_OVERLAP) {
			overlap = 0;
		} else {
			overlap *= boost;
		}

		return overlap;
	}

	protected void updatePermanence() {
		zone.updatePermanence();
	}

	protected void boostPermanence() {
		zone.boostPermanence();
	}

	@Override
	public int distanceTo(Sensor rhs) {
		return 0;
	}

	@Override
	public int getDebugPermanence(int position) {
		return zone.getPermanence(position);
	}

	@Override
	public int getPermanence(int position) {
		return zone.getPermanence(position);
	}
}