package weather.factory;
import weather.strategy.*;

public class StrategyFactory {

    public static UpdateStrategy create(String type, String city) {
        switch (type.toLowerCase()) {
            case "manual" -> {
                return new ManualInputStrategy();
            }
            case "sensor" -> {
                return new RealTimeSensorStrategy(city);
            }
            case "batch" -> {
                return new ScheduledBatchStrategy(city, 3);
            }
            default -> {
                System.out.println("[Factory] Unknown strategy type, using manual.");
                return new ManualInputStrategy();
            }
        }
    }
}