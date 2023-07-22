package databases;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class DialogAbstractDialogAddEdit extends Stage {
	public enum EditMode {
		ADD,
		EDIT
	}
	
	protected enum DialogStatus {
		RETURN_VALUE,
		RETURN_NO_VALUE
	}
	
	protected EditMode mode;
	VBox vbMain;
	protected DialogStatus dialogStatus = DialogStatus.RETURN_NO_VALUE;
	
	public DialogAbstractDialogAddEdit(Stage parent, EditMode mode, String title) {
		this.mode = mode;
		this.setTitle(title);
		this.initOwner(parent);
		this.initModality(Modality.APPLICATION_MODAL);

		Button btnSave = new Button(Translations.SAVE.getText());
		Button btnClose = new Button(Translations.CANCEL.getText());
		
		btnSave.setOnAction(ne -> { onSave(); });
		
		btnClose.setOnAction(e -> { hide(); });
		
		HBox hbButtons = new HBox(5, btnSave, btnClose);
		
		vbMain = new VBox(5, hbButtons);
		vbMain.setPadding(new Insets(10, 10, 10, 10));
		
		Scene scene = new Scene(vbMain);
		this.setScene(scene);
		sizeToScene();
	}
	
	protected abstract void onSave();
	
	protected void addMainWidget(Node node) {
		VBox.setVgrow(node, Priority.ALWAYS);
		vbMain.getChildren().add(0, node);
		sizeToScene();
	}
	
	public abstract Optional showAndReturn();
}
