package engine;

import java.util.ArrayList;
import java.util.Comparator;

import util.Utils;

public class AdaptiveSensors extends FieldSensors {
	protected ArrayList<Integer>[] nearest;

	@SuppressWarnings("unchecked")
	public AdaptiveSensors(Options opt, Field field) {
		super(opt, field);
		int count = opt.SENSORS;
		nearest = new ArrayList[count];
		for (int sid = 0; sid < count; sid++) {
			nearest[sid] = findNearest(sensors[sid]);
		}
	}

	protected void createSensors(Field field) {
		this.fields.add(field);
		sensors = new AdaptiveSensor[opt.SENSORS];
		for (int i = 0; i < opt.SENSORS; ++i) {
			sensors[i] = new AdaptiveSensor(opt, field);
		}
	}

	protected boolean isWinner(final int[] values, int source) {
		if (values[source] == 0)
			return false;
		final ArrayList<Integer> neighbours = nearest[source];
		int size = neighbours.size();
		if (size <= opt.SENSORS_ZONE_WINNERS) {
			return true;
		}
		final int bottom = values[source];
		int[] ids = Utils.shuffle(size);
		int[] winners = Utils.topK(ids, opt.SENSORS_ZONE_WINNERS, new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return values[neighbours.get(o2)] - (o1 == null ? bottom+1 : values[neighbours.get(o1)]);
			}
		});
		return winners.length < opt.SENSORS_ZONE_WINNERS;
	}

	public int[] getWinners(int[] values) {
		ArrayList<Integer> candidates = new ArrayList<Integer>();
		for (int i = 0; i < values.length; i++) {
			if (isWinner(values, i)) {
				candidates.add(i);
			}
		}
		return Utils.toIntArray(candidates);
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
