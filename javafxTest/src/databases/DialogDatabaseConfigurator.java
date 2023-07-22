package databases;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

import databases.DialogAbstractDialogAddEdit.EditMode;
import databases.dbAPI.*;
import databases.dbAPI.Globals.CallStatus;

public class DialogDatabaseConfigurator extends Stage {
	final TreeTableView<TreeViewItemDatabase> treeTable;
	
	public DialogDatabaseConfigurator(Stage parent) {
		this.setTitle(Translations.DB_CONFIGURATOR.getText());
		this.initOwner(parent);
		this.initModality(Modality.APPLICATION_MODAL);
		
		treeTable = new TreeTableView<>();
		treeTable.setShowRoot(false);
		
		buildTree();
		
		HBox.setHgrow(treeTable, Priority.ALWAYS);

		Button btnClose = new Button(Translations.CLOSE.getText());
		btnClose.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				hide();
			}
		});
		
		Label lblDatabase = new Label(Translations.DATABASE.getText());
		Button btnAddDatabase = new Button(Translations.ADD_DATABASE.getText());
		Button btnEditDatabase = new Button(Translations.EDIT_DATABASE.getText());
		Button btnDeleteDatabase = new Button(Translations.DELETE_DATABASE.getText());
		
		Label lblTable = new Label(Translations.TABLE.getText());
		Button btnAddTable = new Button(Translations.ADD_TABLE.getText());
		Button btnEditTable = new Button(Translations.EDIT_TABLE.getText());
		Button btnDeleteTable = new Button(Translations.DELETE_TABLE.getText());
		
		btnAddDatabase.setMaxWidth(Double.MAX_VALUE);
		btnEditDatabase.setMaxWidth(Double.MAX_VALUE);
		btnDeleteDatabase.setMaxWidth(Double.MAX_VALUE);
		btnAddTable.setMaxWidth(Double.MAX_VALUE);
		btnEditTable.setMaxWidth(Double.MAX_VALUE);
		btnDeleteTable.setMaxWidth(Double.MAX_VALUE);
		
		btnAddDatabase.setOnAction(e -> {
			RecDatabase rec = new RecDatabase();
			var dlgAddDatabase = new DialogDatabaseAddEdit(parent, EditMode.ADD, rec);
			Optional ret = dlgAddDatabase.showAndReturn();
			
			if (ret.isPresent()) {
				addDatabaseToTree((RecDatabase)ret.get());
			}
		});

		btnEditDatabase.setOnAction(e -> {
			TreeItem<TreeViewItemDatabase> row = treeTable.getSelectionModel().getSelectedItem();
			RecDatabase rec = row.getValue().getRec();
			
			var dlgAddDatabase = new DialogDatabaseAddEdit(parent, EditMode.EDIT, rec);
			Optional ret = dlgAddDatabase.showAndReturn();
			
			if (ret.isPresent()) {
				row.getValue().setRec((RecDatabase)ret.get());
			}
		});

		var vbTreeButtons = new VBox(5, lblDatabase, btnAddDatabase, btnEditDatabase, btnDeleteDatabase,
										new Separator(),
										lblTable, btnAddTable, btnEditTable, btnDeleteTable);
		
		var hbTree = new HBox(5, treeTable, vbTreeButtons);
		
		VBox.setVgrow(hbTree, Priority.ALWAYS);
		
		var hbButtons = new HBox(5, btnClose);
		hbButtons.setAlignment(Pos.CENTER_RIGHT);
		
		var vbMain = new VBox(5, hbTree, hbButtons);
		vbMain.setPadding(new Insets(10, 10, 10, 10));
		
		Scene scene = new Scene(vbMain, 500, 500);
		this.setScene(scene);
	}
	
	private void buildTree() {
		AbstractStatus ret = DBConnection.getDatabases();
		
		if (ret.getStatus() == CallStatus.OK) {
			ArrayList<RecDatabase> databases = (ArrayList<RecDatabase>)((StatusOK)ret).getValue().getValue();
			TreeItem<TreeViewItemDatabase> root = new TreeItem<>(new TreeViewItemDatabase(TreeViewItemDatabase.DBObjectType.ROOT));
			for(RecDatabase r : databases) {
				TreeItem<TreeViewItemDatabase> el = new TreeItem<>(new TreeViewItemDatabase(TreeViewItemDatabase.DBObjectType.DATABASE, r.name(), r), new ImageView(Globals.database_icon));
				root.getChildren().add(el);
			}
//			TreeItem<TreeViewItemDatabase> el1 = new TreeItem<>(new TreeViewItemDatabase(TreeViewItemDatabase.DBObjectType.DATABASE, "DB1"), new ImageView(Globals.database_icon));
//			TreeItem<TreeViewItemDatabase> el2 = new TreeItem<>(new TreeViewItemDatabase(TreeViewItemDatabase.DBObjectType.TABLE, "Tab1"), new ImageView(Globals.table_icon));
//			el1.setGraphic(new ImageView(Globals.database_icon));
//			el1.getChildren().add(el2);
//			root.getChildren().add(el1);
			treeTable.setRoot(root);
			
			TreeTableColumn<TreeViewItemDatabase, String> nameCol = new TreeTableColumn<>(Translations.NAME.getText());
			treeTable.getColumns().setAll(nameCol);
			
			nameCol.setCellValueFactory(new TreeItemPropertyValueFactory("name"));
			treeTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		} else {
			Globals.showAlert(AlertType.ERROR, ret.getText(), Translations.ERROR.getText(), Translations.ERROR.getText());
		}
	}
	
	private void addDatabaseToTree(RecDatabase r) {
		TreeItem<TreeViewItemDatabase> el = new TreeItem<>(new TreeViewItemDatabase(TreeViewItemDatabase.DBObjectType.DATABASE, r.name(), r), new ImageView(Globals.database_icon));
		treeTable.getRoot().getChildren().add(el);
	}
}
