package studytracker.model;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudyIntervalTest {

    private final LocalDateTime start = LocalDateTime.of(2026, 8, 25, 14, 0);
    private final LocalDateTime end = LocalDateTime.of(2026, 8, 25, 15, 0);

    @Test
    void constructorStoresStartAndEndTimes() {
        StudyInterval interval = new StudyInterval(start, end);

        assertAll(
                () -> assertEquals(start, interval.getStartTime()),
                () -> assertEquals(end, interval.getEndTime()));
    }

    @Test
    void constructorRejectsNullStartTime() {
        assertThrows(NullPointerException.class, () -> new StudyInterval(null, end));
    }

    @Test
    void constructorRejectsNullEndTime() {
        assertThrows(NullPointerException.class, () -> new StudyInterval(start, null));
    }

    @Test
    void constructorRejectsEndBeforeStart() {
        assertThrows(IllegalArgumentException.class, () -> new StudyInterval(end, start));
    }

    @Test
    void constructorAllowsEqualStartAndEndTimes() {
        assertDoesNotThrow(() -> new StudyInterval(start, start));
    }
}
