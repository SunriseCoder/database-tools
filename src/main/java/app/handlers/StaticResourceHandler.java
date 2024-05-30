package app.handlers;

import app.util.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class StaticResourceHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        if (path.equals("/")) {
            sendResource("/index.html", httpExchange);
        } else {
            sendResource(path, httpExchange);
        }
    }

    private void sendResource(String path, HttpExchange httpExchange) throws IOException {
        System.out.println("Requested static resource: " + path);
        String resourcePath = "static" + path;
        HttpUtils.sendResource(resourcePath, httpExchange);
    }
}
