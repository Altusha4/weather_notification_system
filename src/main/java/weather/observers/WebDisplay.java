package weather.observers;
import weather.core.Observer;
import weather.core.WeatherData;
import static weather.core.WeatherUtils.*;

public class WebDisplay implements Observer {
    private final String siteName;
    public WebDisplay(String siteName) {
        this.siteName = siteName;
    }
    @Override
    public void update(WeatherData data) {
        System.out.println("\n[WebDisplay: " + siteName + "]");
        System.out.println("----------------------------");
        System.out.println(" City: " + data.getCity());
        System.out.println(" Temperature: " + formatTemp(data.getTemperature()));
        System.out.println(" Humidity: " + formatHumidity(data.getHumidity()));
        System.out.println(" Pressure: " + formatPressure(data.getPressure()));
        System.out.println(" Wind Speed: " + formatWind(data.getWindSpeed()));
        System.out.println(" Time: " + formatTime(data.getTimestamp()));
        System.out.println("----------------------------\n");
    }
}