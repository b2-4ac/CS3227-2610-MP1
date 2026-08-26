package studytracker.controller;

import studytracker.model.StudySession;
import studytracker.storage.StudySessionStorage;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsController {

    private final StudySessionStorage storage;

    public StatisticsController(StudySessionStorage storage) {
        this.storage = storage;
    }
    
    public Duration getTotalStudyTime(LocalDate startDate, LocalDate endDate) throws IOException {

        Duration total = Duration.ZERO;

        List<StudySession> sessions = retrieveSessions();

        for (StudySession session : sessions) {

            LocalDate sessionDate = session.getStartTime().toLocalDate();

            boolean isWithinRange = !sessionDate.isBefore(startDate) && !sessionDate.isAfter(endDate);

            if (!isWithinRange) {
                continue;
            }

            total = total.plus(session.getDuration());
        }

        return total;
    }

    public Map<String, Duration> getStudyTimeBySubject(LocalDate startDate, LocalDate endDate) throws IOException {

        Map<String, Duration> studyTimeBySubject = new HashMap<>();

        List<StudySession> sessions = retrieveSessions();

        for (StudySession session : sessions) {

            LocalDate sessionDate = session.getStartTime().toLocalDate();

            boolean isWithinRange = !sessionDate.isBefore(startDate) && !sessionDate.isAfter(endDate);

            if (!isWithinRange) {
                continue;
            }

            String subjectName = session.getSubject().getName();
            Duration currentDuration = studyTimeBySubject.getOrDefault(subjectName, Duration.ZERO);
            Duration updatedDuration = currentDuration.plus(session.getDuration());

            studyTimeBySubject.put(subjectName, updatedDuration);
        }

        return studyTimeBySubject;
    }

    private List<StudySession> retrieveSessions() throws IOException {
        return storage.loadAll();
    }

}
