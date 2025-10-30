package weather.core;
import weather.strategy.UpdateStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class WeatherStation implements Subject {
    private String name;
    private UpdateStrategy strategy;
    private final List<Observer> observers = new CopyOnWriteArrayList<>();
    private WeatherData lastData;

    private static volatile WeatherStation instance;

    private WeatherStation() {
        System.out.println("[WeatherStation] Singleton instance created");
    }
    public static WeatherStation getInstance() {
        if (instance == null) {
            synchronized (WeatherStation.class) {
                if (instance == null) {
                    instance = new WeatherStation();
                }
            }
        }
        return instance;
    }
    public void initialize(String name, UpdateStrategy strategy) {
        this.name = Objects.requireNonNull(name, "Station name cannot be null");
        this.strategy = Objects.requireNonNull(strategy, "Strategy cannot be null");
        System.out.println("[WeatherStation] Initialized: " + name + " with " +
                strategy.getClass().getSimpleName());
    }
    public void reinitialize(String name, UpdateStrategy strategy) {
        this.name = Objects.requireNonNull(name, "Station name cannot be null");
        setStrategy(strategy);
        System.out.println("[WeatherStation] Reinitialized: " + name);
    }
    @Override
    public void addObserver(Observer observer) {
        Objects.requireNonNull(observer, "Observer cannot be null");
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("[WeatherStation] Observer added: " +
                    observer.getClass().getSimpleName());
        }
    }
    @Override
    public void removeObserver(Observer observer) {
        Objects.requireNonNull(observer, "Observer cannot be null");
        if (observers.remove(observer)) {
            System.out.println("[WeatherStation] Observer removed: " +
                    observer.getClass().getSimpleName());
        }
    }
    @Override
    public void notifyObservers(WeatherData data) {
        Objects.requireNonNull(data, "Weather data cannot be null");

        if (observers.isEmpty()) {
            System.out.println("[WeatherStation] No observers to notify");
            return;
        }

        System.out.println("[WeatherStation] Notifying " + observers.size() + " observers");
        for (Observer observer : observers) {
            try {
                observer.update(data);
            } catch (Exception e) {
                System.err.println("[WeatherStation] Error notifying observer " +
                        observer.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
    public void setStrategy(UpdateStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "Strategy cannot be null");
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
        try {
            WeatherData newData = strategy.fetchData();
            lastData = newData;
            notifyObservers(newData);
        } catch (Exception e) {
            System.err.println("[WeatherStation] Error fetching weather data: " + e.getMessage());
        }
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
    public int getObserverCount() {
        return observers.size();
    }
    public boolean hasObservers() {
        return !observers.isEmpty();
    }
    public boolean hasData() {
        return lastData != null;
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
    public void clearObservers() {
        int count = observers.size();
        observers.clear();
        System.out.println("[WeatherStation] Cleared " + count + " observers");
    }
    public void removeAllObservers() {
        clearObservers();
    }
    public static void resetInstance() {
        synchronized (WeatherStation.class) {
            instance = null;
            System.out.println("[WeatherStation] Singleton instance reset");
        }
    }
    public static boolean isInitialized() {
        return instance != null;
    }
    public UpdateStrategy getCurrentStrategy() {
        return this.strategy;
    }
}