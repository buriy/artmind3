package engine;

import util.Rand;

public class ZoneSensor extends Sensor {
	private int centerX;
	private int centerY;
	private ZoneSensorZone mainZone;

	public ZoneSensor(Options opt, Field main, Field... fields) {
		super(opt);
		int r4 = opt.SENSORS_RADIUS / 4;
		mainZone = createMainZone(opt, main, r4);
		zones = new SensorZone[fields.length + 1];
		int i = 0;
		zones[i++] = mainZone;
		for(Field f: fields){
			zones[i++] = new FieldSensorZone(f, opt);
		}
		zones = new SensorZone[] { mainZone };
	}

	private ZoneSensorZone createMainZone(Options opt, Field field, int r4) {
		int width = Math.max(field.width() - r4 * 2, 0);
		int height = Math.max(field.height() - r4 * 2, 0);
		centerX = Rand.range(width) + r4;
		centerY = Rand.range(height) + r4;
		return new ZoneSensorZone(field, opt, centerX, centerY);
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