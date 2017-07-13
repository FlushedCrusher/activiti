package jersey.rest.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api")
public class Controller {

	@GET
	@Path("/greeter")
	@Produces(MediaType.APPLICATION_JSON)
	public Response greet() {
		String greeting = "Hello World!";
		return Response.ok(greeting, MediaType.APPLICATION_JSON).build();
	}
	
}
