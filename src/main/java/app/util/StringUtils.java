package app.util;

public class StringUtils {

    public static String fillNumberWithBeginningZeros(long value, int targetLength) {
        StringBuilder sb = new StringBuilder();
        sb.append(value);
        while (sb.length() < targetLength) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }
}
