package studytracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudyTimer {

    private TimerState state;
    private Subject subject;

    // When the current uninterrupted period of studying began
    private LocalDateTime currentRunStartTime;

    // Completed uninterrupted periods of studying in the current session
    private List<StudyInterval> intervals;

    public StudyTimer() {
        state = TimerState.NOT_STARTED;
        intervals = new ArrayList<>();
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
        this.currentRunStartTime = LocalDateTime.now();
        this.intervals.clear();
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

        intervals.add(new StudyInterval(currentRunStartTime, now));

        state = TimerState.PAUSED;
        currentRunStartTime = null;
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

        if (state == TimerState.RUNNING) {
            addCurrentRunToIntervals();
        }

        StudySession session = new StudySession(
                subject,
                intervals);

        reset();

        return session;
    }

    public Duration getElapsedTime() {
        Duration completedDuration = intervals.stream()
                .map(interval -> Duration.between(interval.getStartTime(), interval.getEndTime()))
                .reduce(Duration.ZERO, Duration::plus);

        if (state == TimerState.RUNNING) {
            return completedDuration.plus(Duration.between(currentRunStartTime, LocalDateTime.now()));
        }

        return completedDuration;
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
        currentRunStartTime = null;
        intervals.clear();
    }

    private void addCurrentRunToIntervals() {
        LocalDateTime now = LocalDateTime.now();
        intervals.add(new StudyInterval(currentRunStartTime, now));
        currentRunStartTime = null;
    }
}
