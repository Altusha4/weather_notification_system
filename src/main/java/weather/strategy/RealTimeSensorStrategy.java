package weather.strategy;
import weather.core.WeatherData;
import java.time.Instant;
import java.util.Random;

public class RealTimeSensorStrategy implements UpdateStrategy {
    private final Random random = new Random();
    private final String city;
    public RealTimeSensorStrategy(String city) {
        this.city = city;
    }
    @Override
    public WeatherData fetchData() {
        double temp = 15 + random.nextDouble() * 20;   // 15–35°C
        double hum = 40 + random.nextDouble() * 50;    // 40–90%
        double press = 990 + random.nextDouble() * 20; // 990–1010 hPa
        double wind = random.nextDouble() * 10;        // 0–10 m/s

        System.out.println("[Sensor] Reading real-time weather for " + city);
        return new WeatherData(city, temp, hum, press, wind, Instant.now());
    }
}