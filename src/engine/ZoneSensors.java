package engine;

import java.util.LinkedList;
import java.util.TreeSet;

public class ZoneSensors extends Sensors {
	protected LinkedList<Integer>[] nearest;

	@SuppressWarnings("unchecked")
	public ZoneSensors(Options opt, Field input) {
		super(opt, input);
		int count = opt.SENSORS;
		nearest = new LinkedList[count];
		for (int i = 0; i < opt.SENSORS; ++i) {
			nearest[i] = new LinkedList<Integer>();
		}
		double rd = opt.SENSORS_RADIUS * opt.SENSORS_ZONE_DISTANCE + 1e-8;
		int radius2 = (int) (rd * rd);
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < count; j++) {
				if (sensors[i].distanceTo(sensors[j]) < radius2) {
					nearest[i].add(j);
				}
			}
		}
	}

	protected void createSensors(Options opt, Field input) {
		sensors = new ZoneSensor[opt.SENSORS];
		for (int i = 0; i < opt.SENSORS; ++i) {
			sensors[i] = new ZoneSensor(opt, input);
		}
	}

	protected boolean isWinner(int[] values, int source) {
		if (values[source] == 0)
			return false;
		if (nearest[source].size() <= opt.SENSORS_ZONE_WINNERS) {
			return true;
		}
		TreeSet<Integer> winners = new TreeSet<Integer>();
		Integer candidate = 0;
		for (int j : nearest[source]) {
			if (winners.size() < opt.SENSORS_ZONE_WINNERS) {
				winners.add(values[j]);
				candidate = winners.first();
			} else {
				if (candidate < values[j]) {
					winners.add(values[j]);
					winners.remove(candidate);
					candidate = winners.first();
				}
			}
		}
		return values[source] >= candidate;
	}

	protected int[] getWinners(int[] values) {
		LinkedList<Integer> candidates = new LinkedList<Integer>();
		for (int i = 0; i < values.length; i++) {
			if (isWinner(values, i)) {
				candidates.add(i);
			}
		}
		int size = candidates.size();
		int[] winners = new int[size];
		for (int i = 0; i < size; i++) {
			winners[i] = candidates.poll();
		}
		return winners;
	}

	@Override
	public int[] learn() {
		int[] winners = operate();
		double maxDutyCycle[] = new double[sensors.length];
		for (int i = 0; i < sensors.length; ++i) {
			for (int j : nearest[i]) {
				if (sensors[j].activeDutyCycle() > maxDutyCycle[i]) {
					maxDutyCycle[i] = sensors[j].activeDutyCycle();
				}
			}
		}
		for (int i = 0; i < winners.length; i++) {
			sensors[winners[i]].updateWinner();
		}
		for (int i = 0; i < sensors.length; i++) {
			sensors[i].updateSensor(maxDutyCycle[i]);
		}
		return winners;
	}
}
