package databases;

public class Variant {
	private final Object value;
	
	public Variant() { this.value = null; }
	public Variant(int v) { this.value = Integer.valueOf(v); }
	public Variant(Object v) { this.value = v; }
	
	int getInt() throws WrongTypeException {
		if (value instanceof Integer ) {
			return ((Integer)value).intValue();
		} else {
			throw new WrongTypeException(); 
		}
	}
	
	Object getValue() { return value; }
}
