package studytracker.model;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudyTimerTest {

    private final Subject subject = new Subject("Computer Science");

    @Test
    void newTimerHasInitialState() {
        StudyTimer timer = new StudyTimer();

        assertAll(
                () -> assertEquals(TimerState.NOT_STARTED, timer.getState()),
                () -> assertEquals(Duration.ZERO, timer.getElapsedTime()),
                () -> assertNull(timer.getSubject()));
    }

    @Test
    void startSetsRunningStateAndSubject() {
        StudyTimer timer = new StudyTimer();

        timer.start(subject);

        assertAll(
                () -> assertEquals(TimerState.RUNNING, timer.getState()),
                () -> assertSame(subject, timer.getSubject()),
                () -> assertFalse(timer.getElapsedTime().isNegative()));
    }

    @Test
    void startTwiceThrows() {
        StudyTimer timer = startedTimer();

        assertThrows(IllegalStateException.class, () -> timer.start(subject));
    }

    @Test
    void startAfterPauseThrows() {
        StudyTimer timer = startedTimer();
        timer.pause();

        assertThrows(IllegalStateException.class, () -> timer.start(subject));
    }

    @Test
    void pauseFreezesElapsedTime() throws InterruptedException {
        StudyTimer timer = startedTimer();
        Thread.sleep(10);

        timer.pause();
        Duration elapsedWhenPaused = timer.getElapsedTime();
        Thread.sleep(10);

        assertAll(
                () -> assertEquals(TimerState.PAUSED, timer.getState()),
                () -> assertTrue(elapsedWhenPaused.isPositive()),
                () -> assertEquals(elapsedWhenPaused, timer.getElapsedTime()));
    }

    @Test
    void pauseBeforeStartThrows() {
        StudyTimer timer = new StudyTimer();

        assertThrows(IllegalStateException.class, timer::pause);
    }

    @Test
    void pauseTwiceThrows() {
        StudyTimer timer = startedTimer();
        timer.pause();

        assertThrows(IllegalStateException.class, timer::pause);
    }

    @Test
    void resumeReturnsPausedTimerToRunningStateAndElapsedTimeGrows() throws InterruptedException {
        StudyTimer timer = startedTimer();
        timer.pause();
        Duration elapsedBeforeResume = timer.getElapsedTime();

        timer.resume();
        Thread.sleep(10);

        assertAll(
                () -> assertEquals(TimerState.RUNNING, timer.getState()),
                () -> assertTrue(timer.getElapsedTime().compareTo(elapsedBeforeResume) > 0));
    }

    @Test
    void resumeBeforeStartThrows() {
        StudyTimer timer = new StudyTimer();

        assertThrows(IllegalStateException.class, timer::resume);
    }

    @Test
    void resumeWhileRunningThrows() {
        StudyTimer timer = startedTimer();

        assertThrows(IllegalStateException.class, timer::resume);
    }

    @Test
    void stopWhileRunningReturnsSessionAndResetsTimer() {
        StudyTimer timer = startedTimer();

        StudySession session = timer.stop();

        assertAll(
                () -> assertSame(subject, session.getSubject()),
                () -> assertFalse(session.getDuration().isNegative()),
                () -> assertEquals(TimerState.NOT_STARTED, timer.getState()),
                () -> assertNull(timer.getSubject()),
                () -> assertEquals(Duration.ZERO, timer.getElapsedTime()));
    }

    @Test
    void stopWhilePausedReturnsSessionWithoutAddingPausedTime() throws InterruptedException {
        StudyTimer timer = startedTimer();
        Thread.sleep(10);
        timer.pause();
        Duration elapsedWhenPaused = timer.getElapsedTime();
        Thread.sleep(10);

        StudySession session = timer.stop();

        assertAll(
                () -> assertEquals(elapsedWhenPaused, session.getDuration()),
                () -> assertEquals(TimerState.NOT_STARTED, timer.getState()));
    }

    @Test
    void stopBeforeStartThrows() {
        StudyTimer timer = new StudyTimer();

        assertThrows(IllegalStateException.class, timer::stop);
    }

    @Test
    void resetWhileRunningRestoresInitialState() {
        StudyTimer timer = startedTimer();

        timer.reset();

        assertInitialState(timer);
    }

    @Test
    void resetWhilePausedRestoresInitialState() {
        StudyTimer timer = startedTimer();
        timer.pause();

        timer.reset();

        assertInitialState(timer);
    }

    private StudyTimer startedTimer() {
        StudyTimer timer = new StudyTimer();
        timer.start(subject);
        return timer;
    }

    private void assertInitialState(StudyTimer timer) {
        assertAll(
                () -> assertEquals(TimerState.NOT_STARTED, timer.getState()),
                () -> assertEquals(Duration.ZERO, timer.getElapsedTime()),
                () -> assertNull(timer.getSubject()));
    }
}
