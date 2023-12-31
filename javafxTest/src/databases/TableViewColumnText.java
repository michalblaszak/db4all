package databases;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class TableViewColumnText<TRowRec> extends TableColumn<TRowRec, String>{
    TableViewColumnText(String name,
    		double minWidth,
    		Callback<CellDataFeatures<TRowRec, String>, ObservableValue<String>> valueFactory,
    		EventHandler<TableColumn.CellEditEvent<TRowRec,String>> onEditCommit) {
        super(name);

        Callback<TableColumn<TRowRec, String>, TableCell<TRowRec, String>> cellFactory
        	= (TableColumn<TRowRec, String> param) -> new StringEditingCell();

    	setMinWidth(minWidth);
        setCellValueFactory(valueFactory);
        setCellFactory(cellFactory);
        setOnEditCommit(onEditCommit);
    }

    private class StringEditingCell extends TableCell<TRowRec, String> {

        private TextField textField;

        private StringEditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(item);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
//                        setGraphic(null);
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnAction((e) -> commitEdit(textField.getText()));
            textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (!newValue) {
//                    System.out.println("Commiting " + textField.getText());
                    commitEdit(textField.getText());
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }
}
