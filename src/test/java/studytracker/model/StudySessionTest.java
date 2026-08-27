package studytracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudySessionTest {

    private final Subject subject = new Subject("Computer Science");
    private final StudyInterval firstInterval = new StudyInterval(
            LocalDateTime.of(2026, 8, 25, 14, 0),
            LocalDateTime.of(2026, 8, 25, 14, 30));
    private final StudyInterval secondInterval = new StudyInterval(
            LocalDateTime.of(2026, 8, 25, 14, 45),
            LocalDateTime.of(2026, 8, 25, 15, 30));

    @Test
    void constructorRejectsNullSubject() {
        assertThrows(NullPointerException.class, () -> new StudySession(null, List.of(firstInterval)));
    }

    @Test
    void constructorRejectsNullIntervals() {
        assertThrows(NullPointerException.class, () -> new StudySession(subject, null));
    }

    @Test
    void constructorRejectsEmptyIntervals() {
        assertThrows(IllegalArgumentException.class, () -> new StudySession(subject, List.of()));
    }

    @Test
    void gettersDeriveSessionValuesFromIntervals() {
        StudySession session = new StudySession(subject, List.of(firstInterval, secondInterval));

        assertAll(
                () -> assertSame(subject, session.getSubject()),
                () -> assertEquals(List.of(firstInterval, secondInterval), session.getIntervals()),
                () -> assertEquals(firstInterval.getStartTime(), session.getStartTime()),
                () -> assertEquals(secondInterval.getEndTime(), session.getEndTime()),
                () -> assertEquals(Duration.ofMinutes(75), session.getDuration()));
    }

    @Test
    void derivedTimesUseEarliestStartAndLatestEnd() {
        StudySession session = new StudySession(subject, List.of(secondInterval, firstInterval));

        assertAll(
                () -> assertEquals(firstInterval.getStartTime(), session.getStartTime()),
                () -> assertEquals(secondInterval.getEndTime(), session.getEndTime()));
    }

    @Test
    void intervalsCannotBeModifiedThroughGetter() {
        StudySession session = new StudySession(subject, List.of(firstInterval));

        assertThrows(UnsupportedOperationException.class, () -> session.getIntervals().add(secondInterval));
    }
}
