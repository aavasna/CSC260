package com.runtimeTerror.scrumApp.controllers;

import javafx.scene.control.PasswordField;
import com.runtimeTerror.scrumApp.DBConnection;
import com.runtimeTerror.scrumApp.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Student Login Controller
 *
 * @author Abylay Aitkazy
 * @version 6/02/24
 */
public class StudentLoginController {

  public TextField usernameField;
  public PasswordField passwordField;
  private Main mainApp;

  //Main application
  public void setMain(Main main) {
      this.mainApp = main;
  }

  /**
   * Handles the login action for a student. Validates the entered username
   * and password and logs in if verified. Gives an error if the input is invalid,
   * user does not exit, or username and password is incorrect.
   */
  public void loginAsStudent() {
    String usernameText = usernameField.getText().trim();
    String passwordText = passwordField.getText().trim();

    // check if the input is valid
    if (usernameText.isEmpty() || passwordText.isEmpty()) {
        showAlert("Login Error", "Please enter a Username or Password.");
        return;
    }

    // check if the username exists
    if (!DBConnection.usernameExists(usernameText)) {
      showAlert("Login Error", "User does not exist.");
      return;
    }

    // get the encrypted password stored in the database to verify with the input password
    String hashedPassword = DBConnection.getPasswordByUsername(usernameText);

    // log in as student if successfully verified
    if(DBConnection.verifyPassword(passwordText, hashedPassword)){
      int userID = DBConnection.getUserIDbyUsername(usernameText);
      mainApp.setRole("student", userID);
      closeCurrentWindow();
      mainApp.getPrimaryStage().show();
    }

    else{
    showAlert("Login Error", "Please enter a valid Username or Password.");
    return;
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
      Stage stage = (Stage) usernameField.getScene().getWindow();
      stage.close();
  }
}
