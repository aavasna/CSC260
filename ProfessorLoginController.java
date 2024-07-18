package com.runtimeTerror.scrumApp.controllers;
import com.runtimeTerror.scrumApp.DBConnection;

import javafx.scene.input.MouseEvent;
import com.runtimeTerror.scrumApp.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;


/**
 * Professor Login Controller
 *
 * @author Abylay Aitkazy
 * @version 6/02/24
 */
public class ProfessorLoginController {

    public PasswordField passwordField;
    private Main mainApp;

      //Main application
    public void setMain(Main main) {
        this.mainApp = main;
    }

    public void loginAsProfessor() {
        String password = passwordField.getText().trim();

        // add a default professor if not in the database
        if (!DBConnection.professorExists()){
            String hashedPassword = DBConnection.hashPassword("unionprof");
            DBConnection.makeUser(0, "Professor", "prof1", "Untitled Sprint", hashedPassword, -1);
        }

        // get the encrypted password from the database
        String hashedPassword = DBConnection.getPasswordByID(DBConnection.getUserIDByName("Professor"));

        // verify professor password; login if correct
        if (DBConnection.verifyPassword(password, hashedPassword)) {
            mainApp.setRole("professor", 0);
            closeCurrentWindow();
            mainApp.getPrimaryStage().show();
        } else {
            showAlert("Login Error", "Invalid password. Try again.");
        }
    }

    /**
   * Forgot password method yet to be implemented in future versions.
   * IDEA: If the user forgets the password and clicks the "Forgot Password"
   * button, it should send a link to user to reset the password.
   */
  public void forgotPassword(){
    System.out.println("Forgot Password Method yet to be implemented.");
    return;
  }

    /**
     * Shows an alert dialog
     *
     * @param title The name of the alert
     * @param content The  message content
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

      /**
     * Closes the current login window.
     */
    private void closeCurrentWindow() {
        Stage stage = (Stage) passwordField.getScene().getWindow();
        stage.close();
    }
}
