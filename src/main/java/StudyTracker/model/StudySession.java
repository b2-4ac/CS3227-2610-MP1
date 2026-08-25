package studytracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a single study session completed by the user.
 */
public class StudySession {

    private Subject subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration elapsedTime;

    public StudySession(Subject subject, LocalDateTime startTime, LocalDateTime endTime, Duration elapsedTime) {
        this.subject = Objects.requireNonNull(subject, "Subject cannot be null");
        this.startTime = Objects.requireNonNull(startTime, "Start Time cannot be null");
        this.endTime = Objects.requireNonNull(endTime, "End Time cannot be null");
        this.elapsedTime = Objects.requireNonNull(elapsedTime, "Elapsed Time cannot be null");

        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End Time cannot be before Start Time");
        }
    }

    public Subject getSubject() {
        return this.subject;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public Duration getDuration() {
        return this.elapsedTime;
    }
}
