package de.moduliertersingvogel.dwd.model;

public class SimpleWeatherData {
	public final float temperature;
	public final float precipitationProb;
	
	public SimpleWeatherData(float temperature, float precipitationProb) {
		this.temperature = temperature;
		this.precipitationProb = precipitationProb;
	}
}
