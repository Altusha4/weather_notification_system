package weather.observers;

import weather.core.Observer;
import weather.core.WeatherData;

public class AlertDisplay implements Observer {
    private final double minTemp;
    private final double maxTemp;

    public AlertDisplay(double minTemp, double maxTemp) {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    @Override
    public void update(WeatherData data) {
        double temp = data.getTemperature();
        if (temp < minTemp) {
            System.out.println("[ALERT] Temperature too low in " + data.getCity() + ": " + temp + "°C");
        } else if (temp > maxTemp) {
            System.out.println("[ALERT] Temperature too high in " + data.getCity() + ": " + temp + "°C");
        } else {
            System.out.println("[ALERT] Temperature normal in " + data.getCity() + ": " + temp + "°C");
        }
    }
}
