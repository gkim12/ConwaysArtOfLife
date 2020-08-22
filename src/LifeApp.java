import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;


public class LifeApp extends Application implements GenerationListener {

	private LifeModel model;
	private BorderPane centerPane;
	private BooleanGridPane view;
	private Slider cellSizeSlider;
	private Slider delaySlider;
	private Button clearBtn;
	private Button nextGenBtn;
	private ToggleButton playBtn;
	private Label genNum;
	private MenuItem open;
	private MenuItem save;
	private FileChooser fileChooser;
	private MyAnimationTimer timer;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Boolean[][] grid = { { true, true, true, true, true }, { true, false, false, false, true },
				{ true, false, false, false, true }, { true, false, false, false, true },
				{ true, true, true, true, true } };

		stage.setTitle("Life GUI");
		stage.setResizable(false);
		stage.sizeToScene();

		BorderPane root = new BorderPane();
		root.setPrefSize(800, 600);

		view = new BooleanGridPane();
		model = new LifeModel(grid);
		view.setModel(model);
		view.setOnMouseDragged(new MouseGridHandler());
		view.setOnMousePressed(new MouseGridHandler());

		HBox bottom = new HBox();

		clearBtn = new Button("Clear");
		clearBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				for (int row = 0; row < model.getNumRows(); row++) {
					for (int col = 0; col < model.getNumCols(); col++) {
						model.setValueAt(row, col, false);
					}
				}
				model.setGeneration(0);
				genNum.setText("" + model.getGeneration());
			}

		});

		nextGenBtn = new Button("Next Generation");
		nextGenBtn.setOnAction(new BtnHandler());

		playBtn = new ToggleButton("Play");
		playBtn.selectedProperty().addListener(new playBtnListener());

		VBox generationBox = new VBox();
		Label genLabel = new Label("Generation");
		genNum = new Label("" + model.getGeneration());
		generationBox.setAlignment(Pos.CENTER);
		generationBox.getChildren().addAll(genLabel, genNum);

		VBox cellSizeSliderBox = new VBox();
		Label cellSizeSliderLabel = new Label("Cell Size");
		cellSizeSlider = new Slider();
		cellSizeSlider.setMajorTickUnit(10);
		cellSizeSlider.setShowTickMarks(true);
		cellSizeSlider.setMin(0);
		cellSizeSlider.setShowTickMarks(true);
		cellSizeSlider.setShowTickLabels(true);
		cellSizeSlider.setMajorTickUnit(20);
		cellSizeSlider.valueProperty().addListener(new SliderListener());
		cellSizeSliderBox.setAlignment(Pos.CENTER);
		cellSizeSliderBox.getChildren().addAll(cellSizeSliderLabel, cellSizeSlider);
		
		timer = new MyAnimationTimer();
		VBox delaySliderBox = new VBox();
		Label delaySliderLabel = new Label("Delay");
		delaySlider = new Slider();
		delaySlider.setMin(0);
		delaySlider.setMax(1000);
		delaySlider.setShowTickMarks(true);
		delaySlider.setShowTickLabels(true);
		delaySlider.setMajorTickUnit(200);
		delaySlider.valueProperty().addListener(new SliderListener());
		delaySliderBox.setAlignment(Pos.CENTER);
		delaySliderBox.getChildren().addAll(delaySliderLabel, delaySlider);

		bottom.setSpacing(10);
		bottom.setPadding(new Insets(8));
		bottom.setAlignment(Pos.CENTER_LEFT);
		bottom.getChildren().addAll(clearBtn, nextGenBtn, playBtn, generationBox, delaySliderBox, cellSizeSliderBox);

		BackgroundFill botFill = new BackgroundFill(Color.GRAY, new CornerRadii(0), new Insets(0));
		bottom.setBackground(new Background(botFill));
		bottom.setAlignment(Pos.CENTER);

		// Menu
		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("File");
		open = new MenuItem("Open");
		save = new MenuItem("Save");

		open.setOnAction(new BtnHandler());
		save.setOnAction(new BtnHandler());
		menu.getItems().addAll(open, save);
		menuBar.getMenus().add(menu);

		// Sizing the center
		centerPane = new BorderPane();
		centerPane.setCenter(view);

		// Set Pane
		
		
		root.setTop(menuBar);
		root.setBottom(bottom);
		root.setCenter(centerPane);

		Scene scene = new Scene(root);
		stage.setScene(scene);

		stage.show();
		configureTileSize();
	}

	@Override
	public void generationChanged(int oldVal, int newVal) {
		model.addGenerationListener(this);
	}

	private void configureTileSize() {
		double possTileHeight = centerPane.getHeight() / model.getNumRows();
		double possTileWidth = centerPane.getWidth() / model.getNumCols();
		double tileSize;

		if (possTileHeight > possTileWidth) {
			tileSize = possTileWidth;
		} else {
			tileSize = possTileHeight;
		}
		view.setTileSize(tileSize);
		cellSizeSlider.setMax(tileSize);
	}

	public void eraseObject(int row, int col) {
		if (row >= 0 && row < model.getNumRows() && col >= 0 && col < model.getNumCols() && model.getValueAt(row, col).equals(true)) {
			model.setValueAt(row, col, false);
			eraseObject(row + 1, col);
			eraseObject(row, col + 1);
			eraseObject(row - 1, col);
			eraseObject(row, col - 1);
		}
	}
	
	private class MouseGridHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {

			if(event.getClickCount() == 2) {
				int row = view.rowForYPos(event.getY());
				int col = view.colForXPos(event.getX());
				eraseObject(row, col);
			}
			else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					int row = view.rowForYPos(event.getY());
					int col = view.colForXPos(event.getX());
					if (row >= 0 && row < model.getNumRows() && col >= 0 && col < model.getNumCols()) {
						model.setValueAt(row, col, true);
					}
				} else if (event.getButton().equals(MouseButton.SECONDARY)) {
					int row = view.rowForYPos(event.getY());
					int col = view.colForXPos(event.getX());
					if (row >= 0 && row < model.getNumRows() && col >= 0 && col < model.getNumCols()) {
						model.setValueAt(row, col, false);
					}
				}
			} else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					int row = view.rowForYPos(event.getY());
					int col = view.colForXPos(event.getX());
					if (row >= 0 && row < model.getNumRows() && col >= 0 && col < model.getNumCols()) {
						model.setValueAt(row, col, true);
					}
				} else if (event.getButton().equals(MouseButton.SECONDARY)) {
					int row = view.rowForYPos(event.getY());
					int col = view.colForXPos(event.getX());
					if (row >= 0 && row < model.getNumRows() && col >= 0 && col < model.getNumCols()) {
						model.setValueAt(row, col, false);
					}
				}
			}
		}
	}

	private class BtnHandler implements EventHandler<ActionEvent> {

		private Scanner scanner;

		@Override
		public void handle(ActionEvent event) {
			int row;
			int col;
			if (event.getSource().equals(open)) {
				fileChooser = new FileChooser();
				fileChooser.setTitle("Open Text File");
				fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
				File selectedFile = fileChooser.showOpenDialog(null);
				if (selectedFile != null) {
					try {
						scanner = new Scanner(selectedFile);
					} catch (FileNotFoundException error) {
						System.out.println(error);
					}

					int numRows = scanner.nextInt();
					int numCols = scanner.nextInt();

					Boolean[][] loadGrid = new Boolean[numRows][numCols];
					for (row = 0; row < numRows; row++) {
						for (col = 0; col < numCols; col++) {
							String value = scanner.next();
							if (value.equals("X")) {
								loadGrid[row][col] = true;
							} else if (value.equals("O")) {
								loadGrid[row][col] = false;
							}
						}
					}
					model.setGrid(loadGrid);
					model.setGeneration(0);
					genNum.setText("" + model.getGeneration());
					configureTileSize();
				}
			} else if (event.getSource().equals(nextGenBtn)) {
				model.nextGeneration();
				genNum.setText("" + model.getGeneration());
			} else if (event.getSource().equals(save)) {
				FileWriter writer;
				try {
					writer = new FileWriter("test.txt");
					writer.write(model.getNumRows() + " " + model.getNumCols() + "\r\n");
					for (row = 0; row < model.getNumRows(); row++) {
						String rowStr = "";
						for (col = 0; col < model.getNumCols(); col++) {
							if(model.getValueAt(row, col))
								rowStr += "X ";
							else
								rowStr += "O ";
						}
						writer.write(rowStr.substring(0, rowStr.length() - 1) + "\r\n");
					}
					writer.close();
				} catch (IOException err) {
					System.out.println(err);
				}

			}
		}

	}

	private class SliderListener implements ChangeListener<Number> {

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if (observable.equals(cellSizeSlider.valueProperty())) {
				view.setTileSize((double) newValue);
			}
			else if(observable.equals(delaySlider.valueProperty())) {
				Number delay = newValue.doubleValue() * 1e6;
				timer.setDelay(delay.longValue());
			}
		}

	}

	private class playBtnListener implements ChangeListener<Boolean> {

		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

			if (newValue) {
				timer.start();
				playBtn.setText("Stop");
			} else {
				timer.stop();
				playBtn.setText("Play");
			}
		}

	}

	private class MyAnimationTimer extends AnimationTimer {

		private long delay = (long) 1e9;
		private long previousTime;

		@Override
		public void handle(long now) {
			if (now - previousTime >= delay) {
				model.nextGeneration();
				genNum.setText("" + model.getGeneration());
				previousTime = now;
			}
		}
		
		public void setDelay(long delay) {
			this.delay = delay;
		}
	}
}
