package studytracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single study session completed by the user.
 */
public class StudySession {

    private Subject subject;
    private List<StudyInterval> intervals;

    public StudySession(
            @JsonProperty("subject") Subject subject,
            @JsonProperty("intervals") List<StudyInterval> intervals) {
        this.subject = Objects.requireNonNull(subject, "Subject cannot be null");
        this.intervals = List.copyOf(Objects.requireNonNull(intervals, "Intervals cannot be null"));

        if (this.intervals.isEmpty()) {
            throw new IllegalArgumentException("A session must contain at least one study interval");
        }
    }

    public Subject getSubject() {
        return this.subject;
    }

    @JsonIgnore
    public LocalDateTime getStartTime() {
        return intervals.stream()
                .map(StudyInterval::getStartTime)
                .min(Comparator.naturalOrder())
                .orElseThrow();
    }

    @JsonIgnore
    public LocalDateTime getEndTime() {
        return intervals.stream()
                .map(StudyInterval::getEndTime)
                .max(Comparator.naturalOrder())
                .orElseThrow();
    }

    @JsonIgnore
    public Duration getDuration() {
        return intervals.stream()
                .map(interval -> Duration.between(interval.getStartTime(), interval.getEndTime()))
                .reduce(Duration.ZERO, Duration::plus);
    }

    public List<StudyInterval> getIntervals() {
        return intervals;
    }
}
