package engine;

interface SensorZone {
	int getPermanence(int position);

	int getOverlap();

	void updatePermanence();

	void boostPermanence();

	int height();
}
