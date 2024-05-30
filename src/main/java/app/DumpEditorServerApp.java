package app;

import app.handlers.RestRequestHandler;
import app.handlers.StaticResourceHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class DumpEditorServerApp {

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        try {
            int port = args.length > 0 ? Integer.parseInt(args[0]) : 3003;

            System.out.println("Starting server on port: " + port);
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            server.createContext("/rest/", new RestRequestHandler());
            server.createContext("/", new StaticResourceHandler());

            server.start();
            System.out.println("Server started");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
