package engine;

import util.Utils;

public class FieldSensors implements Sensors {
	protected Options opt;
	protected int[] last_winners;
	protected Sensor[] sensors;
	protected Field[] fields;

	public FieldSensors(Options opt, Field... fields) {
		this.opt = opt;
		this.fields = fields;
		createSensors(fields);
	}

	protected void createSensors(Field... fields) {
		this.sensors = new FieldSensor[opt.SENSORS];
		for (int i = 0; i < sensors.length; i++) {
			sensors[i] = new FieldSensor(opt, fields);
		}
	}

	public int[] operate() {
		int[] values = new int[sensors.length];
		for (int i = 0; i < values.length; ++i) {
			values[i] = sensors[i].sum();
		}
		int[] winners = getWinners(values);
		last_winners = winners;
		return winners;
	}

	public int[] getWinners(int[] values) {
		int[] winners = Utils.binarize(values, opt.SENSORS_WINNERS);
		return winners;
	}

	public String toString() {
		if (last_winners == null) {
			return "Not ready";
		}
		if (last_winners.length == 0) {
			return "Empty";
		}

		String data = "";
		for (Field field : fields) {
			data += restoreWinners(last_winners, field) + "\n";
		}
		return data;
	}

	public String restoreWinners(int[] winners, Field input) {
		int values[] = new int[input.size()];
		for (int j = 0; j < winners.length; j++) {
			Sensor winner = sensors[winners[j]];
			winner.sumOverField(values, input, 1);
		}
		return Utils.renderValues(input.width(), input.height(), values).toString();
	}

	public int[] restore(int[] source, double divider, Field input) {
		int values[] = new int[input.size()];
		for (int s = 0; s < source.length; s++) {
			int multiplier = (source[s] != 0) ? 1 : 0;
			Sensor sensor = sensors[s];
			sensor.sumOverField(values, input, multiplier / divider);
		}
		return values;
	}

	public int[] learn() {
		int[] winners = operate();
		double maxDutyCycle = 0;
		for (int i = 0; i < sensors.length; ++i) {
			if (sensors[i].activeDutyCycle() > maxDutyCycle) {
				maxDutyCycle = sensors[i].activeDutyCycle();
			}
		}
		for (int i = 0; i < winners.length; i++) {
			sensors[winners[i]].updateWinner();
		}
		for (int i = 0; i < sensors.length; i++) {
			sensors[i].updateSensor(maxDutyCycle);
		}
		return winners;
	}

	public int[] run() {
		return operate();
	}
}
