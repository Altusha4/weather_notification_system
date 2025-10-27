package weather.observers;

import weather.core.Observer;
import weather.core.WeatherData;

public class StatisticsDisplay implements Observer {
    private int count = 0;
    private double sumTemp = 0.0;
    private double minTemp = Double.POSITIVE_INFINITY;
    private double maxTemp = Double.NEGATIVE_INFINITY;

    @Override
    public void update(WeatherData data) {
        double t = data.getTemperature();
        count++;
        sumTemp += t;
        if (t < minTemp) minTemp = t;
        if (t > maxTemp) maxTemp = t;

        double avg = sumTemp / count;

        System.out.println("[Stats] " + data.getCity()
                + " | now=" + t + "°C"
                + " | avg=" + round(avg) + "°C"
                + " | min=" + round(minTemp) + "°C"
                + " | max=" + round(maxTemp) + "°C");
    }

    private static double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
