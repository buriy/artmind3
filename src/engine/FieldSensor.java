package engine;

import util.Rand;

public class FieldSensor extends Sensor {
	int[] permanence;

	public FieldSensor(Options opt, Field field) {
		super(opt, field);

		this.permanence = new int[field.size()];
		for (int i = 0; i < permanence.length; i++) {
			permanence[i] = Rand.range(100);
		}
	}

	@Override
	protected int getOverlap() {
		int overlap = 0;
		
		for (int i = 0; i < permanence.length; i++) {
			if (permanence[i] >= opt.SENSOR_PERMANENCE_CONNECTED) {
				overlap += field.get(i);
			}
		}
		return overlap;
	}
	
	protected void updatePermanence() {
		for (int i = 0; i < permanence.length; i++) {
			if (field.test(i)) {
				permanence[i] = Math.min(permanence[i] + opt.SENSOR_PERMANENCE_INC, 100);
			} else {
				permanence[i] = Math.max(permanence[i] - opt.SENSOR_PERMANENCE_DEC, 0);
			}
		}
	}

	protected void boostPermanence() {
		for (int i = 0; i < permanence.length; i++) {
			double increase = permanence[i] + 0.1 * opt.SENSOR_PERMANENCE_CONNECTED;
			permanence[i] = (int) Math.min(increase, 100);
		}
	}

	protected int getPermanence(int position){
		return permanence[position];
	}

	@Override
	public int distanceTo(Sensor rhs) {
		return 0;
	}
}