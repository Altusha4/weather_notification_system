package weather.app.controller;

import org.springframework.web.bind.annotation.*;
import weather.core.WeatherStation;
import weather.core.WeatherData;
import weather.core.Observer;
import weather.observers.*;
import weather.strategy.UpdateStrategy;
import weather.strategy.ManualInputStrategy;
import weather.factory.StrategyFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class WeatherController {

    private final WeatherStation station;
    private final List<Observer> activeObservers = new ArrayList<>();

    public WeatherController() {
        this.station = WeatherStation.getInstance();

        UpdateStrategy initialStrategy = StrategyFactory.create("manual", "Astana");
        this.station.initialize("Web Weather Station", initialStrategy);

        setupObservers();
        System.out.println("[WeatherController] Singleton WeatherStation initialized: " +
                station.hashCode());
    }
    private void setupObservers() {
        try {
            WebDisplay webDisplay = new WebDisplay("Weather Dashboard");
            station.addObserver(webDisplay);
            activeObservers.add(webDisplay);

            LoggerDisplay logger = new LoggerDisplay();
            station.addObserver(logger);
            activeObservers.add(logger);

            HistoryCollector history = new HistoryCollector();
            station.addObserver(history);
            activeObservers.add(history);

            StatisticsDisplay stats = new StatisticsDisplay();
            station.addObserver(stats);
            activeObservers.add(stats);

            AlertDisplay alerts = new AlertDisplay();
            station.addObserver(alerts);
            activeObservers.add(alerts);

            System.out.println("[WeatherController] " + activeObservers.size() + " observers initialized");

        } catch (Exception e) {
            System.err.println("[WeatherController] Error setting up observers: " + e.getMessage());
        }
    }

    @PostMapping("/strategy")
    public Map<String, Object> setStrategy(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String strategyType = request.get("type");
            String city = request.get("city");

            if (strategyType == null || strategyType.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Strategy type is required");
                return response;
            }

            if (city == null || city.trim().isEmpty()) {
                city = "Unknown City";
            }

            UpdateStrategy strategy = StrategyFactory.create(strategyType, city);
            station.setStrategy(strategy);

            response.put("status", "success");
            response.put("message", "Strategy set to: " + strategyType);
            response.put("strategyClass", strategy.getClass().getSimpleName());
            response.put("city", city);
            response.put("stationStatus", station.getStatus());

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to set strategy: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/update")
    public Map<String, Object> updateWeather() {
        Map<String, Object> response = new HashMap<>();

        try {
            station.updateWeather();
            WeatherData data = station.getLastData();

            response.put("status", "success");
            response.put("message", "Weather data updated successfully");
            response.put("data", convertToMap(data));
            response.put("strategy", station.getCurrentStrategyName());
            response.put("observersNotified", station.getObserverCount());

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to update weather: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/strategies")
    public Map<String, Object> getAvailableStrategies() {
        Map<String, Object> strategies = new HashMap<>();
        strategies.put("available", List.of("manual", "sensor", "batch"));
        strategies.put("current", station.getCurrentStrategyName());
        strategies.put("pattern", "Strategy Pattern");
        strategies.put("description", "Different algorithms for fetching weather data");
        strategies.put("usage", "POST /api/weather/strategy with {type, city}");
        return strategies;
    }

    @PostMapping("/manual/data")
    public Map<String, Object> setManualData(@RequestBody Map<String, Object> manualData) {
        Map<String, Object> response = new HashMap<>();

        try {
            String city = (String) manualData.get("city");
            double temperature = ((Number) manualData.get("temperature")).doubleValue();
            double humidity = ((Number) manualData.get("humidity")).doubleValue();
            double pressure = ((Number) manualData.get("pressure")).doubleValue();
            double windSpeed = ((Number) manualData.get("windSpeed")).doubleValue();

            if (temperature < -50 || temperature > 60) {
                throw new IllegalArgumentException("Temperature must be between -50 and 60°C");
            }
            if (humidity < 0 || humidity > 100) {
                throw new IllegalArgumentException("Humidity must be between 0 and 100%");
            }
            if (pressure < 800 || pressure > 1100) {
                throw new IllegalArgumentException("Pressure must be between 800 and 1100 hPa");
            }
            if (windSpeed < 0 || windSpeed > 150) {
                throw new IllegalArgumentException("Wind speed must be between 0 and 150 m/s");
            }

            UpdateStrategy currentStrategy = getCurrentStrategy();
            if (currentStrategy instanceof ManualInputStrategy) {
                ManualInputStrategy manualStrategy = (ManualInputStrategy) currentStrategy;
                manualStrategy.setManualData(city, temperature, humidity, pressure, windSpeed);

                response.put("status", "success");
                response.put("message", "Manual data set successfully");
                response.put("city", city);
                response.put("temperature", temperature);
                response.put("humidity", humidity);
                response.put("pressure", pressure);
                response.put("windSpeed", windSpeed);
            } else {
                response.put("status", "error");
                response.put("message", "Current strategy is not ManualInput. Please set manual strategy first.");
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to set manual data: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/factory/strategies")
    public Map<String, Object> getFactoryInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("pattern", "Factory Pattern");
        info.put("availableStrategies", List.of("manual", "sensor", "batch"));
        info.put("factoryMethod", "StrategyFactory.create(type, city)");
        info.put("description", "Centralized object creation");
        info.put("usageExample", "StrategyFactory.create(\"sensor\", \"Astana\")");
        return info;
    }

    @PostMapping("/factory/create")
    public Map<String, Object> createStrategy(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String type = request.get("type");
            String city = request.get("city");

            if (type == null) {
                response.put("status", "error");
                response.put("message", "Strategy type is required");
                return response;
            }

            UpdateStrategy strategy = StrategyFactory.create(type, city);

            response.put("pattern", "Factory Pattern");
            response.put("status", "success");
            response.put("factory", "StrategyFactory");
            response.put("createdStrategy", strategy.getClass().getSimpleName());
            response.put("type", type);
            response.put("city", city);
            response.put("description", "Strategy created via factory");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to create strategy: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/observers")
    public Map<String, Object> getObserversInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("pattern", "Observer Pattern");
        info.put("totalCount", station.getObserverCount());
        info.put("activeCount", activeObservers.size());
        info.put("types", station.getObservers().stream()
                .map(o -> o.getClass().getSimpleName())
                .collect(Collectors.toList()));
        info.put("activeObservers", activeObservers.stream()
                .map(o -> o.getClass().getSimpleName())
                .collect(Collectors.toList()));
        info.put("description", "Automatic notifications to all subscribers");
        return info;
    }

    @PostMapping("/observers/add")
    public Map<String, Object> addObserver(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String type = request.get("type");
            String name = request.get("name");

            if (type == null) {
                response.put("status", "error");
                response.put("message", "Observer type is required");
                return response;
            }

            Observer newObserver = createObserverByType(type, name);
            if (newObserver != null) {
                station.addObserver(newObserver);
                activeObservers.add(newObserver);

                response.put("status", "success");
                response.put("message", "Observer added: " + type);
                response.put("observerType", newObserver.getClass().getSimpleName());
                response.put("totalObservers", station.getObserverCount());
            } else {
                response.put("status", "error");
                response.put("message", "Unknown observer type: " + type);
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to add observer: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/singleton")
    public Map<String, Object> getSingletonInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("pattern", "Singleton Pattern");
        info.put("implementation", "WeatherStation.getInstance()");
        info.put("purpose", "Single instance of WeatherStation in the system");
        info.put("instanceHash", station.hashCode());
        info.put("stationName", station.getName());
        info.put("description", "Guarantees only one instance of weather station");
        info.put("currentInstance", station.getStatus());
        info.put("isInitialized", WeatherStation.isInitialized());
        return info;
    }

    @GetMapping("/current")
    public Map<String, Object> getCurrentWeather() {
        Map<String, Object> response = new HashMap<>();

        WeatherData data = station.getLastData();
        if (data != null) {
            response.put("status", "success");
            response.put("data", convertToMap(data));
            response.put("lastUpdate", data.getTimestamp().toString());
        } else {
            response.put("status", "success");
            response.put("data", null);
            response.put("message", "No weather data available. Please update first.");
        }

        return response;
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("stationName", station.getName());
        status.put("strategy", station.getCurrentStrategyName());
        status.put("stationStatus", station.getStatus());
        status.put("activeObservers", station.getObserverCount());
        status.put("hasData", station.hasData());
        status.put("lastUpdate", station.getLastData() != null ?
                station.getLastData().getTimestamp().toString() : "Never");
        status.put("singletonInstance", station.hashCode());

        if (station.getLastData() != null) {
            status.put("lastData", convertToMap(station.getLastData()));
        }

        status.put("patterns", List.of("Strategy", "Observer", "Factory", "Singleton"));

        return status;
    }

    @GetMapping("/patterns")
    public Map<String, Object> getAllPatterns() {
        Map<String, Object> patterns = new HashMap<>();

        patterns.put("Strategy Pattern", Map.of(
                "purpose", "Different data fetching algorithms",
                "classes", "UpdateStrategy + 3 implementations",
                "usage", "Runtime strategy switching",
                "endpoint", "POST /api/weather/strategy"
        ));

        patterns.put("Observer Pattern", Map.of(
                "purpose", "Notify subscribers of changes",
                "classes", "Subject + Observer + 6 implementations",
                "usage", "Automatic display updates",
                "endpoint", "GET /api/weather/observers"
        ));

        patterns.put("Factory Pattern", Map.of(
                "purpose", "Centralized object creation",
                "classes", "StrategyFactory",
                "usage", "StrategyFactory.create(type, city)",
                "endpoint", "GET /api/weather/factory/strategies"
        ));

        patterns.put("Singleton Pattern", Map.of(
                "purpose", "Single class instance",
                "classes", "WeatherStation.getInstance()",
                "usage", "Global access to WeatherStation",
                "endpoint", "GET /api/weather/singleton"
        ));

        return patterns;
    }

    @GetMapping("/")
    public Map<String, Object> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "Weather Notification System");
        info.put("version", "1.0");
        info.put("description", "REST API for Weather System with 4 Design Patterns");
        info.put("patterns", List.of("Strategy", "Observer", "Factory", "Singleton"));
        info.put("documentation", "Visit /api/weather/patterns for pattern details");
        info.put("endpoints", Map.of(
                "current", "GET /api/weather/current",
                "update", "POST /api/weather/update",
                "strategy", "POST /api/weather/strategy",
                "manual", "POST /api/weather/manual/data",
                "factory", "POST /api/weather/factory/create",
                "status", "GET /api/weather/status",
                "patterns", "GET /api/weather/patterns",
                "singleton", "GET /api/weather/singleton",
                "observers", "GET /api/weather/observers"
        ));
        return info;
    }

    private Map<String, Object> convertToMap(WeatherData data) {
        Map<String, Object> map = new HashMap<>();
        if (data != null) {
            map.put("city", data.getCity());
            map.put("temperature", data.getTemperature());
            map.put("humidity", data.getHumidity());
            map.put("pressure", data.getPressure());
            map.put("windSpeed", data.getWindSpeed());
            map.put("timestamp", data.getTimestamp().toString());
            map.put("formatted", String.format(
                    "%s: %.1f°C, %.0f%%, %.0fhPa, %.1fm/s",
                    data.getCity(), data.getTemperature(), data.getHumidity(),
                    data.getPressure(), data.getWindSpeed()
            ));
        }
        return map;
    }

    private Observer createObserverByType(String type, String name) {
        if (name == null) {
            name = "Default";
        }

        switch (type.toLowerCase()) {
            case "web":
                return new WebDisplay(name);
            case "phone":
                return new PhoneDisplay(name);
            case "alert":
                return new AlertDisplay();
            case "stats":
            case "statistics":
                return new StatisticsDisplay();
            case "logger":
                return new LoggerDisplay();
            case "history":
                return new HistoryCollector();
            default:
                return null;
        }
    }
    private UpdateStrategy getCurrentStrategy() {
        return station.getCurrentStrategy();
    }
}