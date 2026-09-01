package studytracker.controller;

import studytracker.model.StudyInterval;
import studytracker.model.StudySession;
import studytracker.storage.StudySessionStorage;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsController {

    private final StudySessionStorage storage;

    /**
     * Creates a statistics controller that reads completed study sessions from the
     * specified storage.
     *
     * @param storage storage used to load completed study sessions
     */
    public StatisticsController(StudySessionStorage storage) {
        this.storage = storage;
    }
    
    /**
     * Calculates the total active study time within an inclusive date range.
     *
     * <p>Each active {@link StudyInterval} is clipped to the period from the start
     * of {@code startDate} through, but not including, the day after {@code endDate}.
     * This ensures that active study intervals crossing midnight contribute only the
     * portion that falls within the requested dates. Paused periods do not contribute
     * because they are not represented by study intervals.</p>
     *
     * @param startDate first date to include in the calculation
     * @param endDate last date to include in the calculation
     * @return total duration of all active interval portions within the date range,
     *         or {@link Duration#ZERO} when no active intervals overlap it
     * @throws IOException if completed sessions cannot be loaded from storage
     */
    public Duration getTotalStudyTime(LocalDate startDate, LocalDate endDate) throws IOException {

        return getStudyTimeBySubject(startDate, endDate).values().stream()
                .reduce(Duration.ZERO, Duration::plus);
    }

    /**
     * Calculates active study time per subject within an inclusive date range.
     *
     * <p>For every saved session, this method calculates the overlap between each
     * active interval and the requested period. It then adds that overlap to the
     * session subject's total. Subjects without overlapping active intervals are not
     * included in the returned map.</p>
     *
     * @param startDate first date to include in the calculation
     * @param endDate last date to include in the calculation
     * @return a map from subject name to total active duration within the date range
     * @throws IOException if completed sessions cannot be loaded from storage
     */
    public Map<String, Duration> getStudyTimeBySubject(LocalDate startDate, LocalDate endDate) throws IOException {

        Map<String, Duration> studyTimeBySubject = new HashMap<>();

        List<StudySession> sessions = retrieveSessions();

        for (StudySession session : sessions) {

            String subjectName = session.getSubject().getName();
            for (StudyInterval interval : session.getIntervals()) {
                Duration overlap = getOverlap(interval, startDate, endDate);
                if (!overlap.isZero()) {
                    Duration currentDuration = studyTimeBySubject.getOrDefault(subjectName, Duration.ZERO);
                    studyTimeBySubject.put(subjectName, currentDuration.plus(overlap));
                }
            }
        }

        return studyTimeBySubject;
    }

    /**
     * Loads every completed study session used by statistics calculations.
     *
     * @return all completed sessions currently stored
     * @throws IOException if the session storage cannot be read
     */
    private List<StudySession> retrieveSessions() throws IOException {
        return storage.loadAll();
    }

    /**
     * Calculates how much of one active interval falls within an inclusive date range.
     *
     * <p>The user-facing inclusive range is represented internally as the half-open
     * interval {@code [startDate at 00:00, day after endDate at 00:00)}. A half-open
     * interval prevents time exactly at the following midnight from being counted in
     * both adjacent date ranges.</p>
     *
     * @param interval active study interval to compare with the requested range
     * @param startDate first included date
     * @param endDate last included date
     * @return the duration shared by the study interval and requested date range, or
     *         {@link Duration#ZERO} when they do not overlap
     */
    private Duration getOverlap(StudyInterval interval, LocalDate startDate, LocalDate endDate) {
        LocalDateTime rangeStart = startDate.atStartOfDay();
        LocalDateTime rangeEnd = endDate.plusDays(1).atStartOfDay();

        LocalDateTime overlapStart = interval.getStartTime().isAfter(rangeStart)
                ? interval.getStartTime()
                : rangeStart;
        LocalDateTime overlapEnd = interval.getEndTime().isBefore(rangeEnd)
                ? interval.getEndTime()
                : rangeEnd;

        return overlapEnd.isAfter(overlapStart)
                ? Duration.between(overlapStart, overlapEnd)
                : Duration.ZERO;
    }

}
