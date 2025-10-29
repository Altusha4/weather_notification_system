package weather.strategy;
import weather.core.WeatherData;

public interface UpdateStrategy {
    WeatherData fetchData();
}