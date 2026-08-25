package studytracker.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudySessionTest {

    private final Subject subject = new Subject("Computer Science");

    private final LocalDateTime startTime = LocalDateTime.of(2026, 8, 25, 14, 0);

    private final LocalDateTime endTime = LocalDateTime.of(2026, 8, 25, 15, 0);

    private final Duration duration = Duration.ofHours(1);

    @Test
    void constructorRejectsNullSubject() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new StudySession(
                        null,
                        startTime,
                        endTime,
                        duration));
    }

    @Test
    void constructorRejectsNullStartTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new StudySession(
                        subject,
                        null,
                        endTime,
                        duration));
    }

    @Test
    void constructorRejectsNullEndTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new StudySession(
                        subject,
                        startTime,
                        null,
                        duration));
    }

    @Test
    void constructorRejectsNullDuration() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new StudySession(
                        subject,
                        startTime,
                        endTime,
                        null));
    }

    @Test
    void constructorRejectsEndTimeBeforeStartTime() {

        LocalDateTime invalidEndTime = LocalDateTime.of(2026, 8, 25, 13, 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> new StudySession(
                        subject,
                        startTime,
                        invalidEndTime,
                        duration));
    }

    @Test
    void constructorAllowsEndTimeEqualToStartTime() {

        LocalDateTime equalEndTime = startTime;

        assertDoesNotThrow(
                () -> new StudySession(
                        subject,
                        startTime,
                        equalEndTime,
                        Duration.ZERO));
    }

    @Test
    void constructorAllowsValidSession() {

        assertDoesNotThrow(
                () -> new StudySession(
                        subject,
                        startTime,
                        endTime,
                        duration));
    }
}
