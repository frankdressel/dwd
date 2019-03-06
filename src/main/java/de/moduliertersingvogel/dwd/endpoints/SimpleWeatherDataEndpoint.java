package de.moduliertersingvogel.dwd.endpoints;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.moduliertersingvogel.dwd.model.AbsoluteSimpleWeatherData;
import de.moduliertersingvogel.dwd.model.RelativeSimpleWeatherData;
import de.moduliertersingvogel.dwd.services.DataService;

@ApplicationScoped
@Path("simple")
public class SimpleWeatherDataEndpoint {

	@Inject
	DataService dataservice;

	private static final Logger logger = LogManager.getFormatterLogger("dwd");

	@Path("/{location}/relative")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<RelativeSimpleWeatherData> getRelativeSimpleWeatherData(@PathParam("location") String location) {
		logger.debug("GET simple/%s.", location);

		LocalDateTime now = LocalDateTime.now(ZoneId.of("Berlin"));
		return dataservice.getSimpleWeatherData(location).stream()
				.filter(s -> (s.time.isAfter(now) || s.time.isEqual(now)))
				.map(s -> new RelativeSimpleWeatherData(s.temperature, s.precipitationProb,
						(int) Duration.between(now, s.time).get(ChronoUnit.MINUTES)))
				.collect(Collectors.toList());
	}

	@Path("/{location}/absolute")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<AbsoluteSimpleWeatherData> getAbsoluteSimpleWeatherData(@PathParam("location") String location) {
		logger.debug("GET simple/%s.", location);

		return dataservice.getSimpleWeatherData(location);
	}
}
