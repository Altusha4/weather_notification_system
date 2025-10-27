package weather.strategy;

import weather.core.WeatherData;
import java.time.Instant;
import java.util.Scanner;

public class ManualInputStrategy implements UpdateStrategy {
    Scanner scanner = new Scanner(System.in);

    @Override
    public WeatherData fetchData() {
        System.out.println("Enter city: ");
        String city = scanner.nextLine();

        System.out.println("Enter temperature (°C): ");
        double temp = scanner.nextDouble();

        System.out.println("Enter humidity (%): ");
        double hum = scanner.nextDouble();

        System.out.println("Enter pressure (hPa): ");
        double press = scanner.nextDouble();

        System.out.println("Enter wind speed (m/s): ");
        double wind = scanner.nextDouble();
        scanner.nextLine();

        return new WeatherData(city, temp, hum, press, wind, Instant.now());
    }
}
