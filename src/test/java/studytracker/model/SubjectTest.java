package studytracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubjectTest {

    @Test
    void constructorStoresName() {
        Subject subject = new Subject("Computer Science");

        assertEquals("Computer Science", subject.getName());
    }

    @Test
    void setNameReplacesName() {
        Subject subject = new Subject("Mathematics");

        subject.setName("Statistics");

        assertEquals("Statistics", subject.getName());
    }

    @Test
    void toStringReturnsCurrentName() {
        Subject subject = new Subject("Mathematics");
        subject.setName("Statistics");

        assertEquals("Statistics", subject.toString());
    }
}
