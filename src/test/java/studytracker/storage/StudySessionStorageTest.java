package studytracker.storage;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import studytracker.model.StudySession;
import studytracker.model.Subject;

import static org.junit.jupiter.api.Assertions.*;

class StudySessionStorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void loadAllReturnsEmptyListWhenFileDoesNotExist() throws Exception {
        StudySessionStorage storage = new StudySessionStorage(
                temporaryDirectory.resolve("missing-sessions.json"));

        assertTrue(storage.loadAll().isEmpty());
    }

    @Test
    void saveCreatesParentDirectoriesAndRoundTripsSession() throws Exception {
        Path file = temporaryDirectory.resolve("nested").resolve("sessions.json");
        StudySessionStorage storage = new StudySessionStorage(file);
        StudySession session = session("Computer Science", 9, Duration.ofMinutes(90));

        storage.save(session);

        assertTrue(Files.exists(file));
        assertSessionEquals(session, storage.loadAll().getFirst());
    }

    @Test
    void saveAppendsToPreviouslyStoredSessions() throws Exception {
        StudySessionStorage storage = new StudySessionStorage(
                temporaryDirectory.resolve("sessions.json"));
        StudySession first = session("Computer Science", 9, Duration.ofHours(1));
        StudySession second = session("Mathematics", 14, Duration.ofMinutes(45));

        storage.save(first);
        storage.save(second);

        List<StudySession> sessions = storage.loadAll();
        assertEquals(2, sessions.size());
        assertSessionEquals(first, sessions.get(0));
        assertSessionEquals(second, sessions.get(1));
    }

    @Test
    void loadAllDeserializesSessionFixture() throws Exception {
        StudySessionStorage storage = new StudySessionStorage(fixturePath("sessions.json"));

        List<StudySession> sessions = storage.loadAll();

        assertEquals(16, sessions.size());

        StudySession leapDaySession = sessions.getFirst();
        assertAll(
                () -> assertEquals("Computer Science", leapDaySession.getSubject().getName()),
                () -> assertEquals(LocalDateTime.of(2024, 2, 29, 9, 0), leapDaySession.getStartTime()),
                () -> assertEquals(LocalDateTime.of(2024, 2, 29, 10, 30), leapDaySession.getEndTime()),
                () -> assertEquals(Duration.ofMinutes(90), leapDaySession.getDuration()));
    }

    private StudySession session(String subjectName, int startHour, Duration duration) {
        LocalDateTime start = LocalDateTime.of(2026, 8, 26, startHour, 0);
        return new StudySession(
                new Subject(subjectName),
                start,
                start.plus(duration),
                duration);
    }

    private Path fixturePath(String fileName) throws URISyntaxException {
        return Path.of(getClass().getResource("/studytracker/fixtures/" + fileName).toURI());
    }

    private void assertSessionEquals(StudySession expected, StudySession actual) {
        assertAll(
                () -> assertEquals(expected.getSubject().getName(), actual.getSubject().getName()),
                () -> assertEquals(expected.getStartTime(), actual.getStartTime()),
                () -> assertEquals(expected.getEndTime(), actual.getEndTime()),
                () -> assertEquals(expected.getDuration(), actual.getDuration()));
    }
}
