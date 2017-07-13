package jersey.rest.application;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import jersey.rest.controller.Controller;

@ApplicationPath("/")
public class RestApp extends Application{

	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<>();
		classes.add(Controller.class);
		return classes;
	}
	
}
