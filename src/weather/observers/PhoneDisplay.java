package weather.observers;

import static weather.core.WeatherUtils.*;
import weather.core.*;

public class PhoneDisplay implements Observer {
    String owner;

    public PhoneDisplay(String owner) {
        this.owner = owner;
    }
    @Override
    public void update(WeatherData data) {
        System.out.println("[Phone:" + owner + "] "
                + data.getCity() + " | "
                + formatTemp(data.getTemperature()) + ", "
                + formatHumidity(data.getHumidity()) + ", "
                + formatPressure(data.getPressure()) + ", "
                + formatWind(data.getWindSpeed()) + " | "
                + formatTime(data.getTimestamp()));
    }
}
