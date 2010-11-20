package engine;

import util.Utils;

public class Sensors {
	private Sensor[] sensors;
	private Options opt;

	public Sensors(Options opt, Field input) {
		this.opt = opt;
		sensors = new Sensor[opt.SENSORS];
		for (int i = 0; i < opt.SENSORS; ++i) {
			sensors[i] = new Sensor(opt, input);
		}
	}

	protected int[] operate() {
		int[] values = new int[sensors.length];
		double maxDutyCycle = 0;
		for (int i = 0; i < values.length; ++i) {
			values[i] = sensors[i].sum();
			if (sensors[i].activeDutyCycle() > maxDutyCycle) {
				maxDutyCycle = sensors[i].activeDutyCycle();
			}
		}
		int[] winners = Utils.binarize(values, opt.SENSOR_WINNERS);
		for (int i = 0; i < winners.length; i++) {
			sensors[winners[i]].updateWinner();
		}
		for (int i = 0; i < sensors.length; i++) {
			sensors[i].updateSensor(maxDutyCycle);
		}
		return winners;
	}


}
