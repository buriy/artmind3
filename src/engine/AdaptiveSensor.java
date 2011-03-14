package engine;

import util.Rand;

public class AdaptiveSensor extends FieldSensor {
	private int centerX;
	private int centerY;
	private AdaptiveSensorZone mainZone;

	public AdaptiveSensor(Options opt, Field field) {
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
		int width = field.width();
		int height = field.height();
		centerX = Rand.range(width);
		centerY = Rand.range(height);
		mainZone = new AdaptiveSensorZone(field, opt, centerX, centerY);
		zones.add(mainZone);
	}

	public int distanceTo(Sensor rhs) {
		if (rhs instanceof AdaptiveSensor) {
			AdaptiveSensor zs = (AdaptiveSensor) rhs;
			int dx = centerX - zs.centerX;
			int dy = centerY - zs.centerY;
			return dx * dx + dy * dy;
		}
		return 0;
	}
	
	@Override
	public float getRadius() {
		return mainZone.avgDistance();
	}
}