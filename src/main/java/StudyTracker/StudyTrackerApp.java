package studytracker;

import studytracker.controller.TimerController;
import studytracker.model.StudyTimer;
import studytracker.ui.ConsoleUI;

public class StudyTrackerApp {

    public static void main(String[] args) {

        // Create the timer
        StudyTimer timer = new StudyTimer();

        // Create the controller
        TimerController timerController = new TimerController(timer);

        // Create the user interface
        ConsoleUI consoleUI = new ConsoleUI(timerController);

        // Start the application
        consoleUI.run();
    }
}
