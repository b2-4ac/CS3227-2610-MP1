package studytracker;

import java.nio.file.Path;

import studytracker.controller.TimerController;
import studytracker.model.StudyTimer;
import studytracker.storage.StudySessionStorage;
import studytracker.ui.ConsoleUI;

public class StudyTrackerApp {

    public static void main(String[] args) {

        // Create the timer
        StudyTimer timer = new StudyTimer();

        // Create Storage Object
        StudySessionStorage storage = new StudySessionStorage(Path.of("sessions.json"));

        // Create the controller
        TimerController timerController = new TimerController(timer, storage);

        // Create the user interface
        ConsoleUI consoleUI = new ConsoleUI(timerController);

        // Start the application
        consoleUI.run();
    }
}
