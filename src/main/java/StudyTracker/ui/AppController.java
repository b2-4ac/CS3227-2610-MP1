package studytracker.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import studytracker.controller.StatisticsController;
import studytracker.controller.SubjectController;
import studytracker.controller.TimerController;

public class AppController {

    @FXML private Button dashboardNavigationButton;
    @FXML private Button sessionsNavigationButton;
    @FXML private Button subjectsNavigationButton;
    @FXML private Button statisticsNavigationButton;
    @FXML private StackPane contentPane;

    private TimerController timerController;
    private SubjectController subjectController;
    private StatisticsController statisticsController;
    private DashboardController activeDashboardController;

    /**
     * Supplies the shared application controllers required by the JavaFX views and
     * opens the Dashboard as the initial view.
     *
     * @param timerController controller used by timer-related views
     * @param subjectController controller used by subject-related views
     * @param statisticsController controller used by statistics-related views
     */
    public void configure(
            TimerController timerController,
            SubjectController subjectController,
            StatisticsController statisticsController) {
        this.timerController = timerController;
        this.subjectController = subjectController;
        this.statisticsController = statisticsController;
        showDashboard();
    }

    /**
     * Loads the Dashboard view and configures it with the shared application
     * controllers. This method is also invoked by the Dashboard navigation action.
     */
    @FXML
    private void showDashboard() {
        if (!isConfigured()) {
            return;
        }

        loadView("dashboard.fxml", dashboardNavigationButton, loader -> {
            DashboardController controller = loader.getController();
            controller.configure(
                    timerController,
                    subjectController,
                    statisticsController,
                    this::showSessions);
            activeDashboardController = controller;
        });
    }

    /**
     * Loads the completed-session history view and supplies its timer controller.
     * This method is invoked by the Sessions navigation action.
     */
    @FXML
    private void showSessions() {
        if (!isConfigured()) {
            return;
        }

        loadView("sessions.fxml", sessionsNavigationButton, loader ->
                loader.<SessionViewController>getController().configure(timerController));
    }

    /**
     * Loads the subject-management view and supplies its subject controller. This
     * method is invoked by the Subjects navigation action.
     */
    @FXML
    private void showSubjects() {
        if (!isConfigured()) {
            return;
        }

        loadView("subjects.fxml", subjectsNavigationButton, loader ->
                loader.<SubjectViewController>getController().configure(subjectController));
    }

    /**
     * Loads the statistics view and supplies its statistics controller. This method
     * is invoked by the Statistics navigation action.
     */
    @FXML
    private void showStatistics() {
        if (!isConfigured()) {
            return;
        }

        loadView("statistics.fxml", statisticsNavigationButton, loader ->
                loader.<StatisticsViewController>getController().configure(statisticsController));
    }

    /**
     * Determines whether shared application dependencies have been supplied.
     *
     * @return {@code true} when this controller is ready to load configured views
     */
    private boolean isConfigured() {
        return timerController != null;
    }

    /**
     * Loads an FXML view, configures its controller, and replaces the current
     * content-pane node with the loaded view.
     *
     * <p>If the Dashboard is currently active, its refresh timeline is stopped
     * before the new view is shown.</p>
     *
     * @param viewName FXML filename located in {@code /studytracker/views/}
     * @param selectedButton sidebar button that represents the loaded view
     * @param configurer callback that configures the controller created for the view
     */
    private void loadView(String viewName, Button selectedButton, ViewConfigurer configurer) {
        if (activeDashboardController != null) {
            activeDashboardController.dispose();
            activeDashboardController = null;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/studytracker/views/" + viewName));
            Node view = loader.load();
            configurer.configure(loader);
            contentPane.getChildren().setAll(view);
            selectNavigationButton(selectedButton);
        } catch (IOException exception) {
            UiSupport.showError("Unable to open view", exception.getMessage());
        }
    }

    /**
     * Updates sidebar styling so that only the button for the current view is marked
     * as active.
     *
     * @param selectedButton navigation button associated with the current view
     */
    private void selectNavigationButton(Button selectedButton) {
        for (Button button : new Button[] {
                dashboardNavigationButton,
                sessionsNavigationButton,
                subjectsNavigationButton,
                statisticsNavigationButton }) {
            button.getStyleClass().remove("navigation-button-active");
        }
        selectedButton.getStyleClass().add("navigation-button-active");
    }

    @FunctionalInterface
    private interface ViewConfigurer {

        /**
         * Configures the controller created while loading an FXML view.
         *
         * @param loader FXML loader that created the view and its controller
         */
        void configure(FXMLLoader loader);
    }
}
