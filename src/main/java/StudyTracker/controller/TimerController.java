package StudyTracker.controller;

import java.time.Duration;

import StudyTracker.model.StudySession;
import StudyTracker.model.StudyTimer;
import StudyTracker.model.Subject;
import StudyTracker.model.TimerState;

public class TimerController {

    private final StudyTimer timer;

    public TimerController(StudyTimer timer) {
        this.timer = timer;
    }

    public void startSession(Subject subject) {
        timer.start(subject);
    }

    public void pauseSession() {
        timer.pause();
    }

    public void resumeSession() {
        timer.resume();
    }

    public StudySession stopSession() {
        return timer.stop();
    }

    public TimerState getState() {
        return timer.getState();
    }

    public Subject getSubject() {
        return timer.getSubject();
    }

    public Duration getElapsedTime() {
        return timer.getElapsedTime();
    }
}
