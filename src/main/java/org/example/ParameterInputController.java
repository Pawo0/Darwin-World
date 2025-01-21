package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.genomes.MutationType;
import org.example.simulations.SimulationApp;
import org.example.simulations.SimulationSettings;

import java.io.*;
import java.util.List;

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
        genomeLengthField.setText("5");
        refreshTimeField.setText("500");
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

    @FXML
    private void loadConfiguration() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Configuration");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                List<String> lines = reader.lines().toList();
                if (lines.size() >= 15) {
                    mapWidthField.setText(lines.get(0));
                    mapHeightField.setText(lines.get(1));
                    startAmountOfGrassField.setText(lines.get(2));
                    energyGainedFromEatingField.setText(lines.get(3));
                    dailyAmountGrowingGrassField.setText(lines.get(4));
                    lifeGivingCorpsesField.setSelected(Boolean.parseBoolean(lines.get(5)));
                    startAmountOfAnimalsField.setText(lines.get(6));
                    startAnimalEnergyField.setText(lines.get(7));
                    energyNeededToCopulateField.setText(lines.get(8));
                    energyUsedToCopulateField.setText(lines.get(9));
                    minMutationAmountField.setText(lines.get(10));
                    maxMutationAmountField.setText(lines.get(11));
                    mutationTypeField.setValue(MutationType.valueOf(lines.get(12)));
                    genomeLengthField.setText(lines.get(13));
                    refreshTimeField.setText(lines.get(14));
                    saveToCSVField.setSelected(Boolean.parseBoolean(lines.get(15)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void saveConfiguration() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Configuration");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(mapWidthField.getText() + "\n");
                writer.write(mapHeightField.getText() + "\n");
                writer.write(startAmountOfGrassField.getText() + "\n");
                writer.write(energyGainedFromEatingField.getText() + "\n");
                writer.write(dailyAmountGrowingGrassField.getText() + "\n");
                writer.write(Boolean.toString(lifeGivingCorpsesField.isSelected()) + "\n");
                writer.write(startAmountOfAnimalsField.getText() + "\n");
                writer.write(startAnimalEnergyField.getText() + "\n");
                writer.write(energyNeededToCopulateField.getText() + "\n");
                writer.write(energyUsedToCopulateField.getText() + "\n");
                writer.write(minMutationAmountField.getText() + "\n");
                writer.write(maxMutationAmountField.getText() + "\n");
                writer.write(mutationTypeField.getValue().toString() + "\n");
                writer.write(genomeLengthField.getText() + "\n");
                writer.write(refreshTimeField.getText() + "\n");
                writer.write(Boolean.toString(saveToCSVField.isSelected()) + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}