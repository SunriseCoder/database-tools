package app.handlers;

import app.dto.DbDumpDTO;
import app.util.FileUtils;
import app.util.HttpUtils;
import app.util.JSONUtils;
import app.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class RestRequestHandler implements HttpHandler {
    private static final int FILENAME_VERSION_LENGTH = 6;
    public static final String DATA_FOLDER_PATH = "data";

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            Map<String, List<String>> params = HttpUtils.parseQueryArgs(httpExchange.getRequestURI().getRawQuery());
            System.out.println("Rest request: " + path);

            if (path.equals("/rest/load")) {
                String dumpName = params.get("dump-name").get(0);
                loadData(dumpName, httpExchange);
            } else if (path.equals("/rest/save")) {
                saveData(httpExchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtils.sendResponseStatus(500, httpExchange);
        }
    }

    private void loadData(String dumpName, HttpExchange httpExchange) throws IOException {
        Path dataPath = Paths.get(DATA_FOLDER_PATH);
        if (Files.notExists(dataPath)) {
            Files.createDirectories(dataPath);
        }

        long lastVersionNumber = FileUtils.findLastVersionNumber(dataPath, dumpName + "-v", ".json");
        if (lastVersionNumber == -1) {
            HttpUtils.sendResponseStatus(404, httpExchange);
            return;
        }

        String fileName = generateFilename(dumpName, lastVersionNumber);
        String resourcePath = dataPath.resolve(fileName).toString();
        HttpUtils.sendFile(resourcePath, httpExchange);
    }

    private void saveData(HttpExchange httpExchange) throws IOException {
        DbDumpDTO dbDump = JSONUtils.loadFromInputStream(httpExchange.getRequestBody());

        String dumpName = dbDump.getName();
        Path dataPath = Paths.get(DATA_FOLDER_PATH);
        long lastVersionNumber = FileUtils.findLastVersionNumber(dataPath, dumpName + "-v", ".json");

        lastVersionNumber++;
        String fileName = generateFilename(dumpName, lastVersionNumber);
        String resourcePath = dataPath.resolve(fileName).toString();
        JSONUtils.saveToDisk(dbDump, resourcePath);

        HttpUtils.sendResponseStatus(200, httpExchange);
    }

    private String generateFilename(String dumpName, long lastVersionNumber) {
        return dumpName + "-v"
                + StringUtils.fillNumberWithBeginningZeros(lastVersionNumber, FILENAME_VERSION_LENGTH) + ".json";
    }
}
