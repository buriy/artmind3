package engine;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import util.Utils;

public class Sensors {
	Sensor[] sensors;
	private Options opt;
	private int[] last_winners;
	final Field input;

	public Sensors(Options opt, Field input) {
		this.opt = opt;
		this.input = input;
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
		last_winners = winners;
		for (int i = 0; i < winners.length; i++) {
			sensors[winners[i]].updateWinner();
		}
		for (int i = 0; i < sensors.length; i++) {
			sensors[i].updateSensor(maxDutyCycle);
		}
		return winners;
	}

	public String toString() {
		if (last_winners == null) {
			return "Not ready";
		}
		if (last_winners.length == 0) {
			return "Empty";
		}
		int[] data = restoreWinners(last_winners);
		return Utils.renderValues(input.width(), input.height(), data).toString();
	}

	public int[] restoreWinners(int[] winners) {
		int values[] = new int[input.size()];
		for (int i = 0; i < input.size(); i++) {
			int value = 0;
			for (int j = 0; j < winners.length; j++) {
				Sensor winner = sensors[winners[j]];
				value += winner.permanence[i];
			}
			values[i] = value;
		}
		return values;
	}

	public int[] restore(int[] source, double divider) {
		int values[] = new int[input.size()];
		for (int s=0; s<source.length; s++) {
			int multiplier = source[s];
			Sensor sensor = sensors[s];
			for (int i = 0; i < input.size(); i++) {
				if(multiplier > 0){
					values[i] += sensor.permanence[i] * multiplier / divider;
				}
			}
		}
		return values;
	}
}
