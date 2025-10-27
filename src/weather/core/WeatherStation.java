package weather.core;
import weather.strategy.UpdateStrategy;
import java.util.ArrayList;
import java.util.List;

public class WeatherStation implements Subject {
    String name;
    UpdateStrategy strategy;
    List<Observer> observers = new ArrayList<>();
    WeatherData lastData;

    public WeatherStation(String name, UpdateStrategy strategy) {
        this.name = name;
        this.strategy = strategy;
    }
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
    @Override
    public void notifyObservers(WeatherData data) {
        for (Observer observer : observers) {
            observer.update(data);
        }
    }

    public void setStrategy(UpdateStrategy strategy) {
        this.strategy = strategy;
        System.out.println("Strategy set");
    }
    public void updateWeather() {
        if (strategy == null) {
            System.out.println("No update strategy set!");
        }
        WeatherData newData = strategy.fetchData();
        lastData = newData;
        notifyObservers(newData);
    }
    public WeatherData getLastData() {
        return lastData;
    }
    public String getName() {
        return name;
    }
}
