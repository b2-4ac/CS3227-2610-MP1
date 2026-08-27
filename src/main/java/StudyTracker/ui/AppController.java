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

    public void configure(
            TimerController timerController,
            SubjectController subjectController,
            StatisticsController statisticsController) {
        this.timerController = timerController;
        this.subjectController = subjectController;
        this.statisticsController = statisticsController;
        showDashboard();
    }

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

    @FXML
    private void showSessions() {
        if (!isConfigured()) {
            return;
        }

        loadView("sessions.fxml", sessionsNavigationButton, loader ->
                loader.<SessionViewController>getController().configure(timerController));
    }

    @FXML
    private void showSubjects() {
        if (!isConfigured()) {
            return;
        }

        loadView("subjects.fxml", subjectsNavigationButton, loader ->
                loader.<SubjectViewController>getController().configure(subjectController));
    }

    @FXML
    private void showStatistics() {
        if (!isConfigured()) {
            return;
        }

        loadView("statistics.fxml", statisticsNavigationButton, loader ->
                loader.<StatisticsViewController>getController().configure(statisticsController));
    }

    private boolean isConfigured() {
        return timerController != null;
    }

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
        void configure(FXMLLoader loader);
    }
}
