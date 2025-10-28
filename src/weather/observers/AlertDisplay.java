package weather.observers;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import weather.core.Observer;
import weather.core.WeatherData;

public class AlertDisplay implements Observer {
    private final Map<String, List<Double>> cityTemperatures = new HashMap<>();

    @Override
    public void update(WeatherData data) {
        String city = data.getCity();
        double temp = data.getTemperature();

        cityTemperatures.computeIfAbsent(city, k -> new ArrayList<>()).add(temp);
        List<Double> temps = cityTemperatures.get(city);

        if (temps.size() >= 3) {
            double avg = temps.stream().mapToDouble(Double::doubleValue).average().orElse(temp);
            double deviation = Math.abs(temp - avg);

            if (deviation > 10) {
                System.out.printf("🚨 [EXTREME] %s: %.1f°C (avg: %.1f°C, deviation: %.1f°C)%n",
                        city, temp, avg, deviation);
            } else if (deviation > 5) {
                System.out.printf("⚠️  [ALERT] %s: %.1f°C (avg: %.1f°C)%n", city, temp, avg);
            } else {
                System.out.printf("✅ [NORMAL] %s: %.1f°C%n", city, temp);
            }
        } else {
            System.out.printf("[ALERT] %s: %.1f°C (collecting data...)%n", city, temp);
        }
    }
}