package app.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

    public static long findLastVersionNumber(Path baseFolder, String filePrefix, String fileSuffix) throws IOException {
        Pattern buildFolderPattern = Pattern.compile("^" + filePrefix + "([0-9]+)" + fileSuffix + "$");
        long lastBuildVersion = -1;

        try (DirectoryStream<Path> baseFolderStream = Files.newDirectoryStream(baseFolder);) {
            for (Path filePath : baseFolderStream) {
                Matcher matcher = buildFolderPattern.matcher(filePath.getFileName().toString());
                if (matcher.matches()) {
                    long versionFromFolderName = Long.parseLong(matcher.group(1));
                    if (versionFromFolderName > lastBuildVersion) {
                        lastBuildVersion = versionFromFolderName;
                    }
                }
            }
        }

        return lastBuildVersion;
    }
}
