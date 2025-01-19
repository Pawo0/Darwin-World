package org.example.simulations;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.example.model.*;

import java.util.List;

public class SimulationPresenter implements MapChangeListener {
    @FXML
    private Label napis;

    @FXML
    private GridPane mapGrid;

    private WorldMap map;
    private Simulation simulation;
    private SimulationSettings settings;
    private SimulationEngine engine;

    @FXML
    private void initialize() {
        System.out.println("SimulationPresenter initialized");
        settings = new SimulationSettings(
                20,
                20,
                40,
                7,
                10,
                true,
                20,
                40,
                30,
                30,
                0,
                40,
                MutationType.DEFAULT,
                70,
                100
        );
        if (settings.isLifeGivingCorpses()) {
            map = new WorldMapDeadAnimals(settings);
        } else {
            map = new WorldMap(settings);
        }
        this.simulation = new Simulation(settings, map);
    }


    public void start() {
        napis.setText("Hello World!");
        System.out.println("hello world");
        if (engine != null) {
            engine.resume();
        } else {
//            aktualnie dziala tak ze jednoczesnie kilka na raz chce dzialac
            engine = new SimulationEngine(List.of(simulation));
            try {
                engine.runAsync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            map.addObserver(this);
            drawMap();
        }
    }

    public void pause() {
        engine.pause();
    }

    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().getFirst()); // hack to retain visible grid lines
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    public void drawMap() {
        int height = settings.getMapHeight();
        int width = settings.getMapWidth();
        double cellSize = Math.min(600 / (height + 1), 600 / (width + 1));

        clearGrid();


        for (int i = 0; i <= width; i++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
        }
        for (int j = 0; j <= height; j++) {
            mapGrid.getRowConstraints().add(new RowConstraints(cellSize));
        }
        for (int i = -1; i <= width-1; i++) {
            for (int j = -1; j <= height-1; j++) {
                Vector2d currentPosition = new Vector2d(i, j);
                Label label = null;
                Object object = null;
                if (i == -1 && j == -1) {
                    label = new Label("x/y");
                } else if (i == -1) {
                    label = new Label("" + currentPosition.getY());
                } else if (j == -1) {
                    label = new Label("" + currentPosition.getX());
                } else if (this.map.isAnimalAt(currentPosition)) {
                    object = this.map.animalsAt(currentPosition).peek();
                } else if (this.map.isGrassAt(currentPosition)) {
                    object = this.map.getGrassAt(currentPosition);
                } else if (this.map.isDeadAnimalAt(currentPosition) && settings.isLifeGivingCorpses()) {
                    object = this.map.getDeadAnimals().get(currentPosition).peek();
                } else {
                    label = new Label(" ");
                }
                if (object != null) {
                    label = new Label(object.toString());
                }
//                label.setText("(" + currentPosition.getY() + "," + currentPosition.getX() + ")");
                GridPane.setHalignment(label, HPos.CENTER);
                mapGrid.add(label, i + 1, j + 1, 1, 1);

            }
        }
    }


    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
            napis.setText("Living animals " + message);
//            o ile to dziala tak jak mysle ze dziala
            synchronized (this){
                drawMap();
            }
        });
    }
}
