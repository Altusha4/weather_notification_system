package weather.core;

public interface Observer {
    void update(WeatherData data);
}