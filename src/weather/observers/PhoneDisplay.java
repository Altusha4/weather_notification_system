package weather.observers;

import weather.core.Observer;
import weather.core.WeatherData;

public class PhoneDisplay implements Observer {
    String owner;

    public PhoneDisplay(String owner) {
        this.owner = owner;
    }
    @Override
    public void update(WeatherData data) {
        System.out.println("[Phone:" + owner + "] "
                + data.getCity() + " | "
                + data.getTemperature() + "°C, "
                + data.getHumidity() + "%, "
                + data.getPressure() + " hPa, "
                + data.getWindSpeed() + " m/s | "
                + data.getTimestamp());
    }
}
