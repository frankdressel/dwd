package de.moduliertersingvogel.dwd.model;

public class RelativeSimpleWeatherData extends SimpleWeatherData {
	public final int minutesInFuture;

	public RelativeSimpleWeatherData(float temperature, float precipitationProb, int minutesInFuture) {
		super(temperature, precipitationProb);
		this.minutesInFuture = minutesInFuture;
	}
}
