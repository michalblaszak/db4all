package databases;

import java.util.Date;

//import databases.Main.ComboBoxEditingCell;
//import databases.Main.ComboBoxView;
//import databases.Main.DateEditingCell;
//import databases.Main.EditingCell;
//import databases.Main.Person;
//import databases.Main.Typ;
//import databases.TableViewColumnText;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

public class PaneColumns extends BorderPane {
	private TableView<DBTableColumn> table = new TableView<>();
    private final ObservableList<DBTableColumn> data
	    = FXCollections.observableArrayList(
	            new DBTableColumn("Jacob", new Type("INTEGER", 1), true, true),
	            new DBTableColumn("Urs", new Type("INTEGER", 1), false, false));

    private final ObservableList<Type> typeData
    	= FXCollections.observableArrayList(
    			new Type("INTEGER", 1),
    			new Type("TEXT", 2));

	
	public PaneColumns() {
        table.setEditable(true);

        TableViewColumnText<DBTableColumn> nameCol = new TableViewColumnText<DBTableColumn>("Name", 100, 
        		cellData -> cellData.getValue().nameProperty(),
        		(TableColumn.CellEditEvent<DBTableColumn, String> t) -> {
                    ((DBTableColumn) t.getTableView().getItems()
                    .get(t.getTablePosition().getRow()))
                    .setName(t.getNewValue());
                });

        TableViewColumnCombo<DBTableColumn, Type> typeCol = new TableViewColumnCombo<DBTableColumn, Type>("Type", 100,
        		cellData -> cellData.getValue().typeProperty(),
        		(TableColumn.CellEditEvent<DBTableColumn, Type> t) -> {
                    ((DBTableColumn) t.getTableView().getItems()
                    .get(t.getTablePosition().getRow()))
                    .setType(t.getNewValue());
                },
        		typeData,
        		Type.getColCount());
	
        TableColumn<DBTableColumn, Boolean> noNullCol = new TableViewColumnCheckbox<DBTableColumn>("Not null", 100,
        		cellData -> cellData.getValue().notNullProperty(),
        		(TableColumn.CellEditEvent<DBTableColumn, Boolean> t) -> {
                    ((DBTableColumn) t.getTableView().getItems()
                    .get(t.getTablePosition().getRow()))
                    .setNotNull(t.getNewValue());
                });
        
        TableColumn<DBTableColumn, Boolean> primaryKeyCol = new TableViewColumnCheckbox<DBTableColumn>("PK", 100,
        		cellData -> cellData.getValue().PKProperty(),
        		(TableColumn.CellEditEvent<DBTableColumn, Boolean> t) -> {
                    ((DBTableColumn) t.getTableView().getItems()
                    .get(t.getTablePosition().getRow()))
                    .setPK(t.getNewValue());
                });
        
        table.setItems(data);
        table.getColumns().addAll(nameCol, typeCol, noNullCol, primaryKeyCol);
        
        setCenter(table);
	}

    public static class Type extends AbstractComboRow {
        private final SimpleStringProperty typeName;
        private final SimpleIntegerProperty attr1;
        
        public Type(String typ, int attr1) {
            this.typeName = new SimpleStringProperty(typ);
            this.attr1 = new SimpleIntegerProperty(attr1);
        }

        public static int getColCount() { return 2; }
        
        public String getTypeName() { return this.typeName.get();  }
        public StringProperty typeNameProperty() { return this.typeName; }
        public void setTypeName(String typeName) { this.typeName.set(typeName); }

        public int getAttr1() { return this.attr1.get(); }        
        public IntegerProperty attr1Property() { return this.attr1; }
        public void setAttr1(int attr1) { this.attr1.set(attr1); }
        
        @Override
        public String toString() { return typeName.get(); }

        @Override
        public String getColValue(int i) {
        	return switch(i) {
		        		case 0 -> getTypeName();
		        		case 1 -> String.valueOf(getAttr1());
		        		default -> new String();
		        	};
        }
        
        @Override
        public String getPreferredColValue() { return getColValue(0); }
    }

    public static class DBTableColumn {
        private final SimpleStringProperty name;
        private final SimpleObjectProperty<Type> type;
        private final SimpleBooleanProperty notNull;
        private final SimpleBooleanProperty PK;

        public DBTableColumn(String name, Type type, Boolean notNull, Boolean PK) {
            this.name = new SimpleStringProperty(name);
            this.type = new SimpleObjectProperty<Type>(type);
            this.notNull = new SimpleBooleanProperty(notNull);
            this.PK = new SimpleBooleanProperty(PK);
        }

        public String getName() { return name.get(); }
        public StringProperty nameProperty() { return this.name; }
        public void setName(String name) { this.name.set(name); }
        
        public Type getType() { return type.get(); }
        public ObjectProperty typeProperty() { return this.type; }
        public void setType(Type type) { this.type.set(type); }
        
        public Boolean getNotNull() { return notNull.get(); }
        public BooleanProperty notNullProperty() { return this.notNull; }
        public void setNotNull(Boolean notNull) { this.notNull.set(notNull); }
        
        public Boolean getPK() { return PK.get(); }
        public BooleanProperty PKProperty() { return this.PK; }
        public void setPK(Boolean PK) { this.PK.set(PK); }
    }
}
