package studytracker.ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import studytracker.controller.TimerController;
import studytracker.model.StudySession;

public class SessionViewController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM uuuu, HH:mm");

    @FXML private DatePicker sessionStartDatePicker;
    @FXML private DatePicker sessionEndDatePicker;
    @FXML private Label sessionCountLabel;
    @FXML private TableView<StudySession> sessionsTable;
    @FXML private TableColumn<StudySession, String> sessionSubjectColumn;
    @FXML private TableColumn<StudySession, String> sessionStartColumn;
    @FXML private TableColumn<StudySession, String> sessionEndColumn;
    @FXML private TableColumn<StudySession, String> sessionDurationColumn;

    private TimerController timerController;

    @FXML
    private void initialize() {
        sessionSubjectColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getSubject().getName()));
        sessionStartColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(DATE_TIME_FORMATTER.format(cell.getValue().getStartTime())));
        sessionEndColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(DATE_TIME_FORMATTER.format(cell.getValue().getEndTime())));
        sessionDurationColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(UiSupport.formatDuration(cell.getValue().getDuration())));
    }

    public void configure(TimerController timerController) {
        this.timerController = timerController;
        refreshSessions();
    }

    @FXML
    private void applyFilter() {
        LocalDate startDate = sessionStartDatePicker.getValue();
        LocalDate endDate = sessionEndDatePicker.getValue();
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            UiSupport.showError("Invalid date range", "The end date must not be before the start date.");
            return;
        }
        refreshSessions();
    }

    @FXML
    private void clearFilter() {
        sessionStartDatePicker.setValue(null);
        sessionEndDatePicker.setValue(null);
        refreshSessions();
    }

    private void refreshSessions() {
        try {
            LocalDate startDate = sessionStartDatePicker.getValue();
            LocalDate endDate = sessionEndDatePicker.getValue();
            List<StudySession> filteredSessions = timerController.getPastSessions().stream()
                    .filter(session -> startDate == null
                            || !session.getStartTime().toLocalDate().isBefore(startDate))
                    .filter(session -> endDate == null
                            || !session.getStartTime().toLocalDate().isAfter(endDate))
                    .toList();
            sessionsTable.setItems(FXCollections.observableArrayList(filteredSessions));
            sessionCountLabel.setText(filteredSessions.size() + " sessions");
        } catch (IOException exception) {
            UiSupport.showError("Unable to load sessions", exception.getMessage());
        }
    }
}
