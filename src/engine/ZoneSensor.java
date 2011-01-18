package engine;

import util.Rand;

public class ZoneSensor extends FieldSensor {
	private int centerX;
	private int centerY;
	private ZoneSensorZone mainZone;

	public ZoneSensor(Options opt, Field field) {
		super(opt, field);
	}
	
	@Override
	protected int getOverlap() {
		int overlap = 0;
		int sum = 0;
		
		for (SensorZone zone : zones) {
			int multiplier = opt.SENSOR_ZONE_MULTIPLIER / zone.size();
			overlap += zone.getOverlap() * multiplier;
			sum += zone.size() * multiplier;
		}
		
		if (overlap < opt.SENSOR_ZONE_OVERLAP * sum / 1024) {
			overlap = 0;
		} else {
			overlap *= boost;
		}
		
		return overlap;
	}

	protected void createZones(Field field) {
		int r4 = opt.SENSORS_RADIUS / 4;
		int width = Math.max(field.width() - r4 * 2, 0);
		int height = Math.max(field.height() - r4 * 2, 0);
		centerX = Rand.range(width) + r4;
		centerY = Rand.range(height) + r4;
		mainZone = new ZoneSensorZone(field, opt, centerX, centerY);
		zones.add(mainZone);
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