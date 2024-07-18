package com.runtimeTerror.scrumApp.controllers;

import com.runtimeTerror.scrumApp.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


/**
 * Start LoginDialog Controller
 *
 * @author Abylay Aitkazy
 * @version 6/02/24
 */
public class LoginDialogController {

    private Main mainApp;

    //Main application
    public void setMain(Main main) {
        this.mainApp = main;
    }

    /**
     * Shows the Student Login
     */
    public void showStudentLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/runtimeTerror/scrumApp/studentLogin.fxml"));
            Parent root = loader.load();
            StudentLoginController studentLoginController = loader.getController();
            studentLoginController.setMain(mainApp);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));


            stage.setTitle("Student Login");
            stage.show();
            //closeCurrentWindow(); //causing an error
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the Professor Login
     */
    public void showProfessorLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/runtimeTerror/scrumApp/professorLogin.fxml"));
            Parent root = loader.load();
            ProfessorLoginController professorLoginController = loader.getController();
            professorLoginController.setMain(mainApp);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Professor Login");
            stage.show();
            //closeCurrentWindow(); //causing an error
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     /**
     * Closes the current login window.
     */
    private void closeCurrentWindow() {
        Stage stage = (Stage) mainApp.getPrimaryStage().getScene().getWindow();
        stage.close();
    }
}
