package studytracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Subject that a user is currently studying
 */
public class Subject {
    
    private String name;

    public Subject(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
