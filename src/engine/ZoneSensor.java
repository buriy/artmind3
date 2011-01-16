package engine;

import util.Rand;

public class ZoneSensor extends FieldSensor {
	private int centerX;
	private int centerY;
	private ZoneSensorZone mainZone;

	public ZoneSensor(Options opt, Field field) {
		super(opt, field);
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

	@Override
	void addSecondaryZone(Field field) {
		zones.add(new FieldSensorZone(field, opt));
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