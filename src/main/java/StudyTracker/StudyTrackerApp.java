package studytracker;

import java.nio.file.Path;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import studytracker.controller.StatisticsController;
import studytracker.controller.SubjectController;
import studytracker.controller.TimerController;
import studytracker.model.StudyTimer;
import studytracker.storage.StudySessionStorage;
import studytracker.storage.SubjectStorage;
import studytracker.ui.AppController;

public class StudyTrackerApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Create the timer
        StudyTimer timer = new StudyTimer();

        // Create Storage Object
        StudySessionStorage studySessionStorage = new StudySessionStorage(Path.of("data", "sessions.json"));

        SubjectStorage subjectStorage = new SubjectStorage(Path.of("data", "subjects.json"));

        // Create the controller
        TimerController timerController = new TimerController(timer, studySessionStorage);

        SubjectController subjectController = new SubjectController(subjectStorage);

        StatisticsController statisticsController = new StatisticsController(studySessionStorage);

        FXMLLoader loader = new FXMLLoader(
                StudyTrackerApp.class.getResource("/studytracker/views/app.fxml"));
        Scene scene = new Scene(loader.load());

        AppController appController = loader.getController();
        appController.configure(timerController, subjectController, statisticsController);

        stage.setTitle("Study Tracker");
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
