package no.nixx.photoblog.server;

import no.nixx.photoblog.error.ErrorHandler;
import no.nixx.photoblog.servlet.PhotosServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class PhotoBlogServer {

    // See https://github.com/jesperfj/jetty-secured-sample/blob/master/src/main/java/HelloWorld.java
    // for HTTP basic authentication sample.

    public static final String IMAGE_UPLOAD_SERVLET_PATH = "/photos";

    public static void main(String[] args) throws Exception {
        final SocketConnector socketConnector = new SocketConnector();
        socketConnector.setHost("localhost");
        socketConnector.setPort(8080);

        final WebAppContext webAppContext = new WebAppContext("src/main/webapp", "/");
        webAppContext.addServlet(PhotosServlet.class, IMAGE_UPLOAD_SERVLET_PATH);
        webAppContext.setErrorHandler(new ErrorHandler());

        final Server server = new Server();
        server.addConnector(socketConnector);
        server.setHandler(webAppContext);
        server.start();
    }
}