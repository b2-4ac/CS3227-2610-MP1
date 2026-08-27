package studytracker.controller;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import studytracker.model.Subject;
import studytracker.storage.SubjectStorage;

import static org.junit.jupiter.api.Assertions.*;

class SubjectControllerTest {

    private static final String SUBJECT_TO_DELETE = "Subject To Delete";

    @TempDir
    Path temporaryDirectory;

    private SubjectController controller;

    @BeforeEach
    void setUp() throws Exception {
        Path testSubjectsFile = temporaryDirectory.resolve("subjects.json");
        Files.copy(fixturePath("subjects.json"), testSubjectsFile);
        controller = new SubjectController(new SubjectStorage(testSubjectsFile));
    }

    @Test
    void addSubjectAddsAndPersistsNewSubject() throws Exception {
        controller.addSubject("Economics");

        assertTrue(subjectNames().contains("Economics"));
    }

    @Test
    void addSubjectRejectsExistingNameRegardlessOfCase() throws Exception {
        List<String> originalSubjects = subjectNames();

        assertThrows(IllegalArgumentException.class, () -> controller.addSubject("computer science"));

        assertEquals(originalSubjects, subjectNames());
    }

    @Test
    void deleteSubjectRemovesDummySubjectAndPersistsChange() throws Exception {
        int subjectIndex = subjectNames().indexOf(SUBJECT_TO_DELETE);

        boolean wasDeleted = controller.deleteSubject(subjectIndex);

        assertAll(
                () -> assertTrue(wasDeleted),
                () -> assertFalse(subjectNames().contains(SUBJECT_TO_DELETE)));
    }

    @Test
    void deleteSubjectRejectsIndicesOutsideSubjectList() throws Exception {
        List<String> originalSubjects = subjectNames();

        assertAll(
                () -> assertFalse(controller.deleteSubject(-1)),
                () -> assertFalse(controller.deleteSubject(originalSubjects.size())),
                () -> assertEquals(originalSubjects, subjectNames()));
    }

    private List<String> subjectNames() throws Exception {
        return controller.getSubjects().stream().map(Subject::getName).toList();
    }

    private Path fixturePath(String fileName) throws URISyntaxException {
        return Path.of(getClass().getResource("/studytracker/fixtures/" + fileName).toURI());
    }
}
