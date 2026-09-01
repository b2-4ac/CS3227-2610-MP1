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

    /**
     * Adds a new subject with the specified name and saves the updated subject list.
     *
     * <p>Subject names are compared without regard to letter case. For example,
     * attempting to add {@code "mathematics"} when {@code "Mathematics"} already
     * exists is rejected.</p>
     *
     * @param name name of the subject to add
     * @throws IllegalArgumentException if a subject with the same name already exists
     * @throws IOException if the existing subjects cannot be loaded or the updated
     *         list cannot be saved
     */
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

    /**
     * Deletes the subject at the specified zero-based index and saves the updated
     * subject list.
     *
     * <p>Indices outside the current subject list are treated as invalid. In that
     * case, the stored list is left unchanged and this method returns {@code false}.</p>
     *
     * @param index zero-based index of the subject to delete
     * @return {@code true} if a subject was deleted; {@code false} if the index is
     *         outside the current subject list
     * @throws IOException if the existing subjects cannot be loaded or the updated
     *         list cannot be saved
     */
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
