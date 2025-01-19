package org.example.simulations;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.example.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class SimulationPresenter implements MapChangeListener {
    @FXML
    private Label napis;
    @FXML
    private Label day;
    @FXML
    private Label liveAnimalsAmount;
    @FXML
    private Label deadAnimalsAmount;
    @FXML
    private Label grassAmount;
    @FXML
    private Label dominantGenome;
    @FXML
    private Label averageEnergy;
    @FXML
    private Label averageLifeSpan;
    @FXML
    private Label averageDescendantAmount;

    @FXML
    private GridPane mapGrid;

    private WorldMap map;
    private Simulation simulation;
    private SimulationSettings settings;
    private SimulationEngine engine;
    private SimulationStats stats;
    private Image animalImage;
    private Image grassImage = new Image(getClass().getResourceAsStream("/images/grass.png"));

    @FXML
    private void initialize() {
        System.out.println("SimulationPresenter initialized");
        settings = new SimulationSettings(
                20,
                20,
                40,
                7,
                10,
                false,
                60,
                33,
                30,
                20,
                0,
                40,
                MutationType.DEFAULT,
                70,
                100,
                false

        );
        if (settings.isLifeGivingCorpses()) {
            map = new WorldMapDeadAnimals(settings);
        } else {
            map = new WorldMap(settings);
        }
        this.simulation = new Simulation(settings, map);
        this.stats = new SimulationStats(map);
        drawMap();
    }

    public void initializeWithSettings(SimulationSettings settings) {
        this.settings = settings;
        animalImage = new Image(getClass().getResourceAsStream("/images/animal.png"));

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
            this.setStats();
            drawMap();
        }
    }

    public void pause() {
        engine.pause();
    }

    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().getFirst());
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    private GridPane createAnimalView(Animal animal, double cellSize) {
        ImageView imageView = new ImageView(animalImage);
        imageView.setFitWidth(cellSize * 0.8); // Make the image smaller
        imageView.setFitHeight(cellSize * 0.8); // Make the image smaller

        double energyRatio = (double) animal.getEnergy() / settings.getEnergyNeededToCopulate();
        ProgressBar energyBar = new ProgressBar(energyRatio);
        energyBar.setPrefWidth(cellSize * 0.8); // Match the width of the image

        // Set the color of the progress bar based on the energy ratio
        if (energyRatio > 1) {
            energyBar.setStyle("-fx-accent: darkgreen;");
        } else if (energyRatio < 0.2) {
            energyBar.setStyle("-fx-accent: red;");
        } else if (energyRatio < 0.4) {
            energyBar.setStyle("-fx-accent: orange;");
        } else if (energyRatio < 0.6) {
            energyBar.setStyle("-fx-accent: yellow;");
        } else if (energyRatio < 0.8) {
            energyBar.setStyle("-fx-accent: lightgreen;");
        } else {
            energyBar.setStyle("-fx-accent: green;");
        }

        GridPane animalView = new GridPane();
        animalView.add(imageView, 0, 0);
        animalView.add(energyBar, 0, 1);

        // Set alignment for the image and progress bar
        GridPane.setHalignment(imageView, HPos.CENTER);
        GridPane.setHalignment(energyBar, HPos.CENTER);
        animalView.setAlignment(Pos.CENTER);

        return animalView;
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
        for (int i = 0; i <= width - 1; i++) {
            for (int j = 0; j <= height - 1; j++) {
                Vector2d currentPosition = new Vector2d(i, j);
                Label label = null;
                Object object = null;
//                if (i == -1 && j == -1) {
//                    label = new Label("x/y");
//                } else if (i == -1) {
//                    label = new Label("" + currentPosition.getY());
//                } else if (j == -1) {
//                    label = new Label("" + currentPosition.getX());
//                } else
                if (this.map.isAnimalAt(currentPosition)) {
                    object = this.map.animalsAt(currentPosition).peek();
                    if (object != null) {
                        GridPane animalView = createAnimalView((Animal) object, cellSize);
                        GridPane.setHalignment(animalView, HPos.CENTER);
                        mapGrid.add(animalView, i + 1, j + 1, 1, 1);
                        continue;
                    }
                } else if (this.map.isGrassAt(currentPosition)) {
                    ImageView grassView = new ImageView(grassImage);
                    grassView.setFitWidth(cellSize);
                    grassView.setFitHeight(cellSize);
                    GridPane.setHalignment(grassView, HPos.CENTER);
                    mapGrid.add(grassView, i + 1, j + 1, 1, 1);
                    continue;
                } else if (this.map.isDeadAnimalAt(currentPosition) && settings.isLifeGivingCorpses()) {
                    object = this.map.getDeadAnimals().get(currentPosition).peek();
                } else {
                    label = new Label(" ");
                }
                if (object != null) {
                    label = new Label(object.toString());
                }
                GridPane.setHalignment(label, HPos.CENTER);
                mapGrid.add(label, i + 1, j + 1, 1, 1);
            }
        }
    }


    private void setStats(){
        if (true) {
            this.stats = new SimulationStats(map, "stats/" +map.getId() + "-stats.csv");
        }
        else{
            this.stats = new SimulationStats(map);
        }
        day.setText(String.valueOf(stats.getDay()));
        liveAnimalsAmount.setText(String.valueOf(stats.getLiveAnimalsAmount()));
        deadAnimalsAmount.setText(String.valueOf(stats.getDeadAnimalsAmount()));
        grassAmount.setText(String.valueOf(stats.getGrassAmount()));
        dominantGenome.setText(String.valueOf(stats.getDominantGenome()));
        averageEnergy.setText(String.valueOf(round(stats.getAverageEnergy(),2)));
        averageLifeSpan.setText(String.valueOf(round(stats.getAverageLifeSpan(),2)));
        averageDescendantAmount.setText(String.valueOf(round(stats.getAverageDescendantAmount(),2)));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
            napis.setText("Living animals " + message);
//            o ile to dziala tak jak mysle ze dziala
            synchronized (this){
                drawMap();
                setStats();
            }
        });
    }
}
