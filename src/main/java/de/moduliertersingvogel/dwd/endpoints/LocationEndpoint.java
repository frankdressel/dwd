package de.moduliertersingvogel.dwd.endpoints;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.moduliertersingvogel.dwd.services.DataService;

@ApplicationScoped
@Path("locations")
public class LocationEndpoint {

	@Inject
	DataService dataservice;

	private static final Logger logger = LogManager.getFormatterLogger("dwd");

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getLocations() {
		logger.debug("GET locations");

		return dataservice.getPlacemarks();
	}
}
