package studytracker.controller;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import studytracker.model.StudyInterval;
import studytracker.model.StudySession;
import studytracker.model.Subject;
import studytracker.storage.StudySessionStorage;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsControllerTest {

    @TempDir
    Path temporaryDirectory;

    private StatisticsController controller;

    @BeforeEach
    void setUp() throws Exception {
        StudySessionStorage storage = new StudySessionStorage(fixturePath("sessions.json"));
        controller = new StatisticsController(storage);
    }

    @Test
    void calculatesTotalAndSubjectBreakdownForSingleDay() throws Exception {
        LocalDate date = LocalDate.of(2026, 8, 26);

        assertAll(
                () -> assertEquals(Duration.ofHours(3).plusMinutes(30),
                        controller.getTotalStudyTime(date, date)),
                () -> assertEquals(
                        Map.of(
                                "Computer Science", Duration.ofMinutes(90),
                                "Physics", Duration.ofHours(2)),
                        controller.getStudyTimeBySubject(date, date)));
    }

    @Test
    void includesBothDatesAtRangeBoundaries() throws Exception {
        LocalDate startDate = LocalDate.of(2026, 8, 26);
        LocalDate endDate = LocalDate.of(2026, 8, 27);

        assertAll(
                () -> assertEquals(Duration.ofHours(4).plusMinutes(30),
                        controller.getTotalStudyTime(startDate, endDate)),
                () -> assertEquals(
                        Map.of(
                                "Computer Science", Duration.ofMinutes(90),
                                "Physics", Duration.ofHours(2),
                                "Mathematics", Duration.ofHours(1)),
                        controller.getStudyTimeBySubject(startDate, endDate)));
    }

    @Test
    void returnsNoStatisticsForPeriodWithoutActiveIntervals() throws Exception {
        LocalDate date = LocalDate.of(2026, 4, 1);

        assertAll(
                () -> assertEquals(Duration.ZERO, controller.getTotalStudyTime(date, date)),
                () -> assertTrue(controller.getStudyTimeBySubject(date, date).isEmpty()));
    }

    @Test
    void countsOnlyActiveIntervalsThatOverlapRequestedDateAcrossMidnight() throws Exception {
        LocalDate date = LocalDate.of(2027, 1, 1);

        assertAll(
                () -> assertEquals(Duration.ofHours(1), controller.getTotalStudyTime(date, date)),
                () -> assertEquals(
                        Map.of(
                                "Literature", Duration.ofMinutes(30),
                                "History", Duration.ofMinutes(30)),
                        controller.getStudyTimeBySubject(date, date)));
    }

    @Test
    void excludesThePausedGapInCrossMidnightSession() throws Exception {
        LocalDate date = LocalDate.of(2026, 12, 31);

        assertAll(
                () -> assertEquals(Duration.ofMinutes(15), controller.getTotalStudyTime(date, date)),
                () -> assertEquals(
                        Map.of("Literature", Duration.ofMinutes(15)),
                        controller.getStudyTimeBySubject(date, date)));
    }

    @Test
    void countsOnlyTheRequestedDatePortionOfAnActiveIntervalCrossingMidnight() throws Exception {
        StudySessionStorage storage = new StudySessionStorage(
                temporaryDirectory.resolve("sessions.json"));
        storage.save(new StudySession(
                new Subject("Japanese"),
                List.of(new StudyInterval(
                        LocalDateTime.of(2026, 8, 23, 23, 0),
                        LocalDateTime.of(2026, 8, 24, 0, 30)))));
        StatisticsController temporaryController = new StatisticsController(storage);
        LocalDate date = LocalDate.of(2026, 8, 24);

        assertAll(
                () -> assertEquals(Duration.ofMinutes(30),
                        temporaryController.getTotalStudyTime(date, date)),
                () -> assertEquals(
                        Map.of("Japanese", Duration.ofMinutes(30)),
                        temporaryController.getStudyTimeBySubject(date, date)));
    }

    @Test
    void aggregatesAllIntervalsAcrossMultiYearRange() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 12, 31);
        LocalDate endDate = LocalDate.of(2026, 1, 15);

        assertAll(
                () -> assertEquals(Duration.ofHours(3).plusMinutes(45),
                        controller.getTotalStudyTime(startDate, endDate)),
                () -> assertEquals(
                        Map.of(
                                "History", Duration.ofMinutes(45),
                                "Computer Science", Duration.ofHours(1),
                                "Mathematics", Duration.ofHours(2)),
                        controller.getStudyTimeBySubject(startDate, endDate)));
    }

    private Path fixturePath(String fileName) throws URISyntaxException {
        return Path.of(getClass().getResource("/studytracker/fixtures/" + fileName).toURI());
    }
}
