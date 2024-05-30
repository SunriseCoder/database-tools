package app.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class JSONUtils {

    public static void saveToDisk(Object object, String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(new File(filename), object);
    }

    public static <T> T loadFromInputStream(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        T object = mapper.reader().readValue(inputStream);
        return object;
    }
}
