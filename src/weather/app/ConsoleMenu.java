package weather.app;

import weather.core.WeatherStation;
import weather.strategy.*;

import java.util.Scanner;

public class ConsoleMenu {
    private final WeatherStation station;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(WeatherStation station) {
        this.station = station;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== WEATHER MENU ===");
            System.out.println("1. Update weather");
            System.out.println("2. Switch to Manual Input");
            System.out.println("3. Switch to Real-Time Sensor");
            System.out.println("4. Switch to Scheduled Batch");
            System.out.println("5. Exit");
            System.out.print("Choose: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> station.updateWeather();
                case 2 -> {
                    station.setStrategy(new ManualInputStrategy());
                    System.out.println("Strategy changed to ManualInputStrategy");
                }
                case 3 -> {
                    System.out.print("Enter city: ");
                    String city = scanner.nextLine();
                    station.setStrategy(new RealTimeSensorStrategy(city));
                    System.out.println("Strategy changed to RealTimeSensorStrategy");
                }
                case 4 -> {
                    System.out.print("Enter city: ");
                    String city = scanner.nextLine();
                    station.setStrategy(new ScheduledBatchStrategy(city, 3));
                    System.out.println("Strategy changed to ScheduledBatchStrategy");
                }
                case 5 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }
}
