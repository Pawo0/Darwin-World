package org.example.simulations;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.example.genomes.Genome;
import org.example.interfaces.MapChangeListener;
import org.example.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

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
    private Label dominantGenomeAmount;
    @FXML
    private Label averageEnergy;
    @FXML
    private Label averageLifeSpan;
    @FXML
    private Label averageDescendantAmount;
    @FXML
    private Label animalGenome;
    @FXML
    private Label animalActiveGenome;
    @FXML
    private Label animalEnergy;
    @FXML
    private Label animalEatenGrass;
    @FXML
    private Label animalChildrenAmount;
    @FXML
    private Label animalDescendantsAmount;
    @FXML
    private Label animalLifeSpan;
    @FXML
    private Label animalDeathDay;
    @FXML
    private VBox grassPopulationChartBox;
    @FXML
    private LineChart<Number, Number> grassPopulationChart;
    @FXML
    private GridPane animalStatsGrid;
    @FXML
    private GridPane mapGrid;
    @FXML
    private VBox animalPopulationChartBox;
    @FXML
    private LineChart<Number, Number> animalPopulationChart;

    private WorldMap map;
    private Simulation simulation;
    private SimulationSettings settings;
    private SimulationEngine engine;
    private SimulationStats stats;
    private Image animalImage;
    private final Image grassImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/grass.png")));
    private final Image krecikImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/krecik.jpg")));

    private int height;
    private int width;
    private double cellSize;

    private Animal followedAnimal;

    private boolean isPaused = true;

    private XYChart.Series<Number, Number> animalPopulationSeries;
    private XYChart.Series<Number, Number> grassPopulationSeries;


    public void initialize(SimulationSettings settings) {
        this.settings = settings;
        animalImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/animal.png")));

        if (settings.isLifeGivingCorpses()) {
            map = new WorldMapDeadAnimals(settings);
        } else {
            map = new WorldMap(settings);
        }

        this.simulation = new Simulation(settings, map);
        height = settings.getMapHeight();
        width = settings.getMapWidth();
        cellSize = Math.min(600 / (height), 600 / (width));


        if (settings.isSaveToCSV()) {
            this.stats = new SimulationStats(map, "stats/" + map.getId() + "-stats.csv");
        } else {
            this.stats = new SimulationStats(map);
        }

        animalPopulationSeries = new XYChart.Series<>();
        animalPopulationSeries.setName("Live Animals");
        animalPopulationChart.getData().add(animalPopulationSeries);

        grassPopulationSeries = new XYChart.Series<>();
        grassPopulationSeries.setName("Grass");
        grassPopulationChart.getData().add(grassPopulationSeries);
        drawMap();
    }


    @FXML
    private void showAnimalPopulationChart() {
        animalPopulationChartBox.setManaged(true);
        animalPopulationChartBox.setVisible(true);
        grassPopulationChartBox.setManaged(false);
        grassPopulationChartBox.setVisible(false);
    }


    @FXML
    private void showGrassPopulationChart() {
        animalPopulationChartBox.setManaged(false);
        animalPopulationChartBox.setVisible(false);
        grassPopulationChartBox.setManaged(true);
        grassPopulationChartBox.setVisible(true);
    }


    public void pause() {
        engine.pause();
        isPaused = true;
        mapChanged(map, "paused");
    }


    private void drawInfoGrid(){
        List<Vector2d> fieldsWithPriority =  map.getFieldsWithGrassGrowPriority();

        for (Vector2d position : fieldsWithPriority) {
            Rectangle rectangle = new Rectangle(cellSize, cellSize);
            rectangle.setStyle("-fx-fill: rgba(0, 255, 0, 0.2);");
            rectangle.setMouseTransparent(true);
            GridPane.setHalignment(rectangle, HPos.CENTER);
            mapGrid.add(rectangle, position.getX(), position.getY(), 1, 1);
        }

        Genome dominantGenome =  stats.getDominantGenome();

        for (PriorityQueue<Animal> animals : map.getLiveAnimals().values()) {
            for (Animal animal : animals) {
                if (animal.getGenotype().equals(dominantGenome)) {
                    Rectangle rectangle = new Rectangle(cellSize, cellSize);
                    rectangle.setStyle("-fx-fill: rgba(255, 0, 0, 0.2);");
                    rectangle.setMouseTransparent(true);
                    GridPane.setHalignment(rectangle, HPos.CENTER);
                    mapGrid.add(rectangle, animal.position().getX(), animal.position().getY(), 1, 1);
                }
            }
        }
    }


    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().getFirst());
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }


    private GridPane createAnimalView(Animal animal, double cellSize) {
        if (animal == followedAnimal){
            Vector2d position = animal.position();
            ImageView krecikView = new ImageView(krecikImage);
            krecikView.setFitWidth(cellSize);
            krecikView.setFitHeight(cellSize);
            GridPane.setHalignment(krecikView, HPos.CENTER);
            mapGrid.add(krecikView, position.getX(), position.getY(), 1, 1);
            GridPane animalView = new GridPane();
            animalView.add(krecikView, 0, 0);
            return animalView;
        }

        ImageView imageView = new ImageView(animalImage);
        imageView.setFitWidth(cellSize * 0.8);
        imageView.setFitHeight(cellSize * 0.8);

        double energyRatio = (double) animal.getEnergy() / settings.getEnergyNeededToCopulate();
        ProgressBar energyBar = new ProgressBar(energyRatio);
        energyBar.setPrefWidth(cellSize * 0.8);

        setBarColor(energyBar, energyRatio);
        GridPane animalView = new GridPane();
        animalView.add(imageView, 0, 0);
        animalView.add(energyBar, 0, 1);


        GridPane.setHalignment(imageView, HPos.CENTER);
        GridPane.setHalignment(energyBar, HPos.CENTER);
        animalView.setAlignment(Pos.CENTER);

        return animalView;
    }


    private void setBarColor(ProgressBar bar, double energyRatio) {
        if (energyRatio > 1) {
            bar.setStyle("-fx-accent: darkgreen;");
        } else if (energyRatio < 0.2) {
            bar.setStyle("-fx-accent: red;");
        } else if (energyRatio < 0.4) {
            bar.setStyle("-fx-accent: orange;");
        } else if (energyRatio < 0.6) {
            bar.setStyle("-fx-accent: yellow;");
        } else if (energyRatio < 0.8) {
            bar.setStyle("-fx-accent: lightgreen;");
        } else {
            bar.setStyle("-fx-accent: green;");
        }

    }


    private void drawAnimalStats(Animal animal){
        animalGenome.setText(animal.getGenotype().toString());
        animalActiveGenome.setText(String.valueOf(animal.getGenotype().getGen(animal.getGeneIndex())));
        animalEnergy.setText(String.valueOf(animal.getEnergy()));
        animalEatenGrass.setText(String.valueOf(animal.getGrassEaten()));
        animalChildrenAmount.setText(String.valueOf(animal.getChildrenCounter()));
        animalDescendantsAmount.setText(String.valueOf(animal.getDescendantsCounter()));
        animalLifeSpan.setText(String.valueOf(animal.getAge()));
        animalDeathDay.setText(String.valueOf(animal.getDeathDate()));
    }


    private void drawFocusedAnimal(Animal animal){
        Vector2d position = animal.position();
        if (map.isAnimalAt(position)){
            System.out.println("Animal at " + position + " clicked: " + animal);
            ImageView krecikView = new ImageView(krecikImage);
            krecikView.setFitWidth(cellSize);
            krecikView.setFitHeight(cellSize);
            GridPane.setHalignment(krecikView, HPos.CENTER);
            mapGrid.add(krecikView, position.getX(), position.getY(), 1, 1);
        }
    }


    private void handleAnimalClick(Animal animal){
        if (followedAnimal == null){
            followedAnimal = animal;
            drawFocusedAnimal(animal);

            setAnimalStats(animal);
            drawAnimalStats(animal);
            animalStatsGrid.visibleProperty().setValue(true);
        }
        else if (followedAnimal.equals(animal)){
            followedAnimal = null;
            mapChanged(map, "clicked");
        }
        else{
            followedAnimal = animal;
            mapChanged(map, "clicked");
        }
    }


    private void setStats(){
        if (settings.isSaveToCSV()) {
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
        dominantGenomeAmount.setText(String.valueOf(stats.getDominantGenomeAmount()));
        averageEnergy.setText(String.valueOf(round(stats.getAverageEnergy(),2)));
        averageLifeSpan.setText(String.valueOf(round(stats.getAverageLifeSpan(),2)));
        averageDescendantAmount.setText(String.valueOf(round(stats.getAverageDescendantAmount(),2)));
    }


    private void setAnimalStats(Animal animal){
        AnimalStats animalStats = new AnimalStats(animal);
        System.out.println(animalStats);
    }


    private void clearAnimalStats(){
        System.out.println("clear");
    }


    private void setCharts(){
        int currentDay = stats.getDay();
        int liveAnimals = stats.getLiveAnimalsAmount();
        int grass = stats.getGrassAmount();
        animalPopulationSeries.getData().add(new XYChart.Data<>(currentDay, liveAnimals));
        grassPopulationSeries.getData().add(new XYChart.Data<>(currentDay, grass));
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public void drawMap() {
        clearGrid();

        for (int i = 0; i <= width-1; i++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
        }

        for (int j = 0; j <= height-1; j++) {
            mapGrid.getRowConstraints().add(new RowConstraints(cellSize));
        }

        for (int i = 0; i <= width - 1; i++) {
            for (int j = 0; j <= height - 1; j++) {
                Vector2d currentPosition = new Vector2d(i, j);
                Label label = null;
                Object object = null;

                if (this.map.isAnimalAt(currentPosition)) {
                    object = this.map.getAnimalsAt(currentPosition).peek();
                    if (object != null) {
                        Animal animal = (Animal) object;
                        GridPane animalView = createAnimalView(animal, cellSize);
                        GridPane.setHalignment(animalView, HPos.CENTER);
                        mapGrid.add(animalView, i, j, 1, 1);
                        animalView.setOnMouseClicked(event -> {
                            handleAnimalClick(animal);
                        });
                        continue;
                    }
                }
                else if (this.map.isGrassAt(currentPosition)) {
                    ImageView grassView = new ImageView(grassImage);
                    grassView.setFitWidth(cellSize);
                    grassView.setFitHeight(cellSize);
                    GridPane.setHalignment(grassView, HPos.CENTER);
                    mapGrid.add(grassView, i, j, 1, 1);
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
                mapGrid.add(label, i, j, 1, 1);
            }
        }

        if (followedAnimal != null) {
            drawAnimalStats(followedAnimal);
            animalStatsGrid.visibleProperty().setValue(true);
//            mapChanged(map, "animal stats");
        }
        else {
            animalStatsGrid.visibleProperty().setValue(false);
        }

        if (isPaused){
            drawInfoGrid();
        }
    }

    public void start() {
        napis.setText("Hello World!");
        System.out.println("hello world");
        if (engine != null) {
            engine.resume();
        } else {
            engine = new SimulationEngine(List.of(simulation));
            try {
                engine.runAsync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            map.addObserver(this);
            this.setStats();
//            drawMap();
        }
        isPaused = false;
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
            napis.setText("Living animals " + message);

            synchronized (this){
                drawMap();
                setStats();
                setCharts();

            }
        });
    }
}
