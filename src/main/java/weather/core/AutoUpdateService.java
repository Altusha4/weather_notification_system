package weather.core;
import java.util.Timer;
import java.util.TimerTask;

public class AutoUpdateService {
    private final WeatherStation station;
    private final Timer timer = new Timer();
    private boolean running = false;

    public AutoUpdateService(WeatherStation station) {
        this.station = station;
    }

    public void start(long secondsInterval) {
        if (running) {
            System.out.println("[AutoUpdate] already running");
            return;
        }
        running = true;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                station.updateWeather();
            }
        }, 0, secondsInterval * 1000);
        System.out.println("[AutoUpdate] started every " + secondsInterval + " seconds");
    }

    public void stop() {
        if (!running) {
            System.out.println("[AutoUpdate] not running");
            return;
        }
        timer.cancel();
        running = false;
        System.out.println("[AutoUpdate] stopped");
    }

    public boolean isRunning() {
        return running;
    }
}