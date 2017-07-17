package jersey.rest.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import jersey.rest.resources.Greeter;

@ApplicationPath("/")
public class ResourceConfig extends Application{

	private final Set<Class<?>> classes;
	
	public ResourceConfig() {
		HashSet<Class<?>> c = new HashSet<Class<?>>();
		c.add(Greeter.class);
		classes = Collections.unmodifiableSet(c);
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}
}
