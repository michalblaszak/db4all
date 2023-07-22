package databases;

import java.util.Optional;

import databases.dbAPI.*;
import databases.dbAPI.Globals.CallStatus;
import databases.dbAPI.RecDatabase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DialogDatabaseAddEdit extends DialogAbstractDialogAddEdit {
	private TextField txtName;
	private final RecDatabase rec; // the input value
	private RecDatabase _ret_rec; // The value returned to the caller
	
	public DialogDatabaseAddEdit(Stage parent, EditMode mode, RecDatabase rec) {
		super(parent, mode, mode == EditMode.ADD ? Translations.ADD_DATABASE.getText() : Translations.EDIT_DATABASE.getText());
		this.rec = rec;
		
		Label lblName = new Label(Translations.NAME_LABEL.getText());
		txtName = new TextField(mode == EditMode.EDIT ? rec.name() : "");
		
		GridPane gridPaneMain = new GridPane();
		gridPaneMain.setPadding(new Insets(10, 10, 10, 10));
		gridPaneMain.setVgap(5); 
	    gridPaneMain.setHgap(5);
	    gridPaneMain.setAlignment(Pos.CENTER_LEFT);
		
	    gridPaneMain.add(lblName, 0, 0);
	    gridPaneMain.add(txtName, 1, 0);
	    
		addMainWidget(gridPaneMain);
	}

	@Override
	protected void onSave() {
		AbstractStatus ret;
		
		if (mode == EditMode.ADD) {
			RecDatabase _rec = new RecDatabase(-1, txtName.getText());
			ret = DBConnection.insertDatabase(_rec);
		} else { // EDIT
			RecDatabase _rec = new RecDatabase(rec.id(), txtName.getText());
			ret = DBConnection.updateDatabase(_rec);
		}
		
		if (ret.getStatus() == CallStatus.OK) {
			dialogStatus = DialogStatus.RETURN_VALUE;
			_ret_rec = (RecDatabase)((StatusOK)ret).getValue().getValue();
		} else {
			dialogStatus = DialogStatus.RETURN_NO_VALUE;
			Globals.showAlert(AlertType.ERROR, ret.getText(), Translations.ERROR.getText(), Translations.ERROR.getText());
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
