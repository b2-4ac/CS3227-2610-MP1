package studytracker.controller;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import studytracker.model.StudySession;
import studytracker.model.StudyTimer;
import studytracker.model.Subject;
import studytracker.model.TimerState;
import studytracker.storage.StudySessionStorage;

public class TimerController {

    private final StudyTimer timer;
    private final StudySessionStorage storage;

    public TimerController(StudyTimer timer, StudySessionStorage storage) {
        this.timer = timer;
        this.storage = storage;
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

    public StudySession stopSession() throws IOException {
        StudySession session = timer.stop();

        storage.save(session);

        return session;
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

    public List<StudySession> getPastSessions() throws IOException {
        return storage.loadAll();
    }
}
