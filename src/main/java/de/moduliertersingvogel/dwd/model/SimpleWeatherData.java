package de.moduliertersingvogel.dwd.model;

import java.time.LocalDateTime;

public class SimpleWeatherData {
	public final float temperature;
	public final float precipitationProb;
	public final LocalDateTime time;
	
	public SimpleWeatherData(float temperature, float perceptionProb, LocalDateTime time) {
		this.temperature = temperature;
		this.precipitationProb = perceptionProb;
		this.time = time;
	}
}
