package studytracker;

import java.nio.file.Path;

import studytracker.controller.StatisticsController;
import studytracker.controller.SubjectController;
import studytracker.controller.TimerController;
import studytracker.model.StudyTimer;
import studytracker.storage.StudySessionStorage;
import studytracker.storage.SubjectStorage;
import studytracker.ui.ConsoleUI;

public class StudyTrackerApp {

    public static void main(String[] args) {

        // Create the timer
        StudyTimer timer = new StudyTimer();

        // Create Storage Object
        StudySessionStorage studySessionStorage = new StudySessionStorage(Path.of("data", "sessions.json"));

        SubjectStorage subjectStorage = new SubjectStorage(Path.of("data", "subjects.json"));

        // Create the controller
        TimerController timerController = new TimerController(timer, studySessionStorage);

        SubjectController subjectController = new SubjectController(subjectStorage);

        StatisticsController statisticsController = new StatisticsController(studySessionStorage);

        // Create the user interface
        ConsoleUI consoleUI = new ConsoleUI(timerController, subjectController, statisticsController);

        // Start the application
        consoleUI.run();
    }
}
