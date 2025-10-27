package weather.app;

import weather.core.*;
import weather.strategy.*;
import weather.observers.*;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("=== WEATHER NOTIFICATION SYSTEM ===\n");
        UpdateStrategy manual = new ManualInputStrategy();

        WeatherStation station = new WeatherStation("Astana Weather Station", manual);

        station.addObserver(new PhoneDisplay("Altynay"));
        station.addObserver(new LoggerDisplay());

        System.out.println("=== Manual Input Strategy ===");
        station.updateWeather();

        System.out.println("\n=== Switching to RealTimeSensorStrategy ===");
        station.setStrategy(new RealTimeSensorStrategy("Astana"));
        station.updateWeather();

        System.out.println("\n=== Switching to ScheduledBatchStrategy ===");
        station.setStrategy(new ScheduledBatchStrategy("Astana", 3));
        station.updateWeather();

        System.out.println("\n=== END OF DEMO ===");
    }
}
