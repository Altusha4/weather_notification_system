package weather.strategy;
import weather.core.WeatherData;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ManualInputStrategy implements UpdateStrategy {
    private final Map<String, Double> manualData = new HashMap<>();
    private String currentCity = "Unknown";

    public void setManualData(String city, double temperature, double humidity,
                              double pressure, double windSpeed) {
        this.currentCity = city;
        manualData.clear();
        manualData.put("temperature", temperature);
        manualData.put("humidity", humidity);
        manualData.put("pressure", pressure);
        manualData.put("windSpeed", windSpeed);

        System.out.println("[ManualInput] Data set for " + city +
                ": " + temperature + "°C, " + humidity + "%, " +
                pressure + " hPa, " + windSpeed + " m/s");
    }
    @Override
    public WeatherData fetchData() {
        if (manualData.isEmpty()) {
            System.out.println("[ManualInput] No manual data provided, using defaults");
            return new WeatherData(currentCity, 20.0, 60.0, 1013.0, 3.0, Instant.now());
        }

        double temp = manualData.getOrDefault("temperature", 20.0);
        double hum = manualData.getOrDefault("humidity", 60.0);
        double press = manualData.getOrDefault("pressure", 1013.0);
        double wind = manualData.getOrDefault("windSpeed", 3.0);

        WeatherData data = new WeatherData(currentCity, temp, hum, press, wind, Instant.now());
        System.out.println("[ManualInput] Returning manual data: " + data);

        return data;
    }
    public boolean hasData() {
        return !manualData.isEmpty();
    }
    public void clearData() {
        manualData.clear();
        System.out.println("[ManualInput] Manual data cleared");
    }
    public String getCurrentCity() {
        return currentCity;
    }
    public Map<String, Double> getManualData() {
        return new HashMap<>(manualData);
    }
}