package engine;

import util.Rand;

public class ZoneSensor extends Sensor {
	private byte[][] zone;
	private int minX;
	private int minY;
	private int maxX;
	private int maxY;
	private int width;
	private int height;
	private int centerX;
	private int centerY;

	public ZoneSensor(Options opt, Field field) {
		super(opt, field);
		int r4 = opt.SENSORS_RADIUS / 4;
		centerX = Rand.range(Math.max(field.width() - r4 * 2, 1)) + r4;
		centerY = Rand.range(Math.max(field.height() - r4 * 2, 1)) + r4;
		minX = Math.max(centerX - opt.SENSORS_RADIUS, 0);
		maxX = Math.min(centerX + opt.SENSORS_RADIUS, field.width() - 1);
		minY = Math.max(centerY - opt.SENSORS_RADIUS, 0);
		maxY = Math.min(centerY + opt.SENSORS_RADIUS, field.height() - 1);
		width = maxX - minX + 1;
		height = maxY - minY + 1;
		zone = new byte[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				zone[x][y] = (byte) Rand.range(100);
			}
		}
	}

	@Override
	protected int getOverlap() {
		int overlap = 0;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (zone[x][y] >= opt.SENSOR_PERMANENCE_CONNECTED) {
					overlap += field.get(x + minX, y + minY);
				}
			}
		}

		if (overlap < opt.SENSOR_ZONE_OVERLAP) {
			overlap = 0;
		} else {
			overlap *= boost;
		}

		return overlap;
	}

	protected void updatePermanence() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (field.test(x + minX, y + minY)) {
					zone[x][y] = (byte) Math.min(zone[x][y] + opt.SENSOR_PERMANENCE_INC, 100);
				} else {
					zone[x][y] = (byte) Math.max(zone[x][y] - opt.SENSOR_PERMANENCE_DEC, 0);
				}
			}
		}
	}

	protected void boostPermanence() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double increase = zone[x][y] + 0.1 * opt.SENSOR_PERMANENCE_CONNECTED;
				zone[x][y] = (byte) Math.min(increase, 100);
			}
		}
	}

	protected int getDebugPermanence(int position) {
		return getPermanence(position);
//		int x = position % field.width();
//		int y = position / field.width();
//		if (x != centerX || y != centerY) return 0;
//		return 255;
	}

	protected int getPermanence(int position) {
		int x = position % field.width();
		int y = position / field.width();
		if (x < minX || y < minY || x > maxX || y > maxY) {
			return 0;
		}
		return zone[x - minX][y - minY];
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