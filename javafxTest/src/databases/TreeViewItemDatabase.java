package databases;

import databases.dbAPI.RecDatabase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TreeViewItemDatabase {
	public enum DBObjectType {
		ROOT,
		DATABASE,
		TABLE
	}
	
	private final DBObjectType objectType;
	private StringProperty name;
	private ObjectProperty<RecDatabase> rec;
	
	public TreeViewItemDatabase(DBObjectType objectType) {
		this.objectType = objectType;
		setRec(new RecDatabase());
	}
	
	public TreeViewItemDatabase(DBObjectType objectType, String name, RecDatabase rec) {
		this.objectType = objectType;
		setRec(rec);
		setName(name);
	}
	
	public void setName(String name) { nameProperty().set(name); }
	public String getName() { return nameProperty().get(); }
	
	public void setRec(RecDatabase rec) { 
		recProperty().set(rec);
		setName(rec.name());
	}
	
	public RecDatabase getRec() { return recProperty().get(); }
	
	public StringProperty nameProperty() {
		if (name == null) name = new SimpleStringProperty(this, "name");
		return name;
	}
	
	public ObjectProperty<RecDatabase> recProperty() {
		if (rec == null) rec = new SimpleObjectProperty<RecDatabase>(this, "rec");
		return rec;
	}
}
