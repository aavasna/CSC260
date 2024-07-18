package com.runtimeTerror.scrumApp.controllers;

import com.runtimeTerror.scrumApp.DBConnection;
import com.runtimeTerror.scrumApp.MainScenesController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Controller for group tab
 *
 * @author Thomas Breimer
 * @version 5/29/23
 */

public class GroupController {

    public ButtonBar buttonBar;
    public Button newGroupButton;
    public Button newStudentButton;
    public Button deleteButton;
    public Button viewStudentButton;
    private Classroom classroom;
    private String role;

    private MainScenesController mainScenesController;

    @FXML
    private TreeView<String> groupTree;
    private String userRole;

    public void assignObjects(Classroom classroom, MainScenesController mainScenesController){
        this.classroom = classroom;
        this.role = role;
        this.mainScenesController = mainScenesController;
        createGroupTreeView();
    }

    /**
     * Sets the user role. Called from the main app.
     * @param userRole Either "student" or "professor"
     */
    public void setUserRole(String userRole) {
        this.userRole = userRole;

        if (userRole == "student") {
            newGroupButton.setVisible(false);
            deleteButton.setVisible(false);
            viewStudentButton.setVisible(false);
            newStudentButton.setVisible(false);
        }
    }

    /**
     * Creates the group tree view depending on whether the student is a professor or student
     */
    public void createGroupTreeView() {
        if (this.userRole == "professor"){
            createProfessorTreeView();
        } else {
            createStudentTreeView();
        }
    }

    /**
     * Creates the student's tree view
     */
    private void createStudentTreeView() {
        getClassroomObject();

        TreeItem<String> rootItem = new TreeItem<>("###");

        for (int i = 0; i < classroom.size(); i++) {

            Group group = classroom.getGroup(i);

            TreeItem<String> treeGroup = new TreeItem<String>(group.getName());

            for (int j = 0; j < group.getSize(); j++) {
                Student student = group.getStudent(j);
                TreeItem<String> treeStudent = new TreeItem<String>(student.getName());
                treeGroup.getChildren().add(treeStudent);

                Backlog studentBacklog = student.getBacklog();

                TreeItem<String> treeBacklog = new TreeItem<String>("Backlog");
                treeStudent.getChildren().add(treeBacklog);

                for (int k = 0; k < studentBacklog.getLength(); k++) {

                    List list = studentBacklog.getList(k);

                    TreeItem<String> treeList = new TreeItem<>(list.getName());
                    treeBacklog.getChildren().addAll(treeList);
                }

                Sprint studentSprint = student.getSprint();

                TreeItem<String> treeSprint = new TreeItem<String>("Sprint: " + studentSprint.getName());
                treeStudent.getChildren().add(treeSprint);

                for (int k = 0; k < studentSprint.getLength(); k++) {

                    List list = studentSprint.getList(k);

                    TreeItem<String> treeList = new TreeItem<>(list.getName());
                    treeSprint.getChildren().addAll(treeList);
                }
            }

            rootItem.getChildren().add(treeGroup);
        }

        groupTree.setRoot(rootItem);
        groupTree.setShowRoot(false);
    }

    /**
     * Creates the professor tree view if the user is the professor
     */
    private void createProfessorTreeView() {
        getClassroomObject();

        TreeItem<String> rootItem = new TreeItem<>("###");

        for (int i = 0; i < classroom.size(); i++) {

            Group group = classroom.getGroup(i);

            TreeItem<String> treeGroup = new TreeItem<String>(group.getName());

            for (int j = 0; j < group.getSize(); j++) {
                Student student = group.getStudent(j);
                TreeItem<String> treeStudent = new TreeItem<String>(student.getName() + ", " + "[ "+ student.getUsername() +" ]");
                treeGroup.getChildren().add(treeStudent);
            }

            rootItem.getChildren().add(treeGroup);
        }

        groupTree.setRoot(rootItem);
        groupTree.setShowRoot(false);
    }

    /**
     * Gets the classroom object given the user role
     */
    private void getClassroomObject() {
        if (userRole == "professor") {
            getClassroomObjectProfessor();
        } else {
            getClassroomObjectStudent();
        }
    }

    /**
     * Constructs the classroom object from the DB if the user is a student
     */
    private void getClassroomObjectStudent() {
        int groupID = DBConnection.getGroupIDbyStudentID(mainScenesController.getUserID());

        classroom.reset();

        String groupName = DBConnection.getGroupNameByID(groupID);
        Group group = new Group(groupName, groupID);

        ArrayList<Integer> studentIDs = DBConnection.getIDsByGroup(groupID);

        for (int j = 0; j < studentIDs.size(); j++) {
            Integer studentID = studentIDs.get(j);

            String name = DBConnection.getNameByID(studentID);
            String username = DBConnection.getUsernameByID(studentID);
            Student student = new Student(name, username, studentID);

            group.addStudent(student);
            student.setBacklog(getBacklogOfStudent(studentID));
            student.setSprint(getSprintOfStudent(studentID));
        }

        classroom.addGroup(group);
    }

    /**
     * Gets the sprint info of a user from the DB
     * @param studentID the students ID
     * @return the student's sprint object
     */
    private Sprint getSprintOfStudent(Integer studentID) {
        String sprintName = DBConnection.getCurrentSprintNameByUserID(studentID);

        Sprint toReturn = new Sprint(sprintName, studentID);

        ArrayList<Integer> sprintListsID = DBConnection.getTaskIdsInSprint(studentID);

        for (int i = 0; i < sprintListsID.size(); i++) {
            int listID = sprintListsID.get(i);

            String listName = DBConnection.getTaskNameByID(listID);

            List list = new List(listName, listID);

            toReturn.addList(list);
        }

        return toReturn;
    }

    /**
     * Gets the backlog info of a student from the DB
     * @param studentID student ID
     * @return the student's backlog object
     */
    public Backlog getBacklogOfStudent(int studentID) {
        Backlog toReturn = new Backlog(studentID);

        ArrayList<Integer> backlogListsID = DBConnection.getTaskIdsInBacklog(studentID);

        for (int i = 0; i < backlogListsID.size(); i++) {
            int listID = backlogListsID.get(i);

            String listName = DBConnection.getTaskNameByID(listID);

            List list = new List(listName, listID);

            toReturn.addList(list);
        }

        return toReturn;
    }

    /**
     * Constructs the classroom object from the DB if the user is a student
     */
    private void getClassroomObjectProfessor() {
        ArrayList<Integer> groupIDs = DBConnection.getAllGroupIds();

        classroom.reset();

        for (int i = 0; i < groupIDs.size(); i++) {
            Integer groupID = groupIDs.get(i);

            String groupName = DBConnection.getGroupNameByID(groupID);
            Group group = new Group(groupName, groupID);

            ArrayList<Integer> studentIDs = DBConnection.getIDsByGroup(groupID);

            for (int j = 0; j < studentIDs.size(); j++) {
                Integer studentID = studentIDs.get(j);
                String name = DBConnection.getNameByID(studentID);
                String username = DBConnection.getUsernameByID(studentID);
                Student student = new Student(name, username, studentID);

                group.addStudent(student);
            }

            classroom.addGroup(group);
        }
    }

    /**
     * Called when the add group button is clicked. Pops up a window to ask for a group name
     * @param mouseEvent
     */
    public void newGroup(MouseEvent mouseEvent) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New Group");
        dialog.setHeaderText("New Group");
        dialog.setContentText("Enter the name for your new group: ");

        Optional<String> result = dialog.showAndWait();

        int groupID = DBConnection.makeGroup(result.get());

        result.ifPresent(name -> classroom.addGroup(new Group(result.get(), groupID)));

        createGroupTreeView();
    }

    /**
     * Called when the add student button is clicked. Pops up a window to ask for the students name and id.
     * Adds that student to the selected group.
     * @param mouseEvent
     * @throws IOException
     */
    public void newStudent(MouseEvent mouseEvent) throws IOException {
        TreeItem selectedItem = groupTree.getSelectionModel().getSelectedItem();
        int level = groupTree.getTreeItemLevel(selectedItem);
        int index = groupTree.getSelectionModel().getSelectedIndex();

        if (level == 1 && index >= 0) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/runtimeTerror/scrumApp/addStudentDialog.fxml"));
            Parent root = (Parent) loader.load();
            AddStudentDialogController controller = (AddStudentDialogController) loader.getController();
            controller.setMainController(this, index);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setTitle("Add Student");
            stage.showAndWait();

            createGroupTreeView();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Group Selected");
            alert.setHeaderText(null);
            alert.setContentText("Select a Group! (By Clicking on one)");
            alert.showAndWait();
        }
    }

    /**
     * Called by LoginDialogController once add student button is clicked. Adds the student to the model and DB
     * @param name name of student
     * @param username unique username given to the student
     * @param password password given to the student
     * @param groupPosition position of student in their group class
     */
    public void addNewStudent(String name, String username, String password, int groupPosition) {

        int groupID = classroom.getGroup(groupPosition).getID();

        // get the encrypted password to store in the database
        String hashedPassword = DBConnection.hashPassword(password);

        DBConnection.makeUser(1, name, username, "Untitled Sprint", hashedPassword, groupID); //was nameField.getText(), idField.getText(), groupPosition

        int userID = DBConnection.getUserIDbyUsername(username);

        classroom.getGroup(groupPosition).addStudent(new Student(name, username, userID));
    }

    /**
     * Deletes the selected treeview element.
     * @param mouseEvent
     */
    public void deleteSelected(MouseEvent mouseEvent) {
        TreeItem selectedItem = groupTree.getSelectionModel().getSelectedItem();
        int level = groupTree.getTreeItemLevel(selectedItem);
        int index = groupTree.getSelectionModel().getSelectedIndex();

        // Selected is group
        if (level == 1) {
            int groupID = classroom.getGroup(index).getID();
            classroom.removeGroup(index);
            DBConnection.deleteGroupById(groupID);

        } else if (level == 2) { // Selected is student


            TreeItem listTree = selectedItem.getParent();
            int groupIndex = listTree.getParent().getChildren().indexOf(listTree);
            int studentIndex = selectedItem.getParent().getChildren().indexOf(selectedItem);

            int studentID = classroom.getGroup(groupIndex).getStudent(studentIndex).getId();

            classroom.getGroup(groupIndex).deleteStudent(studentIndex);
            DBConnection.deleteStudentById(studentID);
        }

        createGroupTreeView();
    }

    /**
     * Triggered when the professor clicks the view student button. Verifies the selection and calls appropriate methods to get data and update UI
     * @param mouseEvent
     */
    public void viewStudent(MouseEvent mouseEvent) {
        TreeItem selectedItem = groupTree.getSelectionModel().getSelectedItem();
        int level = groupTree.getTreeItemLevel(selectedItem);
        int index = groupTree.getSelectionModel().getSelectedIndex();

        // Selected is group
        if (level != 2) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Group Selected");
            alert.setHeaderText(null);
            alert.setContentText("Select a Student! (By Clicking on one)");
            alert.showAndWait();
        } else { // Selected is student
            TreeItem listTree = selectedItem.getParent();
            int groupIndex = listTree.getParent().getChildren().indexOf(listTree);
            int studentIndex = selectedItem.getParent().getChildren().indexOf(selectedItem);

            int studentID = classroom.getGroup(groupIndex).getStudent(studentIndex).getId();

            mainScenesController.openLoadingWindow();

            mainScenesController.selectUser(studentID);

            mainScenesController.closeLoadingWindow();
        }
    }


}
