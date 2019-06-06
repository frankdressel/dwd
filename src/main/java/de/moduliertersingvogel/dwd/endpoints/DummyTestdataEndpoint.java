package de.moduliertersingvogel.dwd.endpoints;

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

import de.moduliertersingvogel.dwd.services.DataService;

@ApplicationScoped
@Path("test")
public class DummyTestdataEndpoint {
	@Inject
	DataService dataservice;

	private static final Logger logger = LogManager.getFormatterLogger("dwd");

	@Path("/test1")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response test1(@PathParam("location") String location,
			@PathParam("relativetime") String relativetime) {
		logger.debug("Providing test1 data");
		return Response.status(Status.OK).entity(15).build();
	}

	@Path("/test2")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response test2(@PathParam("location") String location,
			@PathParam("relativetime") String relativetime) {
		logger.debug("Providing test1 data");
		return Response.status(Status.OK).entity(45).build();
	}

	@Path("/test3")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response test3(@PathParam("location") String location,
			@PathParam("relativetime") String relativetime) {
		logger.debug("Providing test1 data");
		return Response.status(Status.OK).entity(80).build();
	}
}
