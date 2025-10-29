package weather.observers;
import weather.core.Observer;
import weather.core.WeatherData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryCollector implements Observer {
    private final List<WeatherData> history = new ArrayList<>();

    @Override
    public void update(WeatherData data) {
        history.add(data);
        System.out.println("[History] stored #" + history.size() + " for " + data.getCity());
    }
    public List<WeatherData> getHistory() {
        return Collections.unmodifiableList(history);
    }
    public void printSummary() {
        if (history.isEmpty()) {
            System.out.println("[History] no records yet");
            return;
        }
        WeatherData last = history.get(history.size() - 1);
        System.out.println("\n=== HISTORY SUMMARY (" + history.size() + " records) ===");
        System.out.println("Last city: " + last.getCity());
        System.out.println("Last temp: " + last.getTemperature() + "°C");
        System.out.println("Last time: " + last.getTimestamp());
        System.out.println("========================================\n");
    }
}