package engine;

import util.Rand;
import util.Renderer;
import util.Utils;

public class AdaptiveSensorZone implements SensorZone {
	int[] permanence;
	private final Field field;
	private final Options opt;
	private final int centerX;
	private final int centerY;
	private float avgDistance;

	public AdaptiveSensorZone(Field field, Options opt, int centerX, int centerY) {
		this.field = field;
		this.opt = opt;
		this.centerX = centerX;
		this.centerY = centerY;
		this.permanence = new int[field.size()];
		this.avgDistance = 0;
		for (int i = 0; i < permanence.length; i++) {
			permanence[i] = Rand.range(100);
		}
		updateRadius();
	}

	public int getOverlap() {
		int overlap = 0;

		for (int i = 0; i < permanence.length; i++) {
			if (permanence[i] >= opt.SENSOR_PERMANENCE_CONNECTED) {
				overlap += field.get(i);
			}
		}
		return overlap;
	}

	public int getPermanence(int position) {
		return permanence[position];
	}

	public void updatePermanence() {
		for (int i = 0; i < permanence.length; i++) {
			if (field.test(i)) {
				permanence[i] = Math.min(permanence[i] + opt.SENSOR_PERMANENCE_INC, 100);
			} else {
				permanence[i] = Math.max(permanence[i] - opt.SENSOR_PERMANENCE_DEC, 0);
			}
		}
		updateRadius();
	}

	private void updateRadius() {
		float sumDistances = 0;
		int numDistances = 0;
		for (int i = 0; i < permanence.length; i++) {
			if(permanence[i] >= opt.SENSOR_PERMANENCE_CONNECTED){
				int deltaX = i % field.width() - centerX; 
				int deltaY = i / field.width() - centerY;
				sumDistances += Math.sqrt(deltaX*deltaX + deltaY*deltaY);
				numDistances ++;
			}
		}
		if(numDistances > 0){
			this.avgDistance = sumDistances / numDistances; 
		}else{
			this.avgDistance = 0;
		}
	}

	public void boostPermanence() {
		for (int i = 0; i < permanence.length; i++) {
			double increase = permanence[i] + 0.1 * opt.SENSOR_PERMANENCE_CONNECTED;
			permanence[i] = (int) Math.min(increase, 100);
		}
	}

	public String toString() {
		return Utils.render(field.width(), field.height(), new Renderer() {
			public char paint(int position) {
				return Utils.color100(getPermanence(position));
			}
		}).append(" position:("+centerX+","+centerY+")\n  radius: "+avgDistance).toString();
	}

	public Field field() {
		return field;
	}

	@Override
	public int size() {
		return field.size();
	}

	public float avgDistance() {
		return avgDistance;
	}
}
