package databases;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class TableViewColumnCheckbox <TRowRec> extends TableColumn<TRowRec, Boolean> {
	TableViewColumnCheckbox(String name,
    		double minWidth,
    		Callback<CellDataFeatures<TRowRec, Boolean>, ObservableValue<Boolean>> valueFactory,
    		EventHandler<TableColumn.CellEditEvent<TRowRec, Boolean>> onEditCommit) {
        super(name);

        Callback<TableColumn<TRowRec, Boolean>, TableCell<TRowRec, Boolean>> cellFactory
        	= (TableColumn<TRowRec, Boolean> param) -> new CheckBoxEditingCell();
        
    	setMinWidth(minWidth);
        setCellValueFactory(valueFactory);
        setCellFactory(cellFactory);
        setOnEditCommit(onEditCommit);
    }

    class CheckBoxEditingCell extends TableCell<TRowRec, Boolean> {
        private CheckBox checkBox;
        public CheckBoxEditingCell() {
            checkBox = new CheckBox();
            checkBox.setDisable(true);
            checkBox.selectedProperty().addListener(new ChangeListener<Boolean> () {
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if(isEditing())
                        commitEdit(newValue == null ? false : newValue);
                }
            });
            this.setGraphic(checkBox);
            this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            this.setEditable(true);
        }
        
        @Override
        public void startEdit() {
            super.startEdit();
            if (isEmpty()) {
                return;
            }
            checkBox.setDisable(false);
            checkBox.requestFocus();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            checkBox.setDisable(true);
        }
        
        @Override
        public void commitEdit(Boolean value) {
            super.commitEdit(value);
            checkBox.setDisable(true);
        }
        
        @Override
        public void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!isEmpty()) {
                checkBox.setSelected(item);
            }
        }
    }
}
