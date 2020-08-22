import java.util.ArrayList;
import java.util.Arrays;

public class GridModel<T> {

	private T[][] grid;
	private ArrayList<GridListener<T>> listeners;
	
	public GridModel(T[][] grid) {
		this.grid = grid;
		this.listeners = new ArrayList<>();
	}
	
	public void addListener(GridListener<T> l) {
		if (!listeners.contains(l)) {
			listeners.add(l);
		}
	}
	
	public void removeListener(GridListener<T> l) {
		listeners.remove(l);
	}
	
	public int getNumRows() {
		return this.grid.length;
	}
	
	public int getNumCols() {
		return this.grid.length > 0 ? this.grid[0].length : 0;
	}
	
	public T getValueAt(int row, int col) {
		return this.grid[row][col];
	}
	
	public void setValueAt(int row, int col, T val) {
		T oldVal = this.grid[row][col];
		this.grid[row][col] = val;
		if (!oldVal.equals(val)) {
			for (GridListener<T> l : listeners) {
				l.cellChanged(row, col, oldVal, val);
			}
		}
	}

	public void setGrid(T[][] grid) {
		if (grid == null) throw new IllegalArgumentException("grid cannot be null.");
		this.grid = grid;
		for (GridListener<T> l : listeners) {
			l.gridReplaced();
		}
	}

	@Override
	public String toString() {
		return "GridModel [grid=" + Arrays.toString(grid) + ", listeners=" + listeners + "]";
	}
	
}
