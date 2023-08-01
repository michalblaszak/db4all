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
import databases.TreeViewItemDatabase.DBObjectType;
import databases.dbAPI.*;
import databases.dbAPI.Globals.CallStatus;
import databases.dbAPI.Dod.*;


public class DialogDatabaseConfigurator extends Stage {
	final Stage parent;
	final TreeTableView<TreeViewItemDatabase> treeTable;
	final Button btnAddDatabase;
	final Button btnEditDatabase;
	final Button btnDeleteDatabase;
	final Button btnAddTable;
	final Button btnEditTable;
	final Button btnDeleteTable;

	
	public DialogDatabaseConfigurator(Stage parent) {
		this.parent = parent;
		
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
		btnAddDatabase = new Button(Translations.ADD_DATABASE.getText());
		btnEditDatabase = new Button(Translations.EDIT_DATABASE.getText());
		btnDeleteDatabase = new Button(Translations.DELETE_DATABASE.getText());
		
		Label lblTable = new Label(Translations.TABLE.getText());
		btnAddTable = new Button(Translations.ADD_TABLE.getText());
		btnEditTable = new Button(Translations.EDIT_TABLE.getText());
		btnDeleteTable = new Button(Translations.DELETE_TABLE.getText());
		
		btnAddDatabase.setMaxWidth(Double.MAX_VALUE);
		btnEditDatabase.setMaxWidth(Double.MAX_VALUE);
		btnDeleteDatabase.setMaxWidth(Double.MAX_VALUE);
		btnAddTable.setMaxWidth(Double.MAX_VALUE);
		btnEditTable.setMaxWidth(Double.MAX_VALUE);
		btnDeleteTable.setMaxWidth(Double.MAX_VALUE);
		
		btnAddDatabase.setOnAction(e -> { onAddDatabase(e); });
		btnEditDatabase.setOnAction(e -> { onEditDatabase(e); });
		btnAddTable.setOnAction(e -> { onAddTable(e); });
		btnEditTable.setOnAction(e -> { onEditTable(e); });

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
		
		treeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			enableButtons();
		});
		
		enableButtons();
	}
	
	private void enableButtons() {
		TreeItem<TreeViewItemDatabase> cur_row = treeTable.getSelectionModel().getSelectedItem();
		
		if(cur_row != null) {
			switch (cur_row.getValue().getobjectType()) {
			case DATABASE:
				btnAddDatabase.setDisable(false);
				btnEditDatabase.setDisable(false);
				btnDeleteDatabase.setDisable(false);
				btnAddTable.setDisable(false);
				btnEditTable.setDisable(true);
				btnDeleteTable.setDisable(true);

				break;

			case TABLE:
				btnAddDatabase.setDisable(false);
				btnEditDatabase.setDisable(false);
				btnDeleteDatabase.setDisable(false);
				btnAddTable.setDisable(false);
				btnEditTable.setDisable(false);
				btnDeleteTable.setDisable(false);

				break;
			}
			
		} else {
			btnAddDatabase.setDisable(false);
			btnEditDatabase.setDisable(true);
			btnDeleteDatabase.setDisable(true);
			btnAddTable.setDisable(true);
			btnEditTable.setDisable(true);
			btnDeleteTable.setDisable(true);
		}

	}
	
	void onAddDatabase(ActionEvent e) {
		RecDatabase rec = new RecDatabase();
		var dlgAddDatabase = new DialogDatabaseAddEdit(parent, EditMode.ADD, rec);
		Optional ret = dlgAddDatabase.showAndReturn();
		
		if (ret.isPresent()) {
			addDatabaseToTree((RecDatabase)ret.get());
		}
	}
	
	void onEditDatabase(ActionEvent e) {
		TreeItem<TreeViewItemDatabase> cur_row = treeTable.getSelectionModel().getSelectedItem();
		if (cur_row != null) {
			TreeItem<TreeViewItemDatabase> db_row;

			switch (cur_row.getValue().getobjectType()) {
			case DATABASE:
				db_row = cur_row;
				break;
				
			case TABLE:
				db_row = cur_row.getParent();
				break;
				
			default:
				Globals.showAlert(parent, AlertType.ERROR, 
						Translations.WRONG_TYPE.getText(), 
						Translations.INTERNAL_ERROR.getText());
				return;
			}

			RecDatabase rec = (RecDatabase)db_row.getValue().getRec();
			
			var dlgEditDatabase = new DialogDatabaseAddEdit(parent, EditMode.EDIT, rec);
			Optional ret = dlgEditDatabase.showAndReturn();
			
			if (ret.isPresent()) {
				db_row.getValue().setNameRec(((RecDatabase)ret.get()).name(),
											(RecDatabase)ret.get());
			}
		} else { // Not selected or not a DATABASE
			Globals.showAlert(parent, AlertType.INFORMATION, 
					Translations.DATABASE_MUST_BE_SELECTED.getText(), 
					Translations.WRONG_SELECTION.getText());
		}
	}
	
	void onAddTable(ActionEvent e) {
		TreeItem<TreeViewItemDatabase> cur_row = treeTable.getSelectionModel().getSelectedItem();
		
		if (cur_row != null) {
			TreeItem<TreeViewItemDatabase> db_row;

			switch (cur_row.getValue().getobjectType()) {
			case DATABASE: 
				db_row = cur_row;
				break;
			
			case TABLE: 
				db_row = cur_row.getParent();
				break;
			
			default:
				Globals.showAlert(parent, AlertType.ERROR, 
						Translations.WRONG_TYPE.getText(), 
						Translations.INTERNAL_ERROR.getText());
				return;
			}
			
			int db_id = ((RecDatabase)db_row.getValue().getRec()).id();
			
			RecTable rec = new RecTable(-1, "", db_id);

			var dlgAddTable = new DialogTableAddEdit(parent, EditMode.ADD, rec);
			Optional ret = dlgAddTable.showAndReturn();
			
			if (ret.isPresent()) {
				addTableToTree((RecTable)ret.get());
			}
		} else { // Not selected or not a DATABASE
			Globals.showAlert(parent, AlertType.INFORMATION, 
					Translations.DATABASE_MUST_BE_SELECTED.getText(), 
					Translations.WRONG_SELECTION.getText());
		}
	}
	
	void onEditTable(ActionEvent e) {
		TreeItem<TreeViewItemDatabase> row = treeTable.getSelectionModel().getSelectedItem();
		if (row != null && row.getValue().getobjectType() == TreeViewItemDatabase.DBObjectType.TABLE) {
			RecTable rec = (RecTable)row.getValue().getRec();
			
			var dlgAddTable = new DialogTableAddEdit(parent, EditMode.EDIT, rec);
			Optional ret = dlgAddTable.showAndReturn();
			
			if (ret.isPresent()) {
				row.getValue().setNameRec(((RecTable)ret.get()).name(),
						(RecTable)ret.get());

			}
		} else { // Not selected or not a DATABASE
			Globals.showAlert(parent, AlertType.INFORMATION, 
					Translations.TABLE_MUST_BE_SELECTED.getText(), 
					Translations.WRONG_SELECTION.getText());
		}
	}
	
	private void buildTree() {
		TreeItem<TreeViewItemDatabase> root = new TreeItem<>(new TreeViewItemDatabase(TreeViewItemDatabase.DBObjectType.ROOT, "ROOT", new RecRoot()));
		treeTable.setRoot(root);
		
		TreeTableColumn<TreeViewItemDatabase, String> nameCol = new TreeTableColumn<>(Translations.NAME.getText());
		treeTable.getColumns().setAll(nameCol);
		
		nameCol.setCellValueFactory(new TreeItemPropertyValueFactory("name"));
		treeTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

		AbstractStatus ret_databases = DBConnection.getDatabases();

		if (ret_databases.getStatus() == CallStatus.OK) {
			ArrayList<RecDatabase> databases = (ArrayList<RecDatabase>)((StatusOK)ret_databases).getValue().getValue();
			for(RecDatabase r_db : databases) {
				TreeItem<TreeViewItemDatabase> el_db = new TreeItem<>(new TreeViewItemDatabase(TreeViewItemDatabase.DBObjectType.DATABASE, r_db.name(), r_db), new ImageView(Globals.database_icon));
				root.getChildren().add(el_db);
				
				AbstractStatus ret_tables = DBConnection.getTables(r_db.id());
				
				if (ret_tables.getStatus() == CallStatus.OK) {
					ArrayList<RecTable> tables = (ArrayList<RecTable>)((StatusOK)ret_tables).getValue().getValue();
					for (RecTable r_tab : tables) {
						TreeItem<TreeViewItemDatabase> el_tab = new TreeItem<>(new TreeViewItemDatabase(TreeViewItemDatabase.DBObjectType.TABLE, r_tab.name(), r_tab), new ImageView(Globals.table_icon));
						el_db.getChildren().add(el_tab);						
					}
				} else {
					Globals.showAlert(this, AlertType.ERROR, ret_tables.getText(), Translations.ERROR.getText());
					return;
				}
			}
		} else {
			Globals.showAlert(this, AlertType.ERROR, ret_databases.getText(), Translations.ERROR.getText());
		}
	}
	
	private void addDatabaseToTree(RecDatabase r) {
		TreeItem<TreeViewItemDatabase> el = new TreeItem<>(new TreeViewItemDatabase(TreeViewItemDatabase.DBObjectType.DATABASE, r.name(), r), new ImageView(Globals.database_icon));
		treeTable.getRoot().getChildren().add(el);
	}
	
	private void addTableToTree(RecTable r) {
		TreeItem<TreeViewItemDatabase> el = new TreeItem<>(new TreeViewItemDatabase(TreeViewItemDatabase.DBObjectType.TABLE, r.name(), r), new ImageView(Globals.table_icon));
		
		TreeItem<TreeViewItemDatabase> parent_row = treeTable.getSelectionModel().getSelectedItem();
		parent_row.getChildren().add(el);
	}
}
