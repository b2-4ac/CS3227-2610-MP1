package studytracker.model;

/**
 * Represents a Subject that a user is currently studying
 */
public class Subject {
    
    private String name;

    public Subject(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
