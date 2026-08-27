package studytracker.storage;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import studytracker.model.Subject;

import static org.junit.jupiter.api.Assertions.*;

class SubjectStorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void loadAllReturnsEmptyListWhenFileDoesNotExist() throws Exception {
        SubjectStorage storage = new SubjectStorage(
                temporaryDirectory.resolve("missing-subjects.json"));

        assertTrue(storage.loadAll().isEmpty());
    }

    @Test
    void saveAllCreatesParentDirectoriesAndRoundTripsSubjects() throws Exception {
        Path file = temporaryDirectory.resolve("nested").resolve("subjects.json");
        SubjectStorage storage = new SubjectStorage(file);
        List<Subject> subjects = List.of(new Subject("Computer Science"), new Subject("Mathematics"));

        storage.saveAll(subjects);

        assertTrue(Files.exists(file));
        assertEquals(List.of("Computer Science", "Mathematics"),
                storage.loadAll().stream().map(Subject::getName).toList());
    }

    @Test
    void saveAllReplacesPreviouslyStoredSubjects() throws Exception {
        SubjectStorage storage = new SubjectStorage(temporaryDirectory.resolve("subjects.json"));

        storage.saveAll(List.of(new Subject("Computer Science"), new Subject("Physics")));
        storage.saveAll(List.of(new Subject("History")));

        assertEquals(List.of("History"),
                storage.loadAll().stream().map(Subject::getName).toList());
    }

    @Test
    void loadAllDeserializesSubjectFixture() throws Exception {
        SubjectStorage storage = new SubjectStorage(fixturePath("subjects.json"));

        assertEquals(
                List.of(
                        "Computer Science",
                        "Mathematics",
                        "Physics",
                        "History",
                        "Literature",
                        "Japanese",
                        "Subject To Delete"),
                storage.loadAll().stream().map(Subject::getName).toList());
    }

    private Path fixturePath(String fileName) throws URISyntaxException {
        return Path.of(getClass().getResource("/studytracker/fixtures/" + fileName).toURI());
    }
}
