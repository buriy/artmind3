package engine;

import java.util.ArrayList;

public abstract class Sensor {
	protected final Options opt;

	protected double boost;
	private double activeDutyCycle;
	private double overlapDutyCycle;
	private double revTime;

	protected ArrayList<SensorZone> zones;

	public Sensor(Options opt) {
		this.activeDutyCycle = 0;
		this.opt = opt;
		this.revTime = opt.boostTimeRev();
		this.zones = new ArrayList<SensorZone>();
		boost = 1;
	}

	abstract void addSecondaryZone(Field field);
	
	public int sum() {
		int overlap = getOverlap();

		activeDutyCycle = activeDutyCycle * (1 - revTime);
		overlapDutyCycle = overlapDutyCycle * (1 - revTime);
		if (overlap > 0) {
			overlapDutyCycle += revTime;
		}
		
		return overlap;
	}

	public void updateWinner() {
		updatePermanence();
		activeDutyCycle += revTime;
	}

	double activeDutyCycle() {
		return activeDutyCycle;
	}

	public void updateSensor(double maxDutyCycle) {
		double minDutyCycle = 0.01 * maxDutyCycle;
		if (activeDutyCycle < minDutyCycle) {
			boost = boost + opt.SENSOR_BOOST;
		} else {
			boost = 1;
		}
		if (overlapDutyCycle < minDutyCycle) {
			boostPermanence();
		}
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		for(SensorZone z: zones){
			result.append(z.toString());
		}
		result.append("\nboost=" + boost);
		result.append("\nactiveDutyCycle=" + activeDutyCycle);
		result.append("\noverlapDutyCycle=" + overlapDutyCycle + "\n");
		return result.toString();
	}

	protected void updatePermanence() {
		for(SensorZone zone: zones){
			zone.updatePermanence();
		}
	}

	protected void boostPermanence() {
		for(SensorZone zone: zones){
			zone.boostPermanence();
		}
	}

	protected int getOverlap() {
		int overlap = 0;
		int sum = 0;

		for (SensorZone zone : zones) {
			int multiplier = opt.SENSOR_ZONE_MULTIPLIER / zone.size();
			overlap += zone.getOverlap() * multiplier;
			sum += zone.size() * multiplier;
		}
	
		if (overlap < opt.SENSOR_FIELD_OVERLAP * sum / 1024) {
			overlap = 0;
		} else {
			overlap *= boost;
		}
	
		return overlap;
	}

	SensorZone getZoneForField(Field field){
		for(SensorZone zone: zones){
			if(zone.field() == field){
				return zone;
			}
		}
		return null;
	}
	
	public void sumOverField(int[] values, Field input, double weight) {
		SensorZone zone = getZoneForField(input);
		for (int i = 0; i < input.size(); i++) {
			values[i] += (zone.getPermanence(i)>=opt.SENSOR_PERMANENCE_CONNECTED?100:0) * weight;
		}
	}

	public int distanceTo(Sensor rhs) {
		return 0;
	}

	public float getRadius() {
		return opt.SENSORS_RADIUS;
	}
}
