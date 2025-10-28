package weather.core;

import weather.observers.HistoryCollector;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class ReportGenerator {

    public static void export(HistoryCollector history, String fileName) {
        System.out.println("[Report] Starting export process...");

        try {
            File folder = new File("reports");
            if (!folder.exists()) {
                System.out.println("[Report] Creating reports directory...");
                boolean created = folder.mkdirs();
                if (!created) {
                    System.out.println("[Report] FAILED to create directory!");
                    return;
                }
            }
            File file = new File(folder, fileName);
            System.out.println("[Report] Target file: " + file.getAbsolutePath());

            if (file.exists()) {
                System.out.println("[Report] Deleting old file...");
                boolean deleted = file.delete();
                System.out.println("[Report] Old file deleted: " + deleted);
            }

            System.out.println("[Report] Creating new file...");
            boolean fileCreated = file.createNewFile();

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file),
                            StandardCharsets.UTF_8
                    )
            )) {

                var list = history.getHistory();
                System.out.println("[Report] Records to export: " + list.size());

                if (list.isEmpty()) {
                    writer.write("=== WEATHER HISTORY REPORT ===");
                    writer.newLine();
                    writer.write("No weather data available for export.");
                    writer.newLine();
                    System.out.println("[Report] No data to save");
                    return;
                }

                writer.write("=== WEATHER HISTORY REPORT ===");
                writer.newLine();
                writer.write("Total records: " + list.size());
                writer.newLine();
                writer.write("Generated at: " + java.time.LocalDateTime.now());
                writer.newLine();
                writer.write("==========================================");
                writer.newLine();
                writer.newLine();

                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneId.systemDefault());

                int recordNumber = 1;
                for (var data : list) {
                    writer.write("RECORD #" + recordNumber++);
                    writer.newLine();
                    writer.write("Time:     " + fmt.format(data.getTimestamp()));
                    writer.newLine();
                    writer.write("City:     " + data.getCity());
                    writer.newLine();
                    writer.write("Temp:     " + String.format("%.1f°C", data.getTemperature()));
                    writer.newLine();
                    writer.write("Humidity: " + String.format("%.0f%%", data.getHumidity()));
                    writer.newLine();
                    writer.write("Pressure: " + String.format("%.0f hPa", data.getPressure()));
                    writer.newLine();
                    writer.write("Wind:     " + String.format("%.1f m/s", data.getWindSpeed()));
                    writer.newLine();
                    writer.write("------------------------------------------");
                    writer.newLine();

                    writer.flush();
                }

                writer.flush();

                System.out.println("[Report] SUCCESS! Saved " + list.size() + " records");
                System.out.println("[Report] File location: " + file.getAbsolutePath());
                System.out.println("[Report] File size: " + file.length() + " bytes");

            } catch (IOException e) {
                System.out.println("[Report] ERROR writing to file: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.out.println("[Report] CRITICAL ERROR: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("[Report] UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void exportCompact(HistoryCollector history, String fileName) {
        System.out.println("[Report] Starting compact export...");

        try {
            File folder = new File("reports");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File file = new File(folder, fileName);

            if (file.exists()) {
                file.delete();
            }

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file),
                            StandardCharsets.UTF_8
                    )
            )) {

                var list = history.getHistory();
                if (list.isEmpty()) {
                    writer.write("No weather data to export.");
                    return;
                }

                writer.write("TIMESTAMP | CITY | TEMPERATURE | HUMIDITY | PRESSURE | WIND SPEED");
                writer.newLine();
                writer.write("==================================================================");
                writer.newLine();

                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneId.systemDefault());

                for (var data : list) {
                    String line = String.format("%s | %s | %.1f°C | %.0f%% | %.0f hPa | %.1f m/s",
                            fmt.format(data.getTimestamp()),
                            data.getCity(),
                            data.getTemperature(),
                            data.getHumidity(),
                            data.getPressure(),
                            data.getWindSpeed());
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                }

                System.out.println("[Report] Compact format saved: " + list.size() + " records");
            }

        } catch (IOException e) {
            System.out.println("[Report] Error: " + e.getMessage());
        }
    }
}