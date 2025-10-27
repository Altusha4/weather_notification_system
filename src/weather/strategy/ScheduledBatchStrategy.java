package weather.strategy;

import weather.core.WeatherData;
import java.time.Instant;
import java.util.Random;

public class ScheduledBatchStrategy implements UpdateStrategy {
    private final String city;
    private final int batchNumber;
    private final Random random = new Random();
    private int counter = 0;

    public ScheduledBatchStrategy(String city, int batchNumber) {
        this.city = city;
        this.batchNumber = batchNumber;
    }

    @Override
    public WeatherData fetchData() {
        counter++;
        double temp = 10 + random.nextDouble() * 25;
        double hum = 30 + random.nextDouble() * 60;
        double press = 980 + random.nextDouble() * 30;
        double wind = random.nextDouble() * 8;

        System.out.println("[Batch] Update #" + counter + " for " + city +
                " (batch size: " + batchNumber + ")");
        return new WeatherData(city, temp, hum, press, wind, Instant.now());
    }
}
