package example.jersey.rest.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("greeter")
public class Controller {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response greet() {
		String message = "Hello World!";
		return Response.ok(message, MediaType.APPLICATION_JSON).build();
	}
	
}
