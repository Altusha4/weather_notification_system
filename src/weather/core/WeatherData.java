package weather.core;
import java.time.Instant;

public class WeatherData {
    String city;
    double temperature;
    double humidity;
    double pressure;
    double windSpeed;
    Instant timestamp;

    public  WeatherData(String city, double temperature, double humidity,
                        double pressure, double windSpeed, Instant timestamp) {
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.timestamp = timestamp;
    }

    public String getCity() {
        return city;
    }
    public double getTemperature() {
        return temperature;
    }
    public double getHumidity() {
        return humidity;
    }
    public double getPressure() {
        return pressure;
    }
    public double getWindSpeed() {
        return windSpeed;
    }
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "City: " + city +
                ", Temperature: " + temperature + "°C" +
                ", Humidity: " + humidity + "%" +
                ", Pressure: " + pressure + " hPa" +
                ", Wind Speed: " + windSpeed + " m/s" +
                ", Time: " + timestamp;
    }
}
