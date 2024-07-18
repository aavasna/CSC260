package com.runtimeTerror.scrumApp;

import com.runtimeTerror.scrumApp.controllers.ArchiveController;
import com.runtimeTerror.scrumApp.controllers.BacklogController;
import com.runtimeTerror.scrumApp.controllers.GroupController;
import com.runtimeTerror.scrumApp.controllers.SprintController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Optional;

import model.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main controller
 * @author Thomas Breimer
 */

public class MainScenesController implements Initializable {

    @FXML
    private Label loggedInAsLabel;

    @FXML
    private TreeView<Sprint> sprintTree;

    @FXML
    private TreeView<SprintArchive> archiveTree;

    @FXML private Parent backlogTabPage;
    @FXML private BacklogController backlogTabPageController;

    @FXML private Parent sprintTabPage;
    @FXML private SprintController sprintTabPageController;

    @FXML private Parent archiveTabPage;
    @FXML private ArchiveController archiveTabPageController;

    @FXML private Parent groupTabPage;
    @FXML private GroupController groupTabPageController;

    private Backlog backlog;
    private Sprint sprint;
    private SprintArchive archive;

    private Alert loadingPopup;

    private Classroom classroom;

    private Main mainApp;

    private String userRole;
    private String username;
    private int userID;

    final Image checkIcon = new Image(getClass().getResourceAsStream("check.png"));
    final Image exIcon = new Image(getClass().getResourceAsStream("ex.png"));

    /**
     * Called by main. Passes in object refs so they can be shared.
     * @param backlog backlog object
     * @param sprint sprint object
     * @param archive archive object
     * @param classroom classroom object
     * @param mainApp ref back to main app
     * @param userRole "student" or "professor"
     * @param username name of student is userRole is "student"
     */
    public void setObjects(Backlog backlog, Sprint sprint, SprintArchive archive, Classroom classroom, Main mainApp, String userRole, int userID) {
        this.backlog = backlog;
        this.sprint = sprint;
        this.archive = archive;
        this.classroom = classroom;
        this.mainApp = mainApp;
        //this.username = username;
        this.userID = userID;

        if (userRole.equals("student")) {
        this.username = DBConnection.getUsernameByID(userID);
        loggedInAsLabel.setText("  Logged in as: " + username + " [ " + Integer.toString(userID) + " ]");
        }
        else{
        loggedInAsLabel.setText("  Logged in as: Professor");
        }

        groupTabPageController.setUserRole(userRole);
        backlogTabPageController.setUserRole(userRole);
        sprintTabPageController.setUserRole(userRole);

        backlogTabPageController.assignObjects(this.backlog, this);
        sprintTabPageController.assignObjects(this.sprint, this);
        sprintTabPageController.assignImages(checkIcon, exIcon);
        archiveTabPageController.assignObjects(this.archive, this);
        archiveTabPageController.assignImages(checkIcon, exIcon);
        groupTabPageController.assignObjects(this.classroom, this);
    }

    /**
     * Sets user role
     * @param userRole "student" or "professor"
     */
    public void setUserRole(String userRole) {
        this.userRole = userRole;

        if (userRole == "student") {
            selectUser(userID);
        }
    }

    /**
     *
     * @return the user's ID (if student). Returns -1 if professor
     */
    public int getUserID(){
        return userID;
    }

    /**
     * When you have selected a task you want to move to sprint you click the add to sprint button.  Task and its
     * subtasks are only added to the sprint if there is no current sprint already active/in progress.
     * @param listID listID of the list selected
     */
    public void addListToSprint(int listID){
        List toAdd = backlog.getList(listID);
        backlog.removeList(listID);
        sprint.addList(toAdd);
        backlogTabPageController.createBackLogTreeView();
        sprintTabPageController.createSprintTreeView();
    }


    /**
     * Once you have decided you are finished with a sprint, and want to move it to the archive you click the finish
     * sprint button which will first ask you to fill out a reflection on the sprint and then will move the sprint to
     * the archive
     */
    public void finishSprint(){

        archive.addSprint(sprint);

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Sprint Review");
        dialog.setHeaderText("Sprint Review");
        dialog.setContentText("Enter a short reflection for your sprint: ");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> sprint.finishSprint(result.get()));

        DBConnection.archiveSprint(userID, sprint.getName(), result.get());

        sprint = new Sprint("Unnamed Sprint", userID);
        sprintTabPageController.assignObjects(sprint, this);

        sprintTabPageController.createSprintTreeView();
        archiveTabPageController.createSprintArchiveTreeView();
    }

    /**
     * Init a backlog, sprint, and sprint archive for the professor view
     * @param studentID
     */
    public void selectUser(int studentID) {
        String stu_username = DBConnection.getUsernameByID(studentID);
        loggedInAsLabel.setText("  Viewing as: " + stu_username );
        setStudentBacklog(studentID);
        setStudentSprint(studentID);
        setStudentArchive(studentID);
    }

    /**
     * Generates the archive object from the DB given a student ID
     * @param studentID
     */
    private void setStudentArchive(int studentID) {
        archive.clear(studentID);

        ArrayList<Integer> archiveIDs = DBConnection.getSprintArchiveIdsByUserid(studentID);

        for (int k = 0; k < archiveIDs.size(); k++) {
            int archiveID = archiveIDs.get(k);

            String sprintName = DBConnection.getSprintArchiveNameByID(archiveID);
            String sprintReflection = DBConnection.getReflectionFromSprintArchiveID(archiveID);

            Sprint thisSprint = new Sprint(sprintName, studentID);
            thisSprint.finishSprint(sprintReflection);

            ArrayList<Integer> sprintListIDs = DBConnection.getTaskIdsInSprintArchive(studentID, archiveID);


            for (int i = 0; i < sprintListIDs.size(); i++) {
                int listID = sprintListIDs.get(i);

                String listName = DBConnection.getTaskNameByID(listID);

                List list = new List(listName, listID);

                ArrayList<Integer> taskIDs = DBConnection.getSubTaskidsforTask(listID);

                for (int j = 0; j < taskIDs.size(); j++) {
                    int taskID = taskIDs.get(j);

                    String taskName = DBConnection.getSubtaskNameByID(taskID);
                    boolean taskChecked = DBConnection.isCheckedSubTask(taskID);

                    list.addTask(taskName, taskID);

                    if (taskChecked){
                        list.toggleTaskChecked(j);
                    }
                }

                thisSprint.addList(list);
            }
            archive.addSprint(thisSprint);
        }

        archiveTabPageController.createSprintArchiveTreeView();
    }

    /**
     * Generates the sprint object from the DB given a student ID
     * @param studentID
     */
    private void setStudentSprint(int studentID) {
        sprint.clear(studentID);

        ArrayList<Integer> sprintListIDs = DBConnection.getTaskIdsInSprint(studentID);
        String sprintName = DBConnection.getCurrentSprintNameByUserID(studentID);

        sprint.changeName(sprintName);

        for (int i = 0; i < sprintListIDs.size(); i++) {
            int listID = sprintListIDs.get(i);

            String listName = DBConnection.getTaskNameByID(listID);

            List list = new List(listName, listID);

            ArrayList<Integer> taskIDs = DBConnection.getSubTaskidsforTask(listID);

            for (int j = 0; j < taskIDs.size(); j++) {
                int taskID = taskIDs.get(j);

                String taskName = DBConnection.getSubtaskNameByID(taskID);
                boolean taskChecked = DBConnection.isCheckedSubTask(taskID);

                list.addTask(taskName, taskID);

                if (taskChecked){
                    list.toggleTaskChecked(j);
                }
            }

            sprint.addList(list);
        }

        sprintTabPageController.createSprintTreeView();
    }


    /**
     * Generates the backlog object from the DB given a student ID
     * @param studentID
     */
    public void setStudentBacklog(int studentID) {
        backlog.clear(studentID);

        ArrayList<Integer> backlogListsID = DBConnection.getTaskIdsInBacklog(studentID);

        for (int i = 0; i < backlogListsID.size(); i++) {
            int listID = backlogListsID.get(i);

            String listName = DBConnection.getTaskNameByID(listID);

            List list = new List(listName, listID);

            ArrayList<Integer> taskIDs = DBConnection.getSubTaskidsforTask(listID);

            for (int j = 0; j < taskIDs.size(); j++) {
                int taskID = taskIDs.get(j);

                String taskName = DBConnection.getSubtaskNameByID(taskID);
                boolean taskChecked = DBConnection.isCheckedSubTask(taskID);


                list.addTask(taskName, taskID);

                if (taskChecked){
                    list.toggleTaskChecked(j);
                }
            }

            backlog.addList(list);
        }

        backlogTabPageController.createBackLogTreeView();
    }

    /**
     * Opens a loading window
     */
    public void openLoadingWindow() {
        loadingPopup = new Alert(Alert.AlertType.INFORMATION);
        loadingPopup.setTitle("Loading");
        loadingPopup.setHeaderText(null);
        loadingPopup.setContentText("Loading information from database... Please wait.");
        loadingPopup.show();
    }

    /**
     * Closes the loading window
     */
    public void closeLoadingWindow() {
        loadingPopup.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
