package com.runtimeTerror.scrumApp.controllers;

import com.runtimeTerror.scrumApp.MainScenesController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Backlog;
import model.List;
import model.Sprint;
import model.SprintArchive;

/**
 * Archive tab controller
 *
 * @author Thomas Breimer
 * @version 5/29/23
 */

public class ArchiveController {
    private SprintArchive sprintArchive;

    private MainScenesController mainScenesController;

    @FXML
    private TreeView<String> archiveTree;

    private Image checkIcon;
    private Image exIcon;

    /**
     * Called from mainScenesController. Assigns the shared model objects
     * @param sprintArchive sprintArchive object
     * @param mainScenesController reference back to mainScenesController
     */
    public void assignObjects(SprintArchive sprintArchive, MainScenesController mainScenesController) {
        this.sprintArchive = sprintArchive;
        this.mainScenesController = mainScenesController;
        createSprintArchiveTreeView();
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
     * Creates a new archive tree
     */
    public void createSprintArchiveTreeView(){
        TreeItem<String> rootItem = new CheckBoxTreeItem<>("Archive");

        for (int i = 0; i < sprintArchive.getArchive().size(); i++) {

            Sprint thisSprint = sprintArchive.getSprint(i);

            String sprintTitle = thisSprint.getName();

            TreeItem<String> treeSprint = new TreeItem<>(sprintTitle);
            rootItem.getChildren().addAll(treeSprint);

            TreeItem<String> treeReflection = new TreeItem<>("Reflection: " + thisSprint.getReflection());
            treeSprint.getChildren().addAll(treeReflection);


            for (int j = 0; j < thisSprint.getLists().size(); j++) {
                List list = thisSprint.getList(j);
                TreeItem<String> treeList = new TreeItem<String>(list.getName());
                treeSprint.getChildren().addAll(treeList);

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
        }

        archiveTree.setRoot(rootItem);
        archiveTree.setCellFactory(TextFieldTreeCell.forTreeView());
        archiveTree.setShowRoot(false);
    }
}
