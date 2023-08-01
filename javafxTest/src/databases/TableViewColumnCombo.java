package databases;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import databases.AbstractComboRow;
//import databases.PaneColumns.DBTableColumn;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class TableViewColumnCombo<TRowRec, TComboRow extends AbstractComboRow> extends TableColumn<TRowRec, TComboRow> {
	ObservableList<TComboRow> comboData;
	Class<TComboRow> clazz;
    int comboBoxColumnCount=0;
    
	TableViewColumnCombo(String name,
    		double minWidth,
    		Callback<CellDataFeatures<TRowRec, TComboRow>, ObservableValue<TComboRow>> valueFactory,
    		EventHandler<TableColumn.CellEditEvent<TRowRec, TComboRow>> onEditCommit,
    		ObservableList<TComboRow> comboData,
    		int comboBoxColumnCount) {
        super(name);
        this.comboBoxColumnCount = comboBoxColumnCount;
        this.comboData = comboData;

        Callback<TableColumn<TRowRec, TComboRow>, TableCell<TRowRec, TComboRow>> cellFactory
        	= (TableColumn<TRowRec, TComboRow> param) -> new ComboBoxEditingCell();

    	setMinWidth(minWidth);
        setCellValueFactory(valueFactory);
        setCellFactory(cellFactory);
        setOnEditCommit(onEditCommit);
    }

    private class ComboBoxEditingCell extends TableCell<TRowRec, TComboRow> {

        private ComboBox<TComboRow> comboBox;

        private ComboBoxEditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createComboBox();
                setText(null);
                setGraphic(comboBox);
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            TComboRow _t = getType();
            setText(_t == null ? "" : _t.getPreferredColValue());
            setGraphic(null);
        }

        @Override
        public void updateItem(TComboRow item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                TComboRow _t = getType();

                if (isEditing()) {
                    if (comboBox != null) {
                        comboBox.setValue(_t);
                    }
                    setText(_t == null ? "" : _t.getPreferredColValue());
                    setGraphic(comboBox);
                } else {
                    setText(_t == null ? "" : _t.getPreferredColValue());
                    setGraphic(null);
                }
            }
        }


        private void createComboBox() {
            Callback<ListView<TComboRow>, ListCell<TComboRow>> comboFactory
            		= (ListView<TComboRow> param) -> new ComboBoxView(comboBoxColumnCount);
            
            comboBox = new ComboBox<>(comboData);
            comboBoxConverter(comboBox);
            comboBox.setCellFactory(comboFactory);
            comboBox.valueProperty().set(getType());
            comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            comboBox.setOnAction((e) -> {
                System.out.println("Committed: " + comboBox.getSelectionModel().getSelectedItem());
                commitEdit(comboBox.getSelectionModel().getSelectedItem());
            });
//            comboBox.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
//                if (!newValue) {
//                    commitEdit(comboBox.getSelectionModel().getSelectedItem());
//                }
//            });
        }

        private void comboBoxConverter(ComboBox<TComboRow> comboBox) {
            // Define rendering of the list of values in ComboBox drop down. 
            comboBox.setCellFactory((c) -> {
                return new ListCell<TComboRow>() {
                    @Override
                    protected void updateItem(TComboRow item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getPreferredColValue());
                        }
                    }
                };
            });
        }

//        public TComboRow getInstanceOfComboRow() 
//        		throws InstantiationException,
//				        IllegalAccessException,
//				        IllegalArgumentException,
//				        InvocationTargetException,
//				        NoSuchMethodException,
//				        SecurityException {
//            return clazz.getDeclaredConstructor().newInstance();
//         }
        
        private TComboRow getType() {
        	return getItem();
//        	try {
//        		return getItem() == null ? getInstanceOfComboRow() : getItem();
//        	} catch(Exception e) {
//        		
//        	}
        }
    }

    private class ComboBoxView extends ListCell<TComboRow> {
    	// Create our layout here to be reused for each ListCell
        GridPane gridPane = new GridPane();
        ArrayList<Label> labels = new ArrayList<Label>();
        final int columnCount;
//        Label lblName = new Label();
//        Label lblTitle = new Label();
//        Label lblLocation = new Label();

        // Static block to configure our layout
        private ComboBoxView() {
        	this.columnCount = 0;
        }
        
        public ComboBoxView(int columnCount)
        {
        	this.columnCount = columnCount;
        	for(int i=0; i<columnCount; i++) {
        		Label l = new Label();
        		labels.add(l);
        		gridPane.getColumnConstraints().add(new ColumnConstraints(100, 100, 100));
        		gridPane.add(l, i, 1);
        	}
            // Ensure all our column widths are constant
//            gridPane.getColumnConstraints().addAll(columnConstraints)
//                    new ColumnConstraints(100, 100, 100)
//                    new ColumnConstraints(100, 100, 100),
//                    new ColumnConstraints(100, 100, 100)
//            );

            
//            gridPane.add(lblName, 0, 1);
//            gridPane.add(lblTitle, 1, 1);
//            gridPane.add(lblLocation, 2, 1);

        }
    	
        @Override 
        public void updateItem(TComboRow item, boolean empty) {
            super.updateItem(item, empty);
            
            if (!empty && item != null) {
            	for(int i=0; i<columnCount; i++) {
            		labels.get(i).setText(item.getColValue(i));
            	}
//            	lblName.setText(item.getColValue());
//                lblTitle.setText(item.getAttr1());
//                lblLocation.setText(item.getAttr2());

                // Set this ListCell's graphicProperty to display our GridPane
                setGraphic(gridPane);
            }
            else {
                setGraphic(null);
            }
        }    	
    }

}
