package studytracker.ui;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import studytracker.controller.StatisticsController;

public class StatisticsViewController {

    @FXML private DatePicker statisticsStartDatePicker;
    @FXML private DatePicker statisticsEndDatePicker;
    @FXML private Label statisticsTotalTimeLabel;
    @FXML private Label statisticsSubjectCountLabel;
    @FXML private BarChart<String, Number> studyTimeBySubjectChart;

    private StatisticsController statisticsController;

    public void configure(StatisticsController statisticsController) {
        this.statisticsController = statisticsController;
        showCurrentWeek();
    }

    @FXML
    private void updateStatistics() {
        LocalDate startDate = statisticsStartDatePicker.getValue();
        LocalDate endDate = statisticsEndDatePicker.getValue();
        if (startDate == null || endDate == null) {
            UiSupport.showError("Choose a date range", "Select both a start date and an end date.");
            return;
        }
        if (endDate.isBefore(startDate)) {
            UiSupport.showError("Invalid date range", "The end date must not be before the start date.");
            return;
        }
        refreshStatistics(startDate, endDate);
    }

    @FXML
    private void showCurrentWeek() {
        LocalDate today = LocalDate.now();
        statisticsStartDatePicker.setValue(today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)));
        statisticsEndDatePicker.setValue(today);
        if (statisticsController != null) {
            refreshStatistics(statisticsStartDatePicker.getValue(), statisticsEndDatePicker.getValue());
        }
    }

    private void refreshStatistics(LocalDate startDate, LocalDate endDate) {
        try {
            Map<String, Duration> timeBySubject = statisticsController.getStudyTimeBySubject(startDate, endDate);
            statisticsTotalTimeLabel.setText(UiSupport.formatShortDuration(
                    statisticsController.getTotalStudyTime(startDate, endDate)));
            statisticsSubjectCountLabel.setText(String.valueOf(timeBySubject.size()));

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            timeBySubject.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> series.getData().add(new XYChart.Data<>(
                            entry.getKey(), entry.getValue().toSeconds() / 60.0)));
            studyTimeBySubjectChart.setData(FXCollections.observableArrayList(series));
        } catch (IOException exception) {
            UiSupport.showError("Unable to load statistics", exception.getMessage());
        }
    }
}
