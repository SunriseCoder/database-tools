package app.util;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class HttpUtils {

    public static void sendResource(String resourcePath, HttpExchange httpExchange) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        String path = classloader.getResource(resourcePath).getFile();
        sendFile(path, httpExchange);
    }

    public static void sendFile(String path, HttpExchange httpExchange) throws IOException {
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            HttpUtils.sendResponseStatus(404, httpExchange);
            return;
        }

        long contentLength = file.length();
        httpExchange.sendResponseHeaders(200, contentLength);

        try (InputStream is = new BufferedInputStream(new FileInputStream(file));
             OutputStream os = httpExchange.getResponseBody();) {

            byte[] buffer = new byte[4096];
            long copiedLength = 0;
            while (copiedLength < contentLength) {
                int read = is.read(buffer);
                os.write(buffer, 0, read);
                copiedLength += read;
            }
        }

        httpExchange.close();
    }

    public static void sendResponseStatus(int statusCode, HttpExchange httpExchange) throws IOException {
        System.out.println("Sending Response Status: " + statusCode);
        httpExchange.sendResponseHeaders(statusCode, 0);
        httpExchange.close();
    }

    public static Map<String, List<String>> parseQueryArgs(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query)
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));
    }

    private static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is a required encoding", e);
        }
    }
}
