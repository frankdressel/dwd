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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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

	@Path("/relative/{location}/{relativetime}/precipitation")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRelativeSimpleWeatherData(@PathParam("location") String location,
			@PathParam("relativetime") String relativetime) {
		logger.debug("GET simple/%s.", location);

		final String timetest = "\\d+[hH]";
		if (relativetime.matches(timetest)) {
			return getRelativeTimeData(location, relativetime);
		}
		final String durationtest = "\\d+-\\d+[hH]";
		if (relativetime.matches(durationtest)) {
			return getRelativeDurationData(location, relativetime);
		}
		return Response.status(Status.BAD_REQUEST)
				.entity(String.format("Only %s or %s is allowed as relative time", timetest, durationtest)).build();

	}

	private Response getRelativeTimeData(String location, String relativetime) {
		final int hour = Integer.valueOf(relativetime.replaceAll("[hH]", ""));

		LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Berlin"));
		List<AbsoluteSimpleWeatherData> elements = dataservice.getSimpleWeatherData(location).stream()
				.filter(s -> ((s.time.isAfter(now.plus(hour - 1, ChronoUnit.HOURS))
						&& s.time.isBefore(now.plus(hour, ChronoUnit.HOURS))) || s.time.isEqual(now))).collect(Collectors.toList());

		if(elements.size() == 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity(String.format("No data available")).build();
		}
		float average = elements.stream()
				.map(s -> new RelativeSimpleWeatherData(s.temperature, s.precipitationProb,
						(int) Duration.between(now, s.time).get(ChronoUnit.SECONDS) / 60))
				.map(s -> s.precipitationProb)
				.reduce(0f, (a, b) -> a + b)/elements.size();

		return Response.ok(average).build();
	}
	
	private Response getRelativeDurationData(String location, String relativetime) {
		final String duration = relativetime.replaceAll("[hH]", "");
		final int start = Integer.valueOf(duration.split("-")[0]);
		final int end = Integer.valueOf(duration.split("-")[1]);

		LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Berlin"));
		List<AbsoluteSimpleWeatherData> elements = dataservice.getSimpleWeatherData(location).stream()
				.filter(s -> ((s.time.isAfter(now.plus(start, ChronoUnit.HOURS))
						&& s.time.isBefore(now.plus(end, ChronoUnit.HOURS))) || s.time.isEqual(now))).collect(Collectors.toList());

		if(elements.size() == 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity(String.format("No data available")).build();
		}
		float max = elements.stream()
				.map(s -> new RelativeSimpleWeatherData(s.temperature, s.precipitationProb,
						(int) Duration.between(now, s.time).get(ChronoUnit.SECONDS) / 60))
				.map(s -> s.precipitationProb)
				.reduce(0f, (a, b) -> Math.max(a, b));

		return Response.ok(max).build();
	}

	@Path("/absolute/{location}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<AbsoluteSimpleWeatherData> getAbsoluteSimpleWeatherData(@PathParam("location") String location) {
		logger.debug("GET simple/%s.", location);

		return dataservice.getSimpleWeatherData(location);
	}
}
