package studytracker.ui;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import studytracker.controller.StatisticsController;
import studytracker.controller.SubjectController;
import studytracker.controller.TimerController;
import studytracker.model.StudySession;
import studytracker.model.Subject;
import studytracker.model.TimerState;

public class DashboardController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM uuuu, HH:mm");

    @FXML private ComboBox<Subject> timerSubjectComboBox;
    @FXML private Label timerStateLabel;
    @FXML private Label elapsedTimeLabel;
    @FXML private Button startTimerButton;
    @FXML private Button pauseTimerButton;
    @FXML private Button resumeTimerButton;
    @FXML private Button stopTimerButton;
    @FXML private Label todayTotalLabel;
    @FXML private Label todaySessionCountLabel;
    @FXML private Label todaySubjectCountLabel;
    @FXML private TableView<StudySession> recentSessionsTable;
    @FXML private TableColumn<StudySession, String> recentSessionSubjectColumn;
    @FXML private TableColumn<StudySession, String> recentSessionStartColumn;
    @FXML private TableColumn<StudySession, String> recentSessionEndColumn;
    @FXML private TableColumn<StudySession, String> recentSessionDurationColumn;

    private TimerController timerController;
    private SubjectController subjectController;
    private StatisticsController statisticsController;
    private Runnable sessionsNavigation;
    private Timeline timerRefreshTimeline;

    @FXML
    private void initialize() {
        recentSessionSubjectColumn.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyStringWrapper(cell.getValue().getSubject().getName()));
        recentSessionStartColumn.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        DATE_TIME_FORMATTER.format(cell.getValue().getStartTime())));
        recentSessionEndColumn.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        DATE_TIME_FORMATTER.format(cell.getValue().getEndTime())));
        recentSessionDurationColumn.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyStringWrapper(UiSupport.formatShortDuration(cell.getValue().getDuration())));

        timerRefreshTimeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), event -> refreshTimer()));
        timerRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void configure(
            TimerController timerController,
            SubjectController subjectController,
            StatisticsController statisticsController,
            Runnable sessionsNavigation) {
        this.timerController = timerController;
        this.subjectController = subjectController;
        this.statisticsController = statisticsController;
        this.sessionsNavigation = sessionsNavigation;
        refreshDashboard();
        timerRefreshTimeline.play();
    }

    @FXML
    private void startTimer() {
        Subject subject = timerSubjectComboBox.getValue();
        if (subject == null) {
            UiSupport.showError("Choose a subject", "Select a subject before starting a study session.");
            return;
        }

        timerController.startSession(subject);
        refreshTimer();
    }

    @FXML
    private void pauseTimer() {
        timerController.pauseSession();
        refreshTimer();
    }

    @FXML
    private void resumeTimer() {
        timerController.resumeSession();
        refreshTimer();
    }

    @FXML
    private void stopTimer() {
        try {
            timerController.stopSession();
            refreshDashboard();
        } catch (IOException exception) {
            UiSupport.showError("Unable to save session", exception.getMessage());
        }
    }

    @FXML
    private void viewAllSessions() {
        sessionsNavigation.run();
    }

    public void dispose() {
        if (timerRefreshTimeline != null) {
            timerRefreshTimeline.stop();
        }
    }

    private void refreshDashboard() {
        try {
            List<Subject> subjects = subjectController.getSubjects();
            timerSubjectComboBox.setItems(FXCollections.observableArrayList(subjects));

            List<StudySession> sessions = timerController.getPastSessions();
            List<StudySession> todaySessions = sessions.stream()
                    .filter(session -> session.getStartTime().toLocalDate().equals(LocalDate.now()))
                    .toList();
            recentSessionsTable.setItems(FXCollections.observableArrayList(todaySessions));
            todaySessionCountLabel.setText(String.valueOf(todaySessions.size()));
            todaySubjectCountLabel.setText(String.valueOf(todaySessions.stream()
                    .map(session -> session.getSubject().getName())
                    .distinct()
                    .count()));
            todayTotalLabel.setText(UiSupport.formatShortDuration(
                    statisticsController.getTotalStudyTime(LocalDate.now(), LocalDate.now())));
            refreshTimer();
        } catch (IOException exception) {
            UiSupport.showError("Unable to load dashboard", exception.getMessage());
        }
    }

    private void refreshTimer() {
        if (timerController == null) {
            return;
        }

        TimerState state = timerController.getState();
        elapsedTimeLabel.setText(UiSupport.formatDuration(timerController.getElapsedTime()));
        timerStateLabel.setText(switch (state) {
            case NOT_STARTED -> "Not started";
            case RUNNING -> "Studying";
            case PAUSED -> "Paused";
        });
        timerSubjectComboBox.setDisable(state != TimerState.NOT_STARTED);
        startTimerButton.setDisable(state != TimerState.NOT_STARTED);
        pauseTimerButton.setDisable(state != TimerState.RUNNING);
        resumeTimerButton.setDisable(state != TimerState.PAUSED);
        stopTimerButton.setDisable(state == TimerState.NOT_STARTED);
    }
}
