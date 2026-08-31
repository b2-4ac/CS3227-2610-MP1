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

    @FXML
    private void initialize() {
        subjectNameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getName()));
        subjectsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        subjectsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldSelection, newSelection) ->
                        deleteSubjectButton.setDisable(newSelection == null));
    }

    public void configure(SubjectController subjectController) {
        this.subjectController = subjectController;
        refreshSubjects();
    }

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

    private void refreshSubjects() {
        try {
            subjectsTable.setItems(FXCollections.observableArrayList(subjectController.getSubjects()));
            deleteSubjectButton.setDisable(true);
        } catch (IOException exception) {
            UiSupport.showError("Unable to load subjects", exception.getMessage());
        }
    }
}
