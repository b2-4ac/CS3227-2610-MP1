package studytracker.ui;

import java.time.Duration;

import javafx.scene.control.Alert;

final class UiSupport {

    private UiSupport() {
    }

    static String formatDuration(Duration duration) {
        long totalSeconds = Math.max(0, duration.getSeconds());
        long hours = totalSeconds / 3_600;
        long minutes = (totalSeconds % 3_600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    static String formatShortDuration(Duration duration) {
        long totalMinutes = Math.max(0, duration.toMinutes());
        return "%d h %d m".formatted(totalMinutes / 60, totalMinutes % 60);
    }

    static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
