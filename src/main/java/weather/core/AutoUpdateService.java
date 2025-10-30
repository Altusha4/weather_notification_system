package weather.core;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoUpdateService {
    private final WeatherStation station;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final long intervalSeconds;

    public AutoUpdateService(WeatherStation station, long intervalSeconds) {
        this.station = station;
        this.intervalSeconds = intervalSeconds;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "AutoUpdateService-Worker");
            thread.setDaemon(true);
            return thread;
        });
        System.out.println("[AutoUpdateService] Created with interval: " + intervalSeconds + " seconds");
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            scheduler.scheduleAtFixedRate(
                    this::safeUpdateWeather,
                    0, // начальная задержка
                    intervalSeconds,
                    TimeUnit.SECONDS
            );
            System.out.println("[AutoUpdateService] Started with interval: " + intervalSeconds + " seconds");
        } else {
            System.out.println("[AutoUpdateService] Already running");
        }
    }

    public void start(long customIntervalSeconds) {
        if (running.compareAndSet(false, true)) {
            scheduler.scheduleAtFixedRate(
                    this::safeUpdateWeather,
                    0,
                    customIntervalSeconds,
                    TimeUnit.SECONDS
            );
            System.out.println("[AutoUpdateService] Started with custom interval: " + customIntervalSeconds + " seconds");
        } else {
            System.out.println("[AutoUpdateService] Already running");
        }
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                    System.out.println("[AutoUpdateService] Forcefully stopped");
                } else {
                    System.out.println("[AutoUpdateService] Stopped gracefully");
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
                System.out.println("[AutoUpdateService] Interrupted during shutdown");
            }
        } else {
            System.out.println("[AutoUpdateService] Not running");
        }
    }

    public void stopImmediately() {
        if (running.get()) {
            scheduler.shutdownNow();
            running.set(false);
            System.out.println("[AutoUpdateService] Stopped immediately");
        } else {
            System.out.println("[AutoUpdateService] Not running");
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public long getIntervalSeconds() {
        return intervalSeconds;
    }

    public void restart() {
        stop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        start();
    }

    public void restart(long newIntervalSeconds) {
        stop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        start(newIntervalSeconds);
    }

    public String getStatus() {
        return String.format(
                "AutoUpdateService{running=%s, interval=%d seconds, station=%s}",
                running.get(),
                intervalSeconds,
                station.getName()
        );
    }

    private void safeUpdateWeather() {
        try {
            if (station != null) {
                station.updateWeather();
            } else {
                System.err.println("[AutoUpdateService] WeatherStation is null!");
                stop();
            }
        } catch (Exception e) {
            System.err.println("[AutoUpdateService] Error during auto-update: " + e.getMessage());
        }
    }

    public void shutdown() {
        if (running.get()) {
            stop();
        }

        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(3, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("[AutoUpdateService] Shutdown completed");
    }
}