package com.runtimeTerror.scrumApp;

import com.runtimeTerror.scrumApp.controllers.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.*;
import java.io.IOException;

/**
 * Main Class
 * Original Author: Thomas Breimer, Guy Tallent, Kevin Welch
 * Updated Author: Abylay Aitkazy 6/1/2024
 */
public class Main extends Application {
    private MainScenesController controller;
    private Backlog backlog;
    private Sprint sprint;
    private SprintArchive archive;
    private Classroom classroom;
    private String userRole;
    private int userID;
    private Stage primaryStage;


    /**
     * Starts the JavaFX application.
     *
     * @param stage The primary stage for this project
     * @throws IOException throws exception if it occurs
     */
    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        stage.hide(); // Hide the primary stage initially

        showLoginDialog();
    }

    /**
     * Getter for primary stage
     *
     * @return The primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

     /**
     * Sets the role of the user. Called by LoginDialogController upon login
     * @param role "student" or "professor"
     * @param username name of student if logged in as student
     */
    public void setRole(String role, int userID) {
        this.userRole = role;
        this.userID = userID;
        initializeMainWindow();
    }

    /**
     * Getter for the Role
     *
     * @return The user role.
     */
    public String getRole() {
        return userRole;
    }

    /**
     * Displays the login dialog. Loads the login dialog FXML file.
     */
    private void showLoginDialog() {
        try {
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/com/runtimeTerror/scrumApp/loginDialog.fxml"));
            Parent loginRoot = loginLoader.load();
            LoginDialogController loginController = loginLoader.getController();
            loginController.setMain(this);

            Scene loginScene = new Scene(loginRoot);
            Stage loginStage = new Stage();

            loginStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/runtimeTerror/scrumApp/icon.png")));
            loginStage.setScene(loginScene);
            loginStage.setTitle("Login");
            loginStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load loginDialog.fxml: " + e.getMessage());
        }
    }

    /**
     * Starts the main application window. Sets up the backlog, sprint, archive, and classroom objects and
     * loads the main scenes FXML file. Also it passes necessary objects and user information to main scenes controller.
     */
    private void initializeMainWindow() {
        try {
            // Main Window
            this.backlog = new Backlog(0);
            this.sprint = new Sprint("Unnamed Sprint", 0);
            this.archive = new SprintArchive(0);
            this.classroom = new Classroom();
            // Login Window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/runtimeTerror/scrumApp/mainScenes.fxml"));
            Parent root = loader.load();
            controller = loader.getController();

            System.out.println("Loading information from database..");

            controller.setObjects(backlog, sprint, archive, classroom, this, userRole, userID);
            controller.setUserRole(userRole);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setTitle("Runtime Terror Task Manager™");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/runtimeTerror/scrumApp/icon.png")));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load mainScenes.fxml: " + e.getMessage());
        }
    }

    /**
     * Starts the JavaFX application.
     * Without it, application won't start.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
