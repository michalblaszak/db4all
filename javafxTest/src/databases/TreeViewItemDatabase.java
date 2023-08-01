package databases;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.lang.reflect.InvocationTargetException;

import databases.dbAPI.Dod.*;

public class TreeViewItemDatabase {
	public enum DBObjectType {
		ROOT,
		DATABASE,
		TABLE
	}
	
	private final DBObjectType objectType;
	private StringProperty name;
	private ObjectProperty rec;
	
//	public TreeViewItemDatabase(DBObjectType objectType) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
//		this.objectType = objectType;
//		setRec(clazz.getDeclaredConstructor().newInstance());
//	}
//	
	public TreeViewItemDatabase(DBObjectType objectType, String name, Object rec) {
		this.objectType = objectType;
		setRec(rec);
		setName(name);
	}
	
	public DBObjectType getobjectType() { return objectType; }
	
	public void setName(String name) { nameProperty().set(name); }
	public String getName() { return nameProperty().get(); }
	
	public void setRec(Object rec) { recProperty().set(rec); }
	public Object getRec() { return recProperty().get(); }
	
	public void setNameRec(String name, Object rec) {
		nameProperty().set(name);
		recProperty().set(rec);
	}
	
	public StringProperty nameProperty() {
		if (name == null) name = new SimpleStringProperty(this, "name");
		return name;
	}
	
	public ObjectProperty recProperty() {
		if (rec == null) rec = new SimpleObjectProperty(this, "rec");
		return rec;
	}
}
