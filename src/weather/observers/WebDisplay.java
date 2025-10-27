package weather.observers;

import weather.core.Observer;
import weather.core.WeatherData;

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
        System.out.println(" Temperature: " + data.getTemperature() + "°C");
        System.out.println(" Humidity: " + data.getHumidity() + "%");
        System.out.println(" Pressure: " + data.getPressure() + " hPa");
        System.out.println(" Wind Speed: " + data.getWindSpeed() + " m/s");
        System.out.println(" Time: " + data.getTimestamp());
        System.out.println("----------------------------\n");
    }
}
