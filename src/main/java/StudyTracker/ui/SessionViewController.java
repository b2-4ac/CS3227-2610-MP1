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

    /**
     * Configures the session-table columns after their FXML fields have been injected.
     *
     * <p>Each row displays the session's subject, derived start and end times, and total active
     * study duration.</p>
     */
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

    /**
     * Supplies the controller used to retrieve saved study sessions and performs the initial load
     * of the table.
     *
     * @param timerController the timer controller that provides access to past sessions
     */
    public void configure(TimerController timerController) {
        this.timerController = timerController;
        refreshSessions();
    }

    /**
     * Validates the selected date range and refreshes the table using the selected filters.
     *
     * <p>A supplied start date and end date are inclusive. The filter currently compares the
     * derived start date of each session. If the end date precedes the start date, an error is
     * displayed and the existing table contents are retained.</p>
     */
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

    /**
     * Clears both date filters and reloads all available sessions.
     */
    @FXML
    private void clearFilter() {
        sessionStartDatePicker.setValue(null);
        sessionEndDatePicker.setValue(null);
        refreshSessions();
    }

    /**
     * Retrieves past sessions, applies the currently selected inclusive date filters, and updates
     * the session table and displayed session count.
     *
     * <p>If either date filter is empty, that side of the date range is left unbounded. Any
     * failure while loading saved sessions is reported to the user in an error dialog.</p>
     */
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
