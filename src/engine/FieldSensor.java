package engine;

public class FieldSensor extends Sensor {
	public FieldSensor(Options opt, Field... fields) {
		super(opt);
		zones = new SensorZone[fields.length];
		int i = 0;
		for(Field f: fields){
			zones[i++] = new FieldSensorZone(f, opt);
		}
	}
}