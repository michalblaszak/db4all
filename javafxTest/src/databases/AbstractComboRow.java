package databases;

public abstract class AbstractComboRow {
    public static int getColCount() { return 0; } 
	public abstract String getColValue(int col);
	public abstract String getPreferredColValue();
}
