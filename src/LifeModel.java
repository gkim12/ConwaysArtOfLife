import java.util.ArrayList;

public class LifeModel extends GridModel<Boolean> {

	private ArrayList<GenerationListener> listeners;
	private int[] horizontal = { -1, -1, -1, 0, 0, 1, 1, 1 };
	private int[] vertical = { 1, 0, -1, 1, -1, 1, 0, -1 };
	private int currentRow;
	private int currentCol;
	private int generation;

	public LifeModel(Boolean[][] grid) {
		super(grid);
		generation = 0;
		listeners = new ArrayList<>();
	}

	// Method that runs the Life simulation through the given generation
	// Generation 0 is the initial position, Generation 1 is the position after one
	// round of Life, etc...
	public void runLife(int numGenerations) {
		for (int gen = 0; gen < numGenerations; gen++) {
			nextGeneration();
		}
	}

	// Method that returns the number of living cells in the given row
	// or returns -1 if row is out of bounds. The first row is row 0.
	public int rowCount(int row) {
		int cellCounter = 0;
		for (int col = 0; col < getNumCols(); col++) {
			if (getValueAt(row, col) == true) {
				cellCounter++;
			}
		}
		return cellCounter;
	}

	// Method that returns the number of living cells in the given column
	// or returns -1 if column is out of bounds. The first column is column 0.
	public int colCount(int col) {
		int cellCounter = 0;
		for (int row = 0; row < getNumRows(); row++) {
			if (getValueAt(row, col) == true) {
				cellCounter++;
			}
		}
		return cellCounter;
	}

	// Method that returns the total number of living cells on the board
	public int totalCount() {
		int cellCounter = 0;
		for (int row = 0; row < getNumRows(); row++) {
			for (int col = 0; col < getNumCols(); col++) {
				if (getValueAt(row, col) == true) {
					cellCounter++;
				}
			}
		}
		return cellCounter;
	}

	// Advances Life forward one generation
	public void nextGeneration() {
		setGeneration(generation + 1);
		Boolean[][] nextGen = new Boolean[getNumRows()][getNumCols()];
		for (int row = 0; row < getNumRows(); row++) {
			for (int col = 0; col < getNumCols(); col++) {
				currentRow = row;
				currentCol = col;
				switch (numLivingSurround()) {
				case 2:
					if (getValueAt(row, col) == true) {
						nextGen[row][col] = true;
					} else {
						nextGen[row][col] = false;
					}
					break;
				case 3:
					nextGen[row][col] = true;
					break;
				default:
					nextGen[row][col] = false;
					break;
				}
			}
		}

		for (int row = 0; row < getNumRows(); row++) {
			for (int col = 0; col < getNumCols(); col++) {
				setValueAt(row, col, nextGen[row][col]);
			}
		}
	}

	private boolean isLivingSurround(int move) {
		int possRow = currentRow + vertical[move];
		int possCol = currentCol + horizontal[move];

		return possCol >= 0 && possCol < getNumCols() && possRow >= 0 && possRow < getNumRows()
				&& getValueAt(possRow, possCol) == true;
	}

	private int numLivingSurround() {
		int livingCells = 0;
		for (int i = 0; i < 8; i++) {
			if (isLivingSurround(i)) {
				livingCells++;
			}
		}
		return livingCells;
	}
	
	public void addGenerationListener(GenerationListener l) {
		listeners.add(l);
	}
	
	public void removeGenerationListener(GenerationListener l) {
		listeners.remove(l);
	}
	
	public void setGeneration(int gen) {
		
		for(int i = 0; i < listeners.size(); i++) {
			listeners.get(i).generationChanged(generation, gen);
		}
		generation = gen;
	}
	
	public int getGeneration() {
		return generation;
	}
}
