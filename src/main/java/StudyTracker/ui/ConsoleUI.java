package studytracker.ui;

import java.io.IOException;
import java.time.Duration;
import java.util.Scanner;

import studytracker.controller.TimerController;
import studytracker.model.StudySession;
import studytracker.model.Subject;
import studytracker.model.TimerState;

public class ConsoleUI {

    private final Scanner scanner;
    private final TimerController timerController;

    public ConsoleUI(TimerController timerController) {
        this.scanner = new Scanner(System.in);
        this.timerController = timerController;
    }

    public void run() {
        boolean running = true;

        displayWelcome();

        while (running) {
            displayMenu();

            int choice = getMenuChoice();

            switch (choice) {
                case 1 -> startStudySession();
                case 2 -> stopStudySession();
                case 3 -> displayTimerStatus();
                case 4 -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option.");
            }
        }

        scanner.close();
    }

    private void displayWelcome() {
        System.out.println("================================");
        System.out.println("         STUDY TRACKER");
        System.out.println("================================");
    }

    private void displayMenu() {
        System.out.println();
        System.out.println("1. Start Study Session");
        System.out.println("2. Stop Study Session");
        System.out.println("3. View Timer Status");
        System.out.println("4. Exit");
    }

    private int getMenuChoice() {
        System.out.print("Please enter a number: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        return choice;
    }

    private void startStudySession() {
        System.out.println();
        System.out.print("Enter subject: ");

        String subjectName = scanner.nextLine();

        Subject subject = new Subject(subjectName);

        try {
            timerController.startSession(subject);

            System.out.println();
            System.out.println("Study session started!");
            System.out.println("Subject: " + subjectName);

        } catch (IllegalStateException e) {
            System.out.println();
            System.out.println("Unable to start session: " + e.getMessage());
        }
    }

    private void stopStudySession() {
        try {
            StudySession session = timerController.stopSession();

            System.out.println();
            System.out.println("Study session completed!");
            System.out.println("Subject: " + session.getSubject());
            System.out.println("Duration: " + formatDuration(session.getDuration()));
        } catch (IllegalStateException e) {
            System.out.println();
            System.out.println("Unable to stop session: " + e.getMessage());
        } catch (IOException e) {
            System.out.println();
            System.out.println("An error has occured while saving the session: " + e.getMessage());

        }
    }

    private void displayTimerStatus() {
        TimerState state = timerController.getState();

        System.out.println();
        System.out.println("Timer state: " + state);

        if (state == TimerState.RUNNING || state == TimerState.PAUSED) {
            Duration elapsedTime = timerController.getElapsedTime();

            System.out.println(
                    "Subject: " + timerController.getSubject());

            System.out.println(
                    "Elapsed time: " + formatDuration(elapsedTime));
        }
    }

    private String formatDuration(Duration duration) {

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        return String.format(
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds);
    }
}
