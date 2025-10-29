package weather.observers;
import weather.core.Observer;
import weather.core.WeatherData;

public class StatisticsDisplay implements Observer {
    private int count = 0;
    private double sumTemp = 0.0, sumHumidity = 0.0, sumPressure = 0.0;
    private double minTemp = Double.POSITIVE_INFINITY;
    private double maxTemp = Double.NEGATIVE_INFINITY;
    private String city;

    @Override
    public void update(WeatherData data) {
        if (city == null || !city.equals(data.getCity())) {
            resetStats(data.getCity());
        }
        double t = data.getTemperature();
        count++;
        sumTemp += t;
        sumHumidity += data.getHumidity();
        sumPressure += data.getPressure();
        minTemp = Math.min(minTemp, t);
        maxTemp = Math.max(maxTemp, t);

        System.out.printf("[Stats] %s | Records: %d | Temp: %.1f°C (min: %.1f, max: %.1f, avg: %.1f)%n",
                data.getCity(), count, t, minTemp, maxTemp, sumTemp / count);
    }
    private void resetStats(String newCity) {
        this.city = newCity;
        count = 0;
        sumTemp = sumHumidity = sumPressure = 0.0;
        minTemp = Double.POSITIVE_INFINITY;
        maxTemp = Double.NEGATIVE_INFINITY;
        System.out.println("[Stats] 📍 Statistics reset for city: " + newCity);
    }
}