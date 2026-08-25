package studytracker.model;

/**
 * Represents a Subject that a user is currently studying
 */
public class Subject {
    
    private String name;

    // Empty Constructor for Jackson
    public Subject() {
    }

    public Subject(String name) {
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
