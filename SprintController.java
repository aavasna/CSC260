package com.runtimeTerror.scrumApp.controllers;

import com.runtimeTerror.scrumApp.DBConnection;
import com.runtimeTerror.scrumApp.MainScenesController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Sprint;
import javafx.scene.control.cell.CheckBoxTreeCell;
import model.List;
import javafx.scene.control.*;
import java.util.Optional;

/**
 * Controller for the sprint tab
 *
 * @author Thomas Breimer
 * @version 5/29/23
 */

public class SprintController {

    public Button finishSprintButton;
    public Button renameSprintButton;
    private Sprint sprint;

    private MainScenesController mainScenesController;

    @FXML
    private TreeView<String> sprintTree;
    private String userRole;

    private Image checkIcon;
    private Image exIcon;

    /**
     * Called when rename sprint is clicked
     * @param mouseEvent
     */
    public void renameSprint(MouseEvent mouseEvent) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Rename Sprint");
        dialog.setHeaderText("Rename Sprint");
        dialog.setContentText("Enter the name for your new sprint: ");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> sprint.changeName(result.get()));
        DBConnection.changeSprintName(mainScenesController.getUserID(), result.get());

        createSprintTreeView();
    }

    public void assignObjects(Sprint sprint, MainScenesController mainScenesController) {
        this.sprint = sprint;
        this.mainScenesController = mainScenesController;
    }

    /**
     * Assign the check and ex images
     * @param checkIcon image of the check icon
     * @param exIcon image of the ex icon
     */
    public void assignImages(Image checkIcon, Image exIcon) {
        this.checkIcon = checkIcon;
        this.exIcon = exIcon;
    }

    /**
     * Sets the user role and updates UI
     * @param userRole
     */
    public void setUserRole(String userRole) {
        this.userRole = userRole;

        if (userRole == "professor") {
            finishSprintButton.setVisible(false);
            renameSprintButton.setVisible(false);
        }
    }

    /**
     * Create the sprint tree based on user role
     */
    public void createSprintTreeView(){
        if (userRole == "student") {
            createSprintTreeViewStudent();
        }else {
            createSprintTreeViewProfessor();
        }
    }

    /**
     * Create the sprint tree based on the model if user is prof
     */
    private void createSprintTreeViewProfessor() {
        if (sprint.isEmpty()) {
            sprintTree.setShowRoot(false);
            TreeItem<String> rootItem = new TreeItem<>("");
            sprintTree.setRoot(rootItem);

        } else {

            CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>(sprint.getName());

            for (int j = 0; j < sprint.getLists().size(); j++) {
                List list = sprint.getList(j);
                TreeItem<String> treeList = new TreeItem<String>(list.getName());
                rootItem.getChildren().addAll(treeList);

                boolean listComplete = true;

                for (int k = 0; k < list.getLength(); k++) {
                    String task = list.getTaskName(k);

                    ImageView icon;

                    if (list.taskIsChecked(k)) {
                        icon = new ImageView(checkIcon);
                    } else {
                        icon = new ImageView(exIcon);
                        listComplete = false;
                    }

                    TreeItem<String> treeSubTask = new TreeItem<String>(task, icon);
                    treeList.getChildren().addAll(treeSubTask);
                }

                ImageView icon;

                if (listComplete) {
                    icon = new ImageView(checkIcon);
                } else {
                    icon = new ImageView(exIcon);
                }

                treeList.setGraphic(icon);
            }

            sprintTree.setRoot(rootItem);

            sprintTree.setShowRoot(true);

        }
    }

    /**
     * Create the sprint tree based on the model if user is student
     */
    public void createSprintTreeViewStudent() {
        if (sprint.isEmpty()) {
            sprintTree.setShowRoot(false);
            TreeItem<String> rootItem = new TreeItem<>("");
            sprintTree.setRoot(rootItem);

        } else {

            CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>(sprint.getName());

            for (int i = 0; i < sprint.getLists().size(); i++) {

                List list = sprint.getList(i);

                CheckBoxTreeItem<String> treeList = new CheckBoxTreeItem<>(list.getName());

                boolean allTasksComplete = true;

                for (int j = 0; j < list.getLength(); j++) {
                    String task = list.getTaskName(j);
                    CheckBoxTreeItem<String> treeTask = new CheckBoxTreeItem<>(task);
                    treeTask.setSelected(list.taskIsChecked(j));

                    if (!list.taskIsChecked(j)) {
                        allTasksComplete = false;
                    }

                    treeTask.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        handleTaskChecked(treeTask);
                    });
                    treeList.getChildren().addAll(treeTask);
                }

                treeList.setSelected(allTasksComplete);
                rootItem.getChildren().addAll(treeList);
            }

            sprintTree.setRoot(rootItem);
            sprintTree.setCellFactory(CheckBoxTreeCell.forTreeView());

            sprintTree.setShowRoot(true);

        }
    }

    /**
     * When you have completed a task of the current sprint you can check it off to mark its completion
     * @param treeTask the task that being checked complete
     */
    public void handleTaskChecked(CheckBoxTreeItem treeTask){
        int taskIndex = treeTask.getParent().getChildren().indexOf(treeTask);
        String listName = (String) treeTask.getParent().getValue();

        int listIndex = sprint.getListPosition(listName);
        sprint.getList(listIndex).toggleTaskChecked(taskIndex);

        boolean checked = sprint.getList(listIndex).taskIsChecked(taskIndex);
        int subtaskID = sprint.getList(listIndex).getTaskID(taskIndex);

        if (checked){
            DBConnection.toggleSetSubtaskChecked(subtaskID, 1);
        } else {
            DBConnection.toggleSetSubtaskChecked(subtaskID, 0);
        }

    }

    /**
     * Called when clicking finish sprint button. Calls mainScenesController to do the real work
     * @param mouseEvent
     */
    public void finishSprint(MouseEvent mouseEvent) {
        mainScenesController.finishSprint();
    }
}
