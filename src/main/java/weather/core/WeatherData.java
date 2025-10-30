package weather.core;
import java.time.Instant;
import java.util.Objects;

public class WeatherData {
    private final String city;
    private final double temperature;
    private final double humidity;
    private final double pressure;
    private final double windSpeed;
    private final Instant timestamp;

    public WeatherData(String city, double temperature, double humidity,
                       double pressure, double windSpeed, Instant timestamp) {
        this.city = validateCity(city);
        this.temperature = validateTemperature(temperature);
        this.humidity = validateHumidity(humidity);
        this.pressure = validatePressure(pressure);
        this.windSpeed = validateWindSpeed(windSpeed);
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }

    private String validateCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        return city.trim();
    }
    private double validateTemperature(double temp) {
        if (temp < -50 || temp > 60) {
            throw new IllegalArgumentException("Temperature must be between -50 and 60°C, got: " + temp);
        }
        return temp;
    }
    private double validateHumidity(double humidity) {
        if (humidity < 0 || humidity > 100) {
            throw new IllegalArgumentException("Humidity must be between 0 and 100%, got: " + humidity);
        }
        return humidity;
    }
    private double validatePressure(double pressure) {
        if (pressure < 800 || pressure > 1100) {
            throw new IllegalArgumentException("Pressure must be between 800 and 1100 hPa, got: " + pressure);
        }
        return pressure;
    }
    private double validateWindSpeed(double windSpeed) {
        if (windSpeed < 0 || windSpeed > 150) {
            throw new IllegalArgumentException("Wind speed must be between 0 and 150 m/s, got: " + windSpeed);
        }
        return windSpeed;
    }
    public String getCity() { return city; }
    public double getTemperature() { return temperature; }
    public double getHumidity() { return humidity; }
    public double getPressure() { return pressure; }
    public double getWindSpeed() { return windSpeed; }
    public Instant getTimestamp() { return timestamp; }
    @Override
    public String toString() {
        return String.format(
                "WeatherData{city='%s', temperature=%.1f°C, humidity=%.1f%%, pressure=%.1f hPa, windSpeed=%.1f m/s, timestamp=%s}",
                city, temperature, humidity, pressure, windSpeed, timestamp
        );
    }
}