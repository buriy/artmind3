package engine;


public class FieldSensor extends Sensor {
	public FieldSensor(Options opt, Field field) {
		super(opt);
		createZones(field);
	}

	protected void createZones(Field field) {
		zones.add(new FieldSensorZone(field, opt));
	}

	@Override
	void addSecondaryZone(Field field) {
		zones.add(new FieldSensorZone(field, opt));
	}
}