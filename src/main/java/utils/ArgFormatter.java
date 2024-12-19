package utils;

// könnte man weiter ausbauen

import java.util.Arrays;
import java.util.List;

public class ArgFormatter {
    private static final String PREFIX = "-";
    public static void removePrefix(String in) {

    };
    public static void done() {};
    public static void merge() {};
    public static void split() {};

    public static List<String> extractAll(String marker, boolean singleArg, String content) {
        String extractedString = extractString(marker, singleArg, content);

        if (extractedString == null) return null;

        return Arrays.stream(extractedString.split(",")).map(String::trim).toList();

    }

    public static String extractString(String marker, boolean singleArg, String content) {
        StringBuilder sb = new StringBuilder(content);
        int markerIndex = sb.indexOf(marker);

        if (markerIndex < 0) return null;

        sb.delete(0, markerIndex+marker.length());

        int endMarkerIndex = sb.indexOf("-");

        if (endMarkerIndex >= 0 && !singleArg) {
            sb.delete(endMarkerIndex, sb.length());
        }

        return sb.toString();
    }
}
