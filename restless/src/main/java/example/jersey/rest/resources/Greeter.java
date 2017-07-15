package example.jersey.rest.resources;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("greeter")
public class Greeter {

	protected @Context UriInfo uriInfo;
	private String DEFAULT_GREETING;
	
	@PostConstruct
	public void onCreate() {
		DEFAULT_GREETING = "World";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response greet() {
		String message = uriInfo.getQueryParameters().getFirst("msg");
		if(message == null) message = DEFAULT_GREETING;
		return Response.ok("Hello, " + message + "!", MediaType.APPLICATION_JSON).build();
	}
	
}
