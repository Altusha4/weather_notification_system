package weather.app;

import weather.core.*;
import weather.observers.*;
import weather.strategy.*;

public class MainApp {
    public static void main(String[] args) {
        WelcomeScreen.show();

        UpdateStrategy initialStrategy = new ManualInputStrategy();
        WeatherStation station = new WeatherStation("Astana Weather Station", initialStrategy);

        station.addObserver(new PhoneDisplay("Altynay"));
        station.addObserver(new WebDisplay("BlueWave Weather Portal"));
        station.addObserver(new LoggerDisplay());
        station.addObserver(new AlertDisplay(-5, 30));
        station.addObserver(new StatisticsDisplay());
        HistoryCollector history = new HistoryCollector();
        station.addObserver(history);

        System.out.println("=== Manual Input Strategy ===");
        station.updateWeather();

        System.out.println("\n=== Switching to RealTimeSensorStrategy ===");
        station.setStrategy(new RealTimeSensorStrategy("Astana"));
        station.updateWeather();

        System.out.println("\n=== Switching to ScheduledBatchStrategy ===");
        station.setStrategy(new ScheduledBatchStrategy("Astana", 3));
        station.updateWeather();

        System.out.println("\n=== DEMO COMPLETE ===");
        System.out.println("Starting interactive menu...\n");

        ConsoleMenu menu = new ConsoleMenu(station);
        menu.start();

        System.out.println("\n=== FINALIZING ===");
        history.printSummary();
        ReportGenerator.export(history, "weather_report.txt");

        ShutdownManager shutdown = new ShutdownManager(((ConsoleMenu) menu).getAutoService());
        shutdown.shutdown();

        System.out.println("\n=== PROGRAM CLOSED ===");
    }
}