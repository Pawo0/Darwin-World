package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.MutationType;
import org.example.simulations.SimulationApp;
import org.example.simulations.SimulationSettings;

public class ParameterInputController {

    @FXML
    private TextField mapWidthField;
    @FXML
    private TextField mapHeightField;
    @FXML
    private TextField startAmountOfGrassField;
    @FXML
    private TextField energyGainedFromEatingField;
    @FXML
    private TextField dailyAmountGrowingGrassField;
    @FXML
    private CheckBox lifeGivingCorpsesField;
    @FXML
    private TextField startAmountOfAnimalsField;
    @FXML
    private TextField startAnimalEnergyField;
    @FXML
    private TextField energyNeededToCopulateField;
    @FXML
    private TextField energyUsedToCopulateField;
    @FXML
    private TextField minMutationAmountField;
    @FXML
    private TextField maxMutationAmountField;
    @FXML
    private ComboBox<MutationType> mutationTypeField;
    @FXML
    private TextField genomeLengthField;
    @FXML
    private TextField refreshTimeField;
    @FXML
    private CheckBox saveToCSVField;

    @FXML
    private void initialize() {
        // Set default values
        mapWidthField.setText("20");
        mapHeightField.setText("20");
        startAmountOfGrassField.setText("40");
        energyGainedFromEatingField.setText("7");
        dailyAmountGrowingGrassField.setText("10");
        lifeGivingCorpsesField.setSelected(false);
        startAmountOfAnimalsField.setText("20");
        startAnimalEnergyField.setText("40");
        energyNeededToCopulateField.setText("30");
        energyUsedToCopulateField.setText("30");
        minMutationAmountField.setText("0");
        maxMutationAmountField.setText("40");
        mutationTypeField.getItems().setAll(MutationType.values());
        mutationTypeField.setValue(MutationType.DEFAULT);
        genomeLengthField.setText("70");
        refreshTimeField.setText("100");
        saveToCSVField.setSelected(false);
    }

    @FXML
    private void startSimulation() {
        SimulationSettings settings = new SimulationSettings(
                Integer.parseInt(mapWidthField.getText()),
                Integer.parseInt(mapHeightField.getText()),
                Integer.parseInt(startAmountOfGrassField.getText()),
                Integer.parseInt(energyGainedFromEatingField.getText()),
                Integer.parseInt(dailyAmountGrowingGrassField.getText()),
                lifeGivingCorpsesField.isSelected(),
                Integer.parseInt(startAmountOfAnimalsField.getText()),
                Integer.parseInt(startAnimalEnergyField.getText()),
                Integer.parseInt(energyNeededToCopulateField.getText()),
                Integer.parseInt(energyUsedToCopulateField.getText()),
                Integer.parseInt(minMutationAmountField.getText()),
                Integer.parseInt(maxMutationAmountField.getText()),
                mutationTypeField.getValue(),
                Integer.parseInt(genomeLengthField.getText()),
                Integer.parseInt(refreshTimeField.getText()),
                saveToCSVField.isSelected()
        );

        try {
            SimulationApp.setSettings(settings);
            SimulationApp simulationApp = new SimulationApp();
            Stage stage = new Stage();
            simulationApp.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}