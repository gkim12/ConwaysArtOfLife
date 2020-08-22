import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BooleanGridPane extends Group implements GridListener<Boolean> {
	private double tileSize;
	private Color trueColor;
	private Color falseColor;
	private Rectangle[][] cells;
	private GridModel<Boolean> model;
	
	public BooleanGridPane() {
		this.model = null;
		this.cells = null;
		this.tileSize = 10;
		this.trueColor = Color.BLACK;
		this.falseColor = Color.WHITE;
	}
	
	public void setTrueColor(Color color) {
		this.trueColor = color;
		for (int row = 0; row < model.getNumRows(); row++) {
			 for (int col = 0; col < model.getNumCols(); col++) {
				 if (model.getValueAt(row, col)) cells[row][col].setFill(trueColor);
			 }
		}
	}
	
	public Color getTrueColor() {
		return this.trueColor;
	}
	
	public void setFalseColor(Color color) {
		this.falseColor = color;
		for (int row = 0; row < model.getNumRows(); row++) {
			 for (int col = 0; col < model.getNumCols(); col++) {
				 if (!model.getValueAt(row, col)) cells[row][col].setFill(falseColor);
			 }
		}
	}
	
	public Color getFalseColor() {
		return this.falseColor;
	}
	
	public void setTileSize(double size) {
		this.tileSize = size;
		resetCells();
	}
	
	public double getTileSize() {
		return this.tileSize;
	}
	
	public void setModel(GridModel<Boolean> model) {
		model.addListener(this);
		this.model = model;
		resetCells();
	}
	
	public void resetCells() {
		getChildren().remove(0, getChildren().size());
		cells = new Rectangle[model.getNumRows()][model.getNumCols()];
		for (int row = 0; row < model.getNumRows(); row++) {
			for (int col = 0; col < model.getNumCols(); col++) {
				Rectangle cell = new Rectangle(tileSize, tileSize);
				cell.setFill(model.getValueAt(row, col) ? trueColor : falseColor);
				cell.setX(tileSize * col);
				cell.setY(tileSize * row);
				cell.setStroke(Color.BLACK);
				cell.setStrokeWidth(1);
				getChildren().add(cell);
				cells[row][col] = cell;
			}
		}
		
	}
	
	public Rectangle cellAtGridCoords(int row, int col) {
		return cells[row][col];
	}
	
	public double xPosForCol(int col) {
		return col * tileSize;
	}
	
	public double yPosForRow(int row) {
		return row * tileSize;
	}
	
	public int colForXPos(double x) {
		return (int)(x / tileSize);
	}
	
	public int rowForYPos(double y) {
		return (int)(y / tileSize);
	}

	@Override
	public void cellChanged(int row, int col, Boolean oldVal, Boolean newVal) {
		cells[row][col].setFill(newVal ? trueColor : falseColor);
	}

	@Override
	public void gridReplaced() {
		resetCells();
	}
	
}
