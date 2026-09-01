package studytracker.ui;

import java.io.IOException;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import studytracker.controller.SubjectController;
import studytracker.model.Subject;

public class SubjectViewController {

    @FXML private TextField newSubjectNameField;
    @FXML private Button deleteSubjectButton;
    @FXML private TableView<Subject> subjectsTable;
    @FXML private TableColumn<Subject, String> subjectNameColumn;

    private SubjectController subjectController;

    /**
     * Configures the subject-name table column and keeps the delete action available only while
     * a subject is selected.
     *
     * <p>This method is invoked automatically after the FXML fields have been injected.</p>
     */
    @FXML
    private void initialize() {
        subjectNameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getName()));
        subjectsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        subjectsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldSelection, newSelection) ->
                        deleteSubjectButton.setDisable(newSelection == null));
    }

    /**
     * Supplies the controller used to manage subjects and performs the initial table load.
     *
     * @param subjectController the controller that creates, retrieves, and deletes subjects
     */
    public void configure(SubjectController subjectController) {
        this.subjectController = subjectController;
        refreshSubjects();
    }

    /**
     * Adds the trimmed name entered by the user as a subject and refreshes the displayed list.
     *
     * <p>Blank names, duplicate names, and failures while saving are reported to the user. The
     * input field is cleared only after a subject has been added successfully.</p>
     */
    @FXML
    private void addSubject() {
        String subjectName = newSubjectNameField.getText().trim();
        if (subjectName.isEmpty()) {
            UiSupport.showError("Enter a subject", "A subject name cannot be empty.");
            return;
        }

        try {
            subjectController.addSubject(subjectName);
            newSubjectNameField.clear();
            refreshSubjects();
        } catch (IllegalArgumentException exception) {
            UiSupport.showError("Unable to add subject", exception.getMessage());
        } catch (IOException exception) {
            UiSupport.showError("Unable to save subject", exception.getMessage());
        }
    }

    /**
     * Deletes the currently selected subject and refreshes the displayed list.
     *
     * <p>If no table row is selected, this method performs no action. A storage failure is
     * reported to the user.</p>
     */
    @FXML
    private void deleteSelectedSubject() {
        int index = subjectsTable.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            return;
        }

        try {
            subjectController.deleteSubject(index);
            refreshSubjects();
        } catch (IOException exception) {
            UiSupport.showError("Unable to delete subject", exception.getMessage());
        }
    }

    /**
     * Loads all subjects from the subject controller into the table and clears the delete
     * selection state.
     *
     * <p>If the subject list cannot be loaded, an error is shown to the user.</p>
     */
    private void refreshSubjects() {
        try {
            subjectsTable.setItems(FXCollections.observableArrayList(subjectController.getSubjects()));
            deleteSubjectButton.setDisable(true);
        } catch (IOException exception) {
            UiSupport.showError("Unable to load subjects", exception.getMessage());
        }
    }
}
