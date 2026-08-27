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

    public StatisticsController(StudySessionStorage storage) {
        this.storage = storage;
    }
    
    public Duration getTotalStudyTime(LocalDate startDate, LocalDate endDate) throws IOException {

        return getStudyTimeBySubject(startDate, endDate).values().stream()
                .reduce(Duration.ZERO, Duration::plus);
    }

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

    private List<StudySession> retrieveSessions() throws IOException {
        return storage.loadAll();
    }

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
