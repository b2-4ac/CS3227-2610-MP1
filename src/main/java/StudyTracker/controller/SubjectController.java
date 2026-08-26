package studytracker.controller;

import studytracker.model.Subject;
import studytracker.storage.SubjectStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SubjectController {

    private final SubjectStorage storage;

    public SubjectController(SubjectStorage storage) {
        this.storage = storage;
    }

    public List<Subject> getSubjects()
            throws IOException {

        return storage.loadAll();
    }

    public void addSubject(String name) throws IOException {

        List<Subject> subjects = new ArrayList<>(storage.loadAll());

        Subject newSubject = new Subject(name);

        boolean alreadyExists = subjects.stream()
                .anyMatch(subject -> subject.getName()
                        .equalsIgnoreCase(
                                newSubject.getName()));

        if (alreadyExists) {
            throw new IllegalArgumentException(
                    "Subject already exists.");
        }

        subjects.add(newSubject);

        storage.saveAll(subjects);
    }

    public boolean deleteSubject(int index) throws IOException {

        List<Subject> subjects = new ArrayList<>(storage.loadAll());

        if (index < 0 || index >= subjects.size()) {
            return false;
        }

        subjects.remove(index);

        storage.saveAll(subjects);

        return true;
    }
}
