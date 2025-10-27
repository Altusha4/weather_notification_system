package weather.core;

public class ShutdownManager {
    private final AutoUpdateService autoService;

    public ShutdownManager(AutoUpdateService autoService) {
        this.autoService = autoService;
    }

    public void shutdown() {
        System.out.println("\n=== SHUTDOWN INITIATED ===");
        if (autoService != null && autoService.isRunning()) {
            autoService.stop();
        }
        System.out.println("All background tasks stopped successfully.");
        System.out.println("Thank you for using the Weather Notification System!");
        System.out.println("==============================================\n");
    }
}
