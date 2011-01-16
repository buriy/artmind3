package engine;

import util.Rand;
import util.Renderer;
import util.Utils;

public class ZoneSensorZone implements SensorZone {
	private final byte[][] zone;
	private final int minX;
	private final int minY;
	private final int maxX;
	private final int maxY;
	private final int width;
	private final int height;
	private final Field field;
	private final Options opt;

	public ZoneSensorZone(Field field, Options opt, int centerX, int centerY) {
		this.field = field;
		this.opt = opt;
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

	public int getOverlap() {
		int overlap = 0;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (zone[x][y] >= opt.SENSOR_PERMANENCE_CONNECTED) {
					overlap += field.get(x + minX, y + minY);
				}
			}
		}
		return overlap;
	}

	@Override
	public int getPermanence(int position) {
		int x = position % field.width();
		int y = position / field.width();
		if (x < minX || y < minY || x > maxX || y > maxY) {
			return 0;
		}
		return zone[x - minX][y - minY];
	}

	@Override
	public void updatePermanence() {
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

	@Override
	public void boostPermanence() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double increase = zone[x][y] + 0.1 * opt.SENSOR_PERMANENCE_CONNECTED;
				zone[x][y] = (byte) Math.min(increase, 100);
			}
		}
	}

	@Override
	public Field field() {
		return field;
	};
	
	public String toString() {
		return Utils.render(field.width(), field.height(), new Renderer() {
			public char paint(int position) {
				return Utils.color100(getPermanence(position));
			}
		}).toString();
	}
}
