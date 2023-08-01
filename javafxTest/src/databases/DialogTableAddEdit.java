package databases;

import java.util.Optional;

import databases.DialogAbstractDialogAddEdit.DialogStatus;
import databases.DialogAbstractDialogAddEdit.EditMode;
import databases.dbAPI.AbstractStatus;
import databases.dbAPI.DBConnection;
import databases.dbAPI.StatusOK;
import databases.dbAPI.Globals.CallStatus;
import databases.dbAPI.Dod.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class DialogTableAddEdit extends DialogAbstractDialogAddEdit {
	private TextField txtName;
	private final RecTable rec; // the input value
	private RecTable _ret_rec; // The value returned to the caller
	
	public DialogTableAddEdit(Stage parent, EditMode mode, RecTable rec) {
		super(parent, mode, mode == EditMode.ADD ? Translations.ADD_TABLE.getText() : Translations.EDIT_TABLE.getText());
		this.rec = rec;
		
		Label lblName = new Label(Translations.NAME_LABEL.getText());
		txtName = new TextField(mode == EditMode.EDIT ? rec.name() : "");
		
		PaneColumns columnsPane = new PaneColumns();
//		HBox.setHgrow(treeTable, Priority.ALWAYS);
	    GridPane.setHgrow(columnsPane, Priority.ALWAYS);
	    GridPane.setVgrow(columnsPane, Priority.ALWAYS);
		
	    ColumnConstraints col1 = new ColumnConstraints();
	    ColumnConstraints col2 = new ColumnConstraints();
	    col2.setHgrow(Priority.ALWAYS);
	    
		GridPane gridPaneMain = new GridPane();
		gridPaneMain.getColumnConstraints().addAll(col1, col2);
		
		gridPaneMain.setPadding(new Insets(10, 10, 10, 10));
		gridPaneMain.setVgap(5); 
	    gridPaneMain.setHgap(5);
	    gridPaneMain.setAlignment(Pos.CENTER_LEFT);
		
	    gridPaneMain.add(lblName, 0, 0);
	    gridPaneMain.add(txtName, 1, 0);
	    gridPaneMain.add(columnsPane, 0, 1, 2, 1);
	    
		addMainWidget(gridPaneMain);
	}

	@Override
	protected void onSave() {
		AbstractStatus ret;
		RecTable _rec;
		
		if (mode == EditMode.ADD) {
			_rec = new RecTable(-1, txtName.getText(), rec.db_id());
			ret = DBConnection.insertTable(_rec);
		} else { // EDIT
			_rec = new RecTable(rec.id(), txtName.getText(), rec.db_id());
			ret = DBConnection.updateTable(_rec);
		}
		
		if (ret.getStatus() == CallStatus.OK) {
			dialogStatus = DialogStatus.RETURN_VALUE;
			if (mode == EditMode.ADD)
				_ret_rec = (RecTable)((StatusOK)ret).getValue().getValue();
			else
				_ret_rec = _rec;
		} else {
			dialogStatus = DialogStatus.RETURN_NO_VALUE;
			Globals.showAlert(parent, AlertType.ERROR, ret.getText(), Translations.ERROR.getText(), Translations.ERROR.getText());
		}
		hide();
	}

	@Override
	public Optional showAndReturn() {
		showAndWait();
		
		if (dialogStatus == DialogStatus.RETURN_VALUE) {
			return Optional.of(_ret_rec);
		} else { // DialogStatus.RETURN_VALUE
			return Optional.empty();
		}
	}
}
