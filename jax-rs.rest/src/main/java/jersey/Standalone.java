package jersey;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import jersey.rest.config.ResourceConfig;

@SuppressWarnings("restriction")
public class Standalone {

	protected static Properties standalone = new Properties();
	protected static InputStream is = null;

	private static final String DEFAULT_URI = "http://localhost/";
	
	static HttpServer start() throws IOException{
		final HttpServer server = HttpServer.create(new InetSocketAddress(getBaseURI().getPort()), 0);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				server.stop(0);
			}
		}));
		
		HttpHandler handler = RuntimeDelegate.getInstance().createEndpoint(new ResourceConfig(), HttpHandler.class);
		server.createContext(getBaseURI().getPath(), handler);
		server.start();
		return server;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		try {
			is = ClassLoader.getSystemResourceAsStream("standalone.properties");
			standalone.load(is);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("\"Standalone\" Jersey Application");
		start();
		System.out.println(
				"Application started.\n" +
				"Deployed to: " + getBaseURI() + "\n" +
				"Hit ENTER to stop..."
		);
	}
	
	private static int getPort(int defaultPort) {
        final String port = standalone.getProperty("jersey.config.container.port");
        if(null != port) {
            try {
                return Integer.parseInt(port);
            } catch (NumberFormatException e) {
                System.out.println("Value of jersey.config.container.port property"
                        + " is not a valid positive integer [" + port + "]."
                        + " Reverting to default [" + defaultPort + "].");
            }
        }
        return defaultPort;
    }
	
	public static URI getBaseURI() {
		String URI = standalone.getProperty("jersey.config.uri");
		if(null != URI) {
			try {
				return UriBuilder.fromUri(URI).port(getPort(8080)).build();
			} catch (Exception e) {
				System.out.println("Value of jersey.config.uri property"
                        + " is not a valid URI [" + URI + "]."
                        + " Reverting to default [" + DEFAULT_URI + "].");
			}
		}
        return UriBuilder.fromUri(DEFAULT_URI).port(getPort(8080)).build();
    }
	
}
