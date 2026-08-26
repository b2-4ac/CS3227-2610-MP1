package studytracker.ui;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

import studytracker.controller.TimerController;
import studytracker.model.StudySession;
import studytracker.model.Subject;
import studytracker.controller.SubjectController;
import studytracker.model.TimerState;

public class ConsoleUI {

    private final Scanner scanner;
    private final TimerController timerController;
    private final SubjectController subjectController;

    public ConsoleUI(TimerController timerController, SubjectController subjectController) {
        this.scanner = new Scanner(System.in);
        this.timerController = timerController;
        this.subjectController = subjectController;
    }

    public void run() {
        boolean isAppRunning = true;

        displayWelcome();

        while (isAppRunning) {
            displayMenu();

            int choice = getMenuChoice();
            TimerState state = timerController.getState();

            switch (choice) {
            case 1:
                if (state == TimerState.RUNNING) {
                    pauseStudySession();

                } else if (state == TimerState.PAUSED) {
                    resumeStudySession();

                } else {
                    startStudySession();
                }
                break;

            case 2:
                if (state != TimerState.NOT_STARTED) {
                    stopStudySession();
                } else {
                    System.out.println("Invalid option.");
                }
                break;

            case 3:
                displayTimerStatus();
                break;

            case 4:
                viewPastSessions();
                break;

            case 5:
                manageSubjects();
                break;

            case 6:
                isAppRunning = false;
                break;

            default:
                System.out.println("Invalid option.");
            }
        }

        scanner.close();
    }

    private void manageSubjects() {

        boolean managing = true;

        while (managing) {

            System.out.println();
            System.out.println("===== MANAGE SUBJECTS =====");
            System.out.println("1. View Subjects");
            System.out.println("2. Add Subject");
            System.out.println("3. Delete Subject");
            System.out.println("4. Back");

            int choice = getMenuChoice();

            switch (choice) {
            case 1:
                displaySubjects();
                break;

            case 2:
                addSubject();
                break;

            case 3:
                deleteSubject();
                break;

            case 4:
                managing = false;
                break;

            default:
                System.out.println("Invalid option.");
            }
        }
    }

    private void displayWelcome() {
        System.out.println("================================");
        System.out.println("         STUDY TRACKER");
        System.out.println("================================");
    }

    private void displayMenu() {
        System.out.println();

        TimerState state = timerController.getState();

        switch (state) {

            case NOT_STARTED:
                System.out.println("1. Start Study Session");
                break;

            case RUNNING:
                System.out.println("1. Pause Study Session");
                System.out.println("2. Stop Study Session");
                break;

            case PAUSED:
                System.out.println("1. Resume Study Session");
                System.out.println("2. Stop Study Session");
                break;
        }

        System.out.println("3. View Timer Status");
        System.out.println("4. View Past Sessions");
        System.out.println("5. Manage Subjects");
        System.out.println("6. Exit");
    }

    private int getMenuChoice() {
        System.out.print("Please enter a number: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        return choice;
    }

    private void startStudySession() {
        try {
            List<Subject> subjects = subjectController.getSubjects();

            if (subjects.isEmpty()) {
                System.out.println("You have not added any subjects yet.");
                System.out.println("Please add a subject first.");
                return;
            }

            System.out.println();
            System.out.println("===== SELECT SUBJECT =====");

            for (int i = 0; i < subjects.size(); i++) {
                System.out.println(
                        (i + 1) + ". " + subjects.get(i).getName());
            }

            int choice = getMenuChoice();

            int index = choice - 1;
            if (index < 0 || index >= subjects.size()) {
                System.out.println("Invalid subject selection.");
                return;
            }

            Subject selectedSubject = subjects.get(index);

            timerController.startSession(selectedSubject);

            System.out.println("Started study session for: " + selectedSubject.getName());

        } catch (IOException e) {
            System.out.println("Unable to load subjects: " + e.getMessage());
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

    private void pauseStudySession() {
        try {
            timerController.pauseSession();
            System.out.println("The session has been paused.");
        } catch (IllegalStateException e) {
            System.out.println("Unable to pause session: " + e.getMessage());
        }
    }

    private void resumeStudySession() {
        try {
            timerController.resumeSession();
            System.out.println("The session has resumed.");
        } catch (IllegalStateException e) {
            System.out.println("Unable to resume session: " + e.getMessage());
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

    private void viewPastSessions() {

        try {
            List<StudySession> sessions = timerController.getPastSessions();

            if (sessions.isEmpty()) {
                System.out.println();
                System.out.println("No past study sessions found.");
                return;
            }

            System.out.println();
            System.out.println("===== Past Study Sessions =====");

            for (int i = 0; i < sessions.size(); i++) {

                StudySession session = sessions.get(i);

                System.out.println();
                System.out.println("Session " + (i + 1));
                System.out.println("Subject: " + session.getSubject());
                System.out.println("Start: " + session.getStartTime());
                System.out.println("End: " + session.getEndTime());
                System.out.println("Duration: " + formatDuration(session.getDuration()));
            }

        } catch (IOException e) {
            System.out.println("Unable to load past sessions: " + e.getMessage());
        }
    }

    private void displaySubjects() {

        try {

            List<Subject> subjects = subjectController.getSubjects();

            if (subjects.isEmpty()) {
                System.out.println("No subjects have been added.");
                return;
            }

            System.out.println();
            System.out.println("===== YOUR SUBJECTS =====");

            for (int i = 0; i < subjects.size(); i++) {
                System.out.println((i + 1) + ". " + subjects.get(i).getName());
            }

        } catch (IOException e) {
            System.out.println("Unable to load subjects: " + e.getMessage());
        }
    }

    private void addSubject() {

        System.out.print("Enter subject name: ");

        String name = scanner.nextLine();

        try {
            subjectController.addSubject(name);
            System.out.println("Subject added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid subject: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Unable to save subject: " + e.getMessage());
        }
    }

    private void deleteSubject() {

        try {
            List<Subject> subjects = subjectController.getSubjects();

            if (subjects.isEmpty()) {
                System.out.println("No subjects to delete.");
                return;
            }

            displaySubjects();

            System.out.print("Enter subject number to delete: ");

            int choice = getMenuChoice();

            boolean deleted = subjectController.deleteSubject(choice - 1);

            if (deleted) {
                System.out.println(
                        "Subject deleted successfully.");

            } else {
                System.out.println(
                        "Invalid subject number.");
            }

        } catch (IOException e) {
            System.out.println("Unable to delete subject: " + e.getMessage());
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
