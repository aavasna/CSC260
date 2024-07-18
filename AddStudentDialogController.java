package com.runtimeTerror.scrumApp.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.runtimeTerror.scrumApp.DBConnection;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller for the add student dialog
 *
 * @author Thomas Breimer
 * @version 5/29/23
 */

public class AddStudentDialogController {

    public TextField nameField;
    public TextField usernameField;
    public TextField passwordField;
    public Button addButton;
    public Button cancelButton;
    private GroupController groupController;

    private int groupPosition;

    /**
     * Sets the group controller
     * @param groupController the group controller
     */
    public void setMainController(GroupController groupController, int groupPosition) {
        this.groupController = groupController;
        this.groupPosition = groupPosition;
    }

    /**
     * Calls the group controller to add a new student when the add student button is clicked
     * @param mouseEvent
     */
    public void addNewStudent(MouseEvent mouseEvent) {
        String studentUsername = usernameField.getText();
        if(!studentUsername.isEmpty()){
          if (!DBConnection.validUsername(studentUsername)) {
            // Display error message about non-unique ID
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("New Student Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a unique username for student.");
            alert.showAndWait();
        } else {
        // Existing code to add the student
        groupController.addNewStudent(nameField.getText(), studentUsername, passwordField.getText(), groupPosition); //
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
      }
    }
 // Display error message about empty student name/ID
  else{
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("New Student Error");
    alert.setHeaderText(null);
    alert.setContentText("Please enter a student name/ID.");
    alert.showAndWait();
  }
}

    /**
     * Closes the window when the cancel button is clicked
     * @param mouseEvent
     */
    public void close(MouseEvent mouseEvent) {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
