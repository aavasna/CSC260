package com.runtimeTerror.scrumApp.controllers;

import com.runtimeTerror.scrumApp.DBConnection;
import com.runtimeTerror.scrumApp.Main;
import com.runtimeTerror.scrumApp.MainScenesController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import model.Backlog;
import model.List;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the backlog tab
 *
 * @author Thomas Breimer
 * @version 5/22/23
 */

public class BacklogController implements Initializable {
    public ButtonBar buttonBar;
    public Button addTaskButton;
    public Button deleteSelectedButton;
    public Button startSprintButton;
    public Button addListButton;
    private Backlog backlog;

    private MainScenesController mainScenesController;

    @FXML
    private TreeView<String> backlogTree;

    private String userRole;

    /**
     * Assigns shared objects between mainScenesController
     * @param newBacklog backlog object
     * @param mainScenesController reference back to mainScenesController
     */
    public void assignObjects(Backlog newBacklog, MainScenesController mainScenesController) {
        this.backlog = newBacklog;
        this.mainScenesController = mainScenesController;

        createBackLogTreeView();
    }

    /**
     * Sets the user role and changes button visibility accordingly
     * @param userRole role of user. "professor" or "student"
     */
    public void setUserRole(String userRole) {
        this.userRole = userRole;

        if (userRole == "professor") {
            addListButton.setVisible(false);
            addTaskButton.setVisible(false);
            startSprintButton.setVisible(false);
            deleteSelectedButton.setVisible(false);
        }
    }

    /**
     * Creates a new backlog item to be viewed in the backlog
     */
    public void createBackLogTreeView(){
        TreeItem<String> rootItem = new TreeItem<>("###");

        for (int i = 0; i < backlog.getLength(); i++) {

            List list = backlog.getList(i);

            TreeItem<String> treeList = new TreeItem<>(list.getName());
            rootItem.getChildren().addAll(treeList);

            for (int j = 0; j < list.getLength(); j++) {
                String task = list.getTaskName(j);
                TreeItem<String> treeTask = new TreeItem<>(task);
                treeList.getChildren().addAll(treeTask);
            }
        }

        backlogTree.setRoot(rootItem);
        backlogTree.setCellFactory(TextFieldTreeCell.forTreeView());
        backlogTree.setEditable(true);
        backlogTree.setOnEditCommit(new EventHandler<TreeView.EditEvent<String>>() {
            @Override
            public void handle(TreeView.EditEvent<String> event) {
                TreeItem selectedItem = backlogTree.getSelectionModel().getSelectedItem();
                int level = backlogTree.getTreeItemLevel(selectedItem);
                int index = backlogTree.getSelectionModel().getSelectedIndex();

                if (level == 1) {
                    List list = backlog.getList(index);
                    list.changeName(event.getNewValue());
                    DBConnection.changeTaskName(list.getID(), event.getNewValue());
                } else if (level == 2) {
                    TreeItem listTree = selectedItem.getParent();
                    int listIndex = listTree.getParent().getChildren().indexOf(listTree);
                    backlog.getList(listIndex).changeTaskName(index - 1, event.getNewValue());
                    int taskID = backlog.getList(listIndex).getTaskID(index - 1);
                    DBConnection.changeSubtaskName(taskID, event.getNewValue());
                }
            }
        });

        backlogTree.setShowRoot(false);
    }

    /**
     * Called when the add to sprint button is clicked. Adds the currently selected list to the sprint
     * @param mouseEvent
     */
    public void addListToSprint(MouseEvent mouseEvent) {
        TreeItem selectedItem = backlogTree.getSelectionModel().getSelectedItem();
        int level = backlogTree.getTreeItemLevel(selectedItem);
        int index = backlogTree.getSelectionModel().getSelectedIndex();

        if (level == 1){
            DBConnection.moveTaskToSprint(backlog.getList(index).getID());
            mainScenesController.addListToSprint(index);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Task Selected!");
            alert.setHeaderText(null);
            alert.setContentText("Select a Task! (Subtasks cannot be individually added)");
            alert.showAndWait();
        }
    }

    /**
     * Called when the add task button is clicked. Pops up a window so the user can specify a name for the new list.
     * Makes a new list and adds it to the backlog
     * @param actionEvent
     */
    public void addListToBacklog(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New Task");
        dialog.setHeaderText("New Task");
        dialog.setContentText("Enter the name for your new task: ");


        Optional<String> result = dialog.showAndWait();
        int listID = DBConnection.makeTask(mainScenesController.getUserID(), result.get(), true, false, -1);
        result.ifPresent(name -> backlog.addList(new List(result.get(), listID)));

        createBackLogTreeView();
    }

    /**
     * When you want to add a new subtask to a newly created task or a previously created task you click the add subtask
     * to selected task you are able to type in a popup window the new subtask you want to add.
     * @param actionEvent for when you want to add a subtask to a selected task
     * @throws IOException
     */
    public void addTaskToList(ActionEvent actionEvent) {
        TreeItem selectedItem = backlogTree.getSelectionModel().getSelectedItem();
        int level = backlogTree.getTreeItemLevel(selectedItem);
        int index = backlogTree.getSelectionModel().getSelectedIndex();

        if (level != 1 || index < 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Task Selected!");
            alert.setHeaderText(null);
            alert.setContentText("Select a Task! (You can't add a subtask to another subtask)");
            alert.showAndWait();
        } else {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("New Subtask");
            dialog.setHeaderText("New Subtask, will be added to the selected list");
            dialog.setContentText("Enter the name for your new subtask: ");

            Optional<String> result = dialog.showAndWait();

            int listID = backlog.getList(index).getID();

            int taskID = DBConnection.makeSubtask(listID, result.get(), false);
            backlog.getList(index).addTask(result.get(), taskID);

            createBackLogTreeView();
        }
    }

    /**
     * When you want to delete either a task and its subtasks, or just a subtask, you click the delete selected button,
     * and it will delete what you have selected from the backlog
     * @param actionEvent for when you want to delete a selected task and its corresponding subtasks or just a single subtask
     * @throws IOException
     */
    public void deleteSelected(ActionEvent actionEvent) {
        TreeItem selectedItem = backlogTree.getSelectionModel().getSelectedItem();
        int level = backlogTree.getTreeItemLevel(selectedItem);
        int index = backlogTree.getSelectionModel().getSelectedIndex();

        // Selected is list
        if (level == 1) {
            int listID = backlog.getList(index).getID();
            DBConnection.deleteTaskById(listID);
            backlog.removeList(index);
        } else if (level == 2) { // Selected is task
            TreeItem listTree = selectedItem.getParent();
            int listIndex = listTree.getParent().getChildren().indexOf(listTree);
            int taskIndex = selectedItem.getParent().getChildren().indexOf(selectedItem);
            DBConnection.deleteSubtaskById(backlog.getList(listIndex).getTaskID(taskIndex));
            backlog.getList(listIndex).removeTask(taskIndex);
        }

        mainScenesController.setStudentBacklog(mainScenesController.getUserID());

        createBackLogTreeView();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
