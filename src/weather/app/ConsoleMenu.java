package weather.app;

import weather.core.WeatherStation;
import weather.core.AutoUpdateService;
import weather.strategy.*;

import java.util.Scanner;

public class ConsoleMenu {
    private final WeatherStation station;
    private final AutoUpdateService autoService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(WeatherStation station) {
        this.station = station;
        this.autoService = new AutoUpdateService(station);
    }
    public AutoUpdateService getAutoService() {
        return autoService;
    }

    public void start() {
        while (true) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("        WEATHER STATION MENU");
            System.out.println("=".repeat(40));
            System.out.println("1. 📡 Update weather once");
            System.out.println("2. ⚙️  Switch to Manual Input");
            System.out.println("3. 📡 Switch to Real-Time Sensor");
            System.out.println("4. 📦 Switch to Scheduled Batch");
            System.out.println("5. 🔄 Auto-update settings");
            System.out.println("6. 📊 Export report and exit");
            System.out.print("Choose: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> {
                        System.out.println("\n--- Manual Update ---");
                        station.updateWeather();
                    }
                    case 2 -> {
                        station.setStrategy(new ManualInputStrategy());
                        System.out.println("✅ Strategy changed to Manual Input");
                    }
                    case 3 -> {
                        System.out.print("Enter city for sensor: ");
                        String city = scanner.nextLine();
                        station.setStrategy(new RealTimeSensorStrategy(city));
                        System.out.println("✅ Strategy changed to Real-Time Sensor");
                    }
                    case 4 -> {
                        System.out.print("Enter city for batch: ");
                        String city = scanner.nextLine();
                        station.setStrategy(new ScheduledBatchStrategy(city, 3));
                        System.out.println("✅ Strategy changed to Scheduled Batch");
                    }
                    case 5 -> {
                        autoUpdateMenu();
                    }
                    case 6 -> {
                        System.out.println("Exporting report and exiting...");
                        return;
                    }
                    default -> System.out.println("❌ Invalid option. Try again.");
                }

                if (choice != 5) {
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                }

            } catch (Exception e) {
                System.out.println("❌ Input error! Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    private void autoUpdateMenu() {
        while (true) {
            System.out.println("\n--- Auto-Update Settings ---");
            System.out.println("Auto-update: " + (autoService.isRunning() ? "🟢 RUNNING" : "🔴 STOPPED"));
            System.out.println("1. " + (autoService.isRunning() ? "⏹️  Stop" : "▶️  Start") + " auto-update");
            System.out.println("2. ⚙️  Change interval");
            System.out.println("3. ↩️  Back to main menu");
            System.out.print("Choose: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> {
                        if (autoService.isRunning()) {
                            autoService.stop();
                            System.out.println("✅ Auto-update stopped");
                        } else {
                            System.out.print("Enter interval in seconds: ");
                            int interval = scanner.nextInt();
                            scanner.nextLine();
                            autoService.start(interval);
                            System.out.println("✅ Auto-update started every " + interval + " seconds");
                        }
                    }
                    case 2 -> {
                        if (autoService.isRunning()) {
                            System.out.print("Enter new interval in seconds: ");
                            int interval = scanner.nextInt();
                            scanner.nextLine();
                            autoService.stop();
                            autoService.start(interval);
                            System.out.println("✅ Interval changed to " + interval + " seconds");
                        } else {
                            System.out.println("❌ Auto-update is not running");
                        }
                    }
                    case 3 -> {
                        return;
                    }
                    default -> System.out.println("❌ Invalid option");
                }
            } catch (Exception e) {
                System.out.println("❌ Input error!");
                scanner.nextLine();
            }
        }
    }
}