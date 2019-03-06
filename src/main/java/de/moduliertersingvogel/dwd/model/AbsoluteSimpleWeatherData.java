package de.moduliertersingvogel.dwd.model;

import java.time.LocalDateTime;

public class AbsoluteSimpleWeatherData extends SimpleWeatherData{
	public final LocalDateTime time;
	
	public AbsoluteSimpleWeatherData(float temperature, float precipitationProb, LocalDateTime time) {
		super(temperature, precipitationProb);
		this.time = time;
	}
}
