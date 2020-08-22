
public interface GridListener<T> {
	public void cellChanged(int row, int col, T oldVal, T newVal);
	public void gridReplaced();
}
