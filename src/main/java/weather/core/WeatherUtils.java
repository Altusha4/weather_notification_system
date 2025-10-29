package weather.core;

import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class WeatherUtils {
    public static String formatTemp(double t) {
        return String.format("%.1f°C", t);
    }
    public static String formatTime(java.time.Instant instant) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }
    public static String formatHumidity(double h) {
        return String.format("%.0f%%", h);
    }
    public static String formatPressure(double p) {
        return String.format("%.0f hPa", p);
    }
    public static String formatWind(double w) {
        return String.format("%.1f m/s", w);
    }
}