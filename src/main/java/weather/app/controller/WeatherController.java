package weather.app.controller;

import org.springframework.web.bind.annotation.*;
import weather.core.WeatherStation;
import weather.core.WeatherData;
import weather.core.Observer;
import weather.observers.*;
import weather.strategy.UpdateStrategy;
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
        // ✅ ИСПРАВЛЕННАЯ ИНИЦИАЛИЗАЦИЯ Singleton
        this.station = WeatherStation.getInstance();

        // ✅ ИНИЦИАЛИЗАЦИЯ через отдельный метод
        UpdateStrategy initialStrategy = StrategyFactory.create("manual", "Astana");
        this.station.initialize("Web Weather Station", initialStrategy);

        setupObservers();
    }

    private void setupObservers() {
        // ✅ ДОБАВЛЕНЫ реальные наблюдатели для веб-интерфейса
        try {
            // Веб-дисплей для основного интерфейса
            WebDisplay webDisplay = new WebDisplay("Weather Dashboard");
            station.addObserver(webDisplay);
            activeObservers.add(webDisplay);

            // Логгер для отслеживания операций
            LoggerDisplay logger = new LoggerDisplay();
            station.addObserver(logger);
            activeObservers.add(logger);

            // Сборщик истории для данных графиков
            HistoryCollector history = new HistoryCollector();
            station.addObserver(history);
            activeObservers.add(history);

            // Статистика для аналитики
            StatisticsDisplay stats = new StatisticsDisplay();
            station.addObserver(stats);
            activeObservers.add(stats);

            System.out.println("[WeatherController] " + activeObservers.size() + " observers initialized");

        } catch (Exception e) {
            System.err.println("[WeatherController] Error setting up observers: " + e.getMessage());
        }
    }

    // ==================== STRATEGY PATTERN ====================

    @PostMapping("/strategy")
    public Map<String, Object> setStrategy(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String strategyType = request.get("type");
            String city = request.get("city");

            // ✅ ДОБАВЛЕНА валидация входных данных
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
            // ✅ ДОБАВЛЕНА обработка ошибок
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
            response.put("observersNotified", activeObservers.size());

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
        strategies.put("description", "Разные алгоритмы получения погодных данных");
        strategies.put("usage", "POST /api/weather/strategy with {type, city}");
        return strategies;
    }

    // ==================== OBSERVER PATTERN ====================

    @GetMapping("/observers")
    public Map<String, Object> getObserversInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("pattern", "Observer Pattern");
        info.put("totalCount", station.getObservers().size());
        info.put("activeCount", activeObservers.size());
        info.put("types", station.getObservers().stream()
                .map(o -> o.getClass().getSimpleName())
                .collect(Collectors.toList()));
        info.put("activeObservers", activeObservers.stream()
                .map(o -> o.getClass().getSimpleName())
                .collect(Collectors.toList()));
        info.put("description", "Автоматические уведомления всех подписчиков");
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
                response.put("totalObservers", station.getObservers().size());
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

    @PostMapping("/observers/remove")
    public Map<String, Object> removeObserver(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String type = request.get("type");

            if (type != null) {
                // Находим и удаляем первый наблюдатель указанного типа
                Observer toRemove = activeObservers.stream()
                        .filter(o -> o.getClass().getSimpleName().toLowerCase().contains(type.toLowerCase()))
                        .findFirst()
                        .orElse(null);

                if (toRemove != null) {
                    station.removeObserver(toRemove);
                    activeObservers.remove(toRemove);

                    response.put("status", "success");
                    response.put("message", "Observer removed: " + type);
                    response.put("removedType", toRemove.getClass().getSimpleName());
                } else {
                    response.put("status", "error");
                    response.put("message", "Observer not found: " + type);
                }
            } else {
                response.put("status", "error");
                response.put("message", "Observer type is required");
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to remove observer: " + e.getMessage());
        }

        return response;
    }

    // ==================== FACTORY PATTERN ====================

    @GetMapping("/factory/strategies")
    public Map<String, Object> getFactoryInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("pattern", "Factory Pattern");
        info.put("availableStrategies", List.of("manual", "sensor", "batch"));
        info.put("factoryMethod", "StrategyFactory.create(type, city)");
        info.put("description", "Централизованное создание объектов стратегий");
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
            response.put("description", "Стратегия создана через фабрику");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to create strategy: " + e.getMessage());
        }

        return response;
    }

    // ==================== SINGLETON PATTERN ====================

    @GetMapping("/singleton")
    public Map<String, Object> getSingletonInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("pattern", "Singleton Pattern");
        info.put("implementation", "WeatherStation.getInstance()");
        info.put("purpose", "Единственный экземпляр WeatherStation в системе");
        info.put("instanceHash", station.hashCode());
        info.put("stationName", station.getName());
        info.put("description", "Гарантирует единственный экземпляр станции");
        info.put("currentInstance", station.getStatus());
        return info;
    }

    // ==================== WEATHER DATA OPERATIONS ====================

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

    @PostMapping("/manual")
    public Map<String, Object> manualWeatherUpdate(@RequestBody Map<String, Object> weatherRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Временно переключаемся на ручную стратегию
            UpdateStrategy manualStrategy = StrategyFactory.create("manual", "Manual Input");
            station.setStrategy(manualStrategy);

            // Обновляем погоду (пользователь введет данные в консоли)
            station.updateWeather();
            WeatherData data = station.getLastData();

            response.put("status", "success");
            response.put("message", "Manual weather data received");
            response.put("data", convertToMap(data));

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Manual update failed: " + e.getMessage());
        }

        return response;
    }

    // ==================== SYSTEM STATUS & INFO ====================

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("stationName", station.getName());
        status.put("strategy", station.getCurrentStrategyName());
        status.put("stationStatus", station.getStatus());
        status.put("activeObservers", activeObservers.size());
        status.put("lastUpdate", station.getLastData() != null ?
                station.getLastData().getTimestamp().toString() : "Never");

        if (station.getLastData() != null) {
            status.put("lastData", convertToMap(station.getLastData()));
        }

        status.put("patterns", List.of("Strategy", "Observer", "Factory", "Singleton"));
        status.put("endpoints", List.of(
                "GET /api/weather/current",
                "POST /api/weather/update",
                "POST /api/weather/strategy",
                "GET /api/weather/observers",
                "GET /api/weather/patterns"
        ));

        return status;
    }

    @GetMapping("/patterns")
    public Map<String, Object> getAllPatterns() {
        Map<String, Object> patterns = new HashMap<>();

        patterns.put("Strategy Pattern", Map.of(
                "purpose", "Разные алгоритмы получения данных",
                "classes", "UpdateStrategy, ManualInputStrategy, RealTimeSensorStrategy, ScheduledBatchStrategy",
                "usage", "Переключение стратегий в runtime",
                "endpoint", "POST /api/weather/strategy"
        ));

        patterns.put("Observer Pattern", Map.of(
                "purpose", "Уведомление подписчиков об изменениях",
                "classes", "Subject, Observer, WeatherStation, PhoneDisplay, WebDisplay, etc.",
                "usage", "Автоматическое обновление всех дисплеев",
                "endpoint", "GET /api/weather/observers"
        ));

        patterns.put("Factory Pattern", Map.of(
                "purpose", "Централизованное создание объектов",
                "classes", "StrategyFactory",
                "usage", "StrategyFactory.create(type, city)",
                "endpoint", "POST /api/weather/factory/create"
        ));

        patterns.put("Singleton Pattern", Map.of(
                "purpose", "Единственный экземпляр класса",
                "classes", "WeatherStation.getInstance()",
                "usage", "Глобальный доступ к WeatherStation",
                "endpoint", "GET /api/weather/singleton"
        ));

        return patterns;
    }

    @GetMapping("/chart/history")
    public Map<String, Object> getChartHistory() {
        Map<String, Object> history = new HashMap<>();
        // Здесь можно добавить логику для получения исторических данных
        history.put("message", "Chart history endpoint ready");
        history.put("maxDataPoints", 20);
        history.put("availableMetrics", List.of("temperature", "humidity", "pressure", "windSpeed"));
        return history;
    }

    @GetMapping("/")
    public Map<String, Object> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "Weather Notification System");
        info.put("version", "1.0");
        info.put("description", "REST API for Weather System with Design Patterns");
        info.put("patterns", List.of("Strategy", "Observer", "Factory", "Singleton"));
        info.put("documentation", "Visit /api/weather/patterns for pattern details");
        info.put("endpoints", Map.of(
                "current", "GET /api/weather/current",
                "update", "POST /api/weather/update",
                "strategy", "POST /api/weather/strategy",
                "status", "GET /api/weather/status",
                "patterns", "GET /api/weather/patterns"
        ));
        return info;
    }

    // ==================== PRIVATE HELPER METHODS ====================

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
}
