package weather.observers;
import weather.core.Observer;
import weather.core.WeatherData;

public class LoggerDisplay implements Observer {
    @Override
    public void update(WeatherData data) {
        System.out.println("[Logger] Weather record saved: " + data);
    }
}