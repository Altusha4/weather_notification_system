package weather.core;

import weather.observers.HistoryCollector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class ReportGenerator {

    public static void export(HistoryCollector history, String fileName) {
        try {
            File folder = new File("reports");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File file = new File(folder, fileName);

            try (FileWriter writer = new FileWriter(file, false)) {
                var list = history.getHistory();
                if (list.isEmpty()) {
                    writer.write("No weather data to export.\n");
                    System.out.println("[Report] no data to save");
                    return;
                }

                writer.write("=== WEATHER HISTORY REPORT ===\n");
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneId.systemDefault());

                for (var d : list) {
                    writer.write(String.format("%s | %s | %.1f°C | %.0f%% | %.0f hPa | %.1f m/s%n",
                            fmt.format(d.getTimestamp()),
                            d.getCity(),
                            d.getTemperature(),
                            d.getHumidity(),
                            d.getPressure(),
                            d.getWindSpeed()));
                }
                System.out.println("[Report] saved to " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("[Report] error: " + e.getMessage());
        }
    }
}
