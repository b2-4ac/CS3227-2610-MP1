package studytracker.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents one uninterrupted period of active study within a session. */
public class StudyInterval {

    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public StudyInterval(
            @JsonProperty("startTime") LocalDateTime startTime,
            @JsonProperty("endTime") LocalDateTime endTime) {
        this.startTime = Objects.requireNonNull(startTime, "Start Time cannot be null");
        this.endTime = Objects.requireNonNull(endTime, "End Time cannot be null");

        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End Time cannot be before Start Time");
        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
