package weather.core;
import weather.strategy.UpdateStrategy;
import java.util.ArrayList;
import java.util.List;

public class WeatherStation implements Subject {
    private String name;
    private UpdateStrategy strategy;
    private List<Observer> observers = new ArrayList<>();
    private WeatherData lastData;

    private static WeatherStation instance;

    private WeatherStation() {
        System.out.println("[WeatherStation] Singleton instance created");
    }
    public static synchronized WeatherStation getInstance() {
        if (instance == null) {
            instance = new WeatherStation();
        }
        return instance;
    }
    public void initialize(String name, UpdateStrategy strategy) {
        this.name = name;
        this.strategy = strategy;
        System.out.println("[WeatherStation] Initialized: " + name + " with " +
                strategy.getClass().getSimpleName());
    }
    public void reinitialize(String name, UpdateStrategy strategy) {
        this.name = name;
        setStrategy(strategy); // используем существующий метод
        System.out.println("[WeatherStation] Reinitialized: " + name);
    }
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
        System.out.println("[WeatherStation] Observer added: " +
                observer.getClass().getSimpleName());
    }
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
        System.out.println("[WeatherStation] Observer removed: " +
                observer.getClass().getSimpleName());
    }
    @Override
    public void notifyObservers(WeatherData data) {
        System.out.println("[WeatherStation] Notifying " + observers.size() + " observers");
        for (Observer observer : observers) {
            observer.update(data);
        }
    }
    public void setStrategy(UpdateStrategy strategy) {
        this.strategy = strategy;
        System.out.println("[WeatherStation] Strategy set to: " +
                strategy.getClass().getSimpleName());
    }
    public void updateWeather() {
        if (strategy == null) {
            System.out.println("[WeatherStation] No update strategy set!");
            return;
        }
        System.out.println("[WeatherStation] Updating weather with " +
                strategy.getClass().getSimpleName());
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
    public String getCurrentStrategyName() {
        if (strategy == null) {
            return "None";
        }
        String className = strategy.getClass().getSimpleName();
        return className.replace("Strategy", "");
    }
    public List<Observer> getObservers() {
        return new ArrayList<>(observers);
    }
    public String getStatus() {
        return String.format(
                "WeatherStation{name='%s', strategy=%s, observers=%d, hasData=%s}",
                name,
                getCurrentStrategyName(),
                observers.size(),
                lastData != null
        );
    }
    public static void resetInstance() {
        instance = null;
        System.out.println("[WeatherStation] Instance reset");
    }
}