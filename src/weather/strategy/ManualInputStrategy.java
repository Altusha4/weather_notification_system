package weather.strategy;

import weather.core.WeatherData;
import java.time.Instant;
import java.util.Scanner;

public class ManualInputStrategy implements UpdateStrategy {
    Scanner scanner = new Scanner(System.in);

    @Override
    public WeatherData fetchData() {
        System.out.print("Enter city: ");
        String city = scanner.nextLine().trim();
        if (city.isEmpty()) city = "Unknown";

        double temp = readValidDouble("Enter temperature (°C): ", -50, 60);
        double hum = readValidDouble("Enter humidity (%): ", 0, 100);
        double press = readValidDouble("Enter pressure (hPa): ", 800, 1100);
        double wind = readValidDouble("Enter wind speed (m/s): ", 0, 150);

        return new WeatherData(city, temp, hum, press, wind, Instant.now());
    }

    private double readValidDouble(String prompt, double min, double max) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = scanner.nextDouble();
                scanner.nextLine();
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("❌ Please enter value between %.0f and %.0f\n", min, max);
            } catch (Exception e) {
                System.out.println("❌ Please enter a valid number");
                scanner.nextLine();
            }
        }
    }
}