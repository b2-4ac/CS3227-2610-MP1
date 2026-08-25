package studytracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class StudyTimer {

    private TimerState state;
    private Subject subject;

    // When the overall study session began
    private LocalDateTime sessionStartTime;

    // When the current uninterrupted period of studying began
    private LocalDateTime currentRunStartTime;

    // Total study time accumulated before the current run
    private Duration elapsedTime;

    public StudyTimer() {
        state = TimerState.NOT_STARTED;
        elapsedTime = Duration.ZERO;
    }

    /**
     * Starts the timer for the desired subject.
     *
     * @param subject Subject that is currently being studied.
     */
    public void start(Subject subject) {
        if (state != TimerState.NOT_STARTED) {
            throw new IllegalStateException("Timer has already been started");
        }

        this.subject = subject;
        this.sessionStartTime = LocalDateTime.now();
        this.currentRunStartTime = sessionStartTime;
        this.elapsedTime = Duration.ZERO;
        this.state = TimerState.RUNNING;
    }

    /**
     * Pauses the current active timer if currently running.
     */
    public void pause() {
        if (state != TimerState.RUNNING) {
            throw new IllegalStateException("Timer is not currently running");
        }

        LocalDateTime now = LocalDateTime.now();

        elapsedTime = elapsedTime.plus(Duration.between(currentRunStartTime, now));

        state = TimerState.PAUSED;
    }

    /**
     * Resumes the current active timer if paused.
     */
    public void resume() {
        if (state != TimerState.PAUSED) {
            throw new IllegalStateException("Timer is not currently paused");
        }

        currentRunStartTime = LocalDateTime.now();
        state = TimerState.RUNNING;
    }

    /**
     * Stops the current timer if running and creates a new {@link StudySession}
     * object for storage. Resets the timer.
     *
     * @return New {@link StudySession} object from the recorded time.
     */
    public StudySession stop() {
        if (state == TimerState.NOT_STARTED) {
            throw new IllegalStateException("Timer has not been started");
        }

        // If currently running, add the final run
        if (state == TimerState.RUNNING) {
            LocalDateTime now = LocalDateTime.now();

            elapsedTime = elapsedTime.plus(Duration.between(currentRunStartTime, now));
        }

        LocalDateTime endTime = LocalDateTime.now();

        StudySession session = new StudySession(
                subject,
                sessionStartTime,
                endTime,
                elapsedTime);

        reset();

        return session;
    }

    public Duration getElapsedTime() {
        if (state == TimerState.RUNNING) {
            return elapsedTime.plus(
                    Duration.between(
                            currentRunStartTime,
                            LocalDateTime.now()));
        }

        return elapsedTime;
    }

    public TimerState getState() {
        return state;
    }

    public Subject getSubject() {
        return subject;
    }

    /**
     * Resets the current timer and all of its values.
     */
    public void reset() {
        state = TimerState.NOT_STARTED;
        subject = null;
        sessionStartTime = null;
        currentRunStartTime = null;
        elapsedTime = Duration.ZERO;
    }
}
