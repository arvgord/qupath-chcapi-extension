// Copyright (C) 2019 Google LLC
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.quantumsoft.qupathcloud.gui.windows;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.quantumsoft.qupathcloud.entities.Location;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The NewDatasetWindow will be presented if user clicks on the New Dataset button.
 */
public class NewDatasetWindow {

  private static final double STAGE_WIDTH = 300;
  private static final double STAGE_HEIGHT = 150;
  private static final double INDENT = 35;
  private static final double WINDOW_WIDTH = STAGE_WIDTH - INDENT;
  private static final double WINDOW_HEIGHT = STAGE_HEIGHT - INDENT;
  private static final double SHADOW_RADIUS = 13;
  private static final double RIGHT_PADDING = 13;
  private static final double LEFT_PADDING = 13;
  private static final double TOP_PADDING = 13;
  private static final double BOTTOM_PADDING = 13;
  private static final double SPACING = 13;
  private static final String PROMPT_TEXT = "Enter new Id";
  private Stage newDatasetStage;
  private List<Location> locations;

  /**
   * Instantiates a new New dataset window.
   *
   * @param locations the locations
   */
  NewDatasetWindow(List<Location> locations) {
    this.locations = locations;
    newDatasetStage = new Stage();
  }

  /**
   * Show window pane.
   *
   * @return the pane
   */
  Pane showWindow() {
    JFXButton createButton = new JFXButton("CREATE");
    createButton.setId("createButton");
    createButton.getStyleClass().add("createButton");

    JFXButton cancelButton = new JFXButton("CANCEL");
    cancelButton.setId("cancelButton");
    cancelButton.getStyleClass().add("cancelButton");

    JFXComboBox<Label> locationsComboBox = new JFXComboBox<>();
    for (Location location : locations) {
      locationsComboBox.getItems().add(new Label(location.getLocationId()));
    }
    locationsComboBox.setId("locationsComboBoxId");
    locationsComboBox.setPromptText("Select Locations");
    locationsComboBox.setPrefWidth(Integer.MAX_VALUE);

    BorderPane buttonsPanel = new BorderPane();
    buttonsPanel.setLeft(cancelButton);
    buttonsPanel.setRight(createButton);

    TextField dicomStoreId = new TextField();
    dicomStoreId.setPromptText(PROMPT_TEXT);
    dicomStoreId.setId("datasetId");

    VBox vBox = new VBox(dicomStoreId, locationsComboBox, buttonsPanel);
    vBox.setSpacing(SPACING);
    vBox.setAlignment(Pos.CENTER);
    vBox.setPadding(new Insets(TOP_PADDING, RIGHT_PADDING, BOTTOM_PADDING, LEFT_PADDING));
    vBox.setStyle("-fx-background-color: white;");
    vBox.setMaxWidth(WINDOW_WIDTH);
    vBox.setMaxHeight(WINDOW_HEIGHT);

    BorderPane borderPane = new BorderPane(vBox);
    borderPane.setEffect(new DropShadow(SHADOW_RADIUS, 0, 0, Color.BLACK));
    borderPane.setStyle("-fx-background-color: transparent;");

    Scene newDicomStoreScene = new Scene(borderPane);
    newDicomStoreScene.getStylesheets().add("styles/styles.css");
    newDicomStoreScene.setFill(Color.TRANSPARENT);

    newDatasetStage.setScene(newDicomStoreScene);
    newDatasetStage.initStyle(StageStyle.TRANSPARENT);
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    newDatasetStage.setX((screenBounds.getWidth() - STAGE_WIDTH) / 2);
    newDatasetStage.setY((screenBounds.getHeight() - STAGE_HEIGHT) / 2);
    newDatasetStage.initModality(Modality.APPLICATION_MODAL);
    newDatasetStage.setWidth(STAGE_WIDTH);
    newDatasetStage.setHeight(STAGE_HEIGHT);
    newDatasetStage.setResizable(false);
    newDatasetStage.show();

    return vBox;
  }

  /**
   * Gets stage.
   *
   * @return the stage
   */
  public Stage getStage() {
    return newDatasetStage;
  }
}
