package engine;

import util.Rand;

public class ZoneSensor extends Sensor {
	private int centerX;
	private int centerY;

	public ZoneSensor(Options opt, Field field) {
		super(opt);
		zone = new ZoneSensorZone(field, opt, centerX, centerY);
		int r4 = opt.SENSORS_RADIUS / 4;
		centerX = Rand.range(Math.max(field.width() - r4 * 2, 1)) + r4;
		centerY = Rand.range(Math.max(field.height() - r4 * 2, 1)) + r4;
	}

	@Override
	protected int getOverlap() {
		int overlap = zone.getOverlap();

		if (overlap < opt.SENSOR_ZONE_OVERLAP) {
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

	public int getDebugPermanence(int position) {
		return getPermanence(position);
//		int x = position % field.width();
//		int y = position / field.width();
//		if (x != centerX || y != centerY) return 0;
//		return 255;
	}

	public int getPermanence(int position) {
		return zone.getPermanence(position);
	}

	public int distanceTo(Sensor rhs) {
		if (rhs instanceof ZoneSensor) {
			ZoneSensor zs = (ZoneSensor) rhs;
			int dx = centerX - zs.centerX;
			int dy = centerY - zs.centerY;
			return dx * dx + dy * dy;
		}
		return 0;
	}
}