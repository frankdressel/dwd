package de.moduliertersingvogel.dwd.services;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.moduliertersingvogel.dwd.model.SimpleWeatherData;
import de.moduliertersingvogel.dwd.services.Parser.KMLParserException;

@ApplicationScoped
public class DataService {

	private Parser parser = null;

	private static final Logger logger = LogManager.getFormatterLogger("dwd");

	private String mosmixfile;

	public DataService() {
		Configurations configs = new Configurations();
		try{
		    Configuration config = configs.properties(new File("conf/dwd.properties"));
		    mosmixfile = config.getString("mosmixfile", "MOSMIX_L_LATEST.kmz");
		}
		catch (ConfigurationException e){
		    logger.catching(e);
		}
		
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
		executorService.scheduleAtFixedRate(this::refresh, 0, 10L, TimeUnit.MINUTES);
	}

	public List<String> getPlacemarks() {
		logger.debug("Retrieving placemarks.");

		if (parser != null) {
			return parser.getPlacemarks();
		} else {
			logger.info("Parser not yet initialised");
			return Collections.emptyList();
		}
	}

	public List<SimpleWeatherData> getSimpleWeatherData(final String location) {
		logger.debug("Retrieving data for %s.", location);

		if (parser != null) {
			List<SimpleWeatherData> result = new ArrayList<>();

			Map<String, List<Float>> precipitationMap = parser.getPrecipitationMap();
			if (!precipitationMap.containsKey(location)) {
				logger.warn("Location without precipitation: %s", location);
				return Collections.emptyList();
			} else {
				List<Float> precipitation = precipitationMap.get(location);
				List<LocalDateTime> timestep = parser.getTimestamps();

				if (precipitation.size() != timestep.size()) {
					logger.error("Data does not match! Got %d entries for precipitation but %d entries for timesteps.",
							precipitation.size(), timestep.size());
				} else {
					List<SimpleWeatherData> data = IntStream.range(0, timestep.size())
							.mapToObj(index -> new SimpleWeatherData(0, precipitation.get(index), timestep.get(index)))
							.collect(Collectors.toList());
					result = Collections.unmodifiableList(data);
				}

				return result;
			}
		} else {
			logger.info("Parser not yet initialised");
			return Collections.emptyList();
		}
	}

	private void refresh() {
		try {
			parser = new Parser(Paths.get(mosmixfile));
		} catch (KMLParserException e) {
			logger.catching(e);
			this.parser = null;
		}
	}
}
