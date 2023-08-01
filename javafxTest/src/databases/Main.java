package databases;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import databases.dbAPI.AbstractStatus;
import databases.dbAPI.DBConnection;
import databases.dbAPI.Globals.CallStatus;
import databases.dbAPI.StatusFail;
import databases.dbAPI.StatusOK;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author Hasan Selman Kara
 */
public class Main extends Application {
	
	private Stage stage = null;

    private TableView<Person> table = new TableView<>();
    private final ObservableList<Typ> typData
            = FXCollections.observableArrayList(
                    new Typ("Hund", "F1", "F2"),
                    new Typ("Fuchs", "G1", "G2"),
                    new Typ("Esel", "H1", "H2"));
    private final ObservableList<Person> data
            = FXCollections.observableArrayList(
                    new Person("Jacob", typData.get(0), new Date()),
                    new Person("Urs", typData.get(1), new Date()),
                    new Person("Hans", typData.get(2), new Date()),
                    new Person("Ueli", typData.get(2), new Date()));

    private void checkDatabase() {
    	checkGUI();
    	
        AbstractStatus ret = DBConnection.connect();
        
        if (ret.getStatus() != CallStatus.OK) {
			Globals.showAlert(stage, AlertType.ERROR, ret.getText(), Translations.ERROR.getText(), Translations.ERROR.getText());
            
            stage.close();
        }

        ret = DBConnection.countDatabases();
        
        if (ret.getStatus() == CallStatus.OK) {
        	try {
        		int db_count = ((StatusOK)ret).getValue().getInt();
        		
        		if (db_count == 0) {
        			ButtonType btn_yes = new ButtonType(Translations.YES.getText(), ButtonBar.ButtonData.OK_DONE);
        			ButtonType btn_no = new ButtonType(Translations.NO.getText(), ButtonBar.ButtonData.CANCEL_CLOSE);
        			Alert a = new Alert(AlertType.INFORMATION, Translations.NO_DATABASES.getText(), btn_yes, btn_no);
                	a.setTitle(Translations.INFORMATION.getText());
                	a.setHeaderText(Translations.LACK_OF_DATABASE.getText());
        			Optional<ButtonType> r = a.showAndWait();
        			
        			if (r.orElse(btn_no) == btn_yes) {
            			var dlg = new DialogDatabaseConfigurator(stage);
            			dlg.showAndWait();
        			}
        		}
        	} catch (WrongTypeException e) {
    			Globals.showAlert(stage, AlertType.ERROR, ret.getText(), Translations.ERROR.getText(), Translations.ERROR.getText());
                
                stage.close();
        	}
        } else {
			Globals.showAlert(stage, AlertType.ERROR, ret.getText(), Translations.ERROR.getText(), Translations.ERROR.getText());
        }
    }
    
    private void checkGUI() {
    	if (stage == null) {
    		System.out.println(Translations.GUI_NOT_INITIALIZED.getText());
    		System.exit(1);
    	}
    }
    
    private void stageMaker() {
    	checkGUI();
    	
        MenuBar mb = setupMenu();

        final Label label = new Label("Address Book");
        label.setFont(new Font("Arial", 20));

        table.setEditable(true);
        Callback<TableColumn<Person, String>, TableCell<Person, String>> cellFactory
                = (TableColumn<Person, String> param) -> new EditingCell();
        Callback<TableColumn<Person, Date>, TableCell<Person, Date>> dateCellFactory
                = (TableColumn<Person, Date> param) -> new DateEditingCell();
        Callback<TableColumn<Person, Typ>, TableCell<Person, Typ>> comboBoxCellFactory
                = (TableColumn<Person, Typ> param) -> new ComboBoxEditingCell();


        TableColumn<Person, String> firstNameCol = new TableColumn("Vorname");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        firstNameCol.setCellFactory(cellFactory);
        firstNameCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Person, String> t) -> {
                    ((Person) t.getTableView().getItems()
                    .get(t.getTablePosition().getRow()))
                    .setFirstName(t.getNewValue());

                });
        
        TableColumn<Person, Typ> lastNameCol = new TableColumn("Lieblings Tier");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(cellData -> cellData.getValue().typObjProperty());
        lastNameCol.setCellFactory(comboBoxCellFactory);
        lastNameCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Person, Typ> t) -> {
                    ((Person) t.getTableView().getItems()
                    .get(t.getTablePosition().getRow()))
                    .setTypObj(t.getNewValue());

                });

        TableColumn<Person, Date> emailCol = new TableColumn("Geburtstag");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(cellData -> cellData.getValue().birthdayProperty());
        emailCol.setCellFactory(dateCellFactory);
        emailCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Person, Date> t) -> {
                    ((Person) t.getTableView().getItems()
                    .get(t.getTablePosition().getRow()))
                    .setBirthday(t.getNewValue());

                });

        table.setItems(data);
        table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);

        final TextField addFirstName = new TextField();
        addFirstName.setPromptText("First Name");
        addFirstName.setMaxWidth(firstNameCol.getPrefWidth());

        final TextField addLastName = new TextField();
        addLastName.setPromptText("Last Name");
        addLastName.setMaxWidth(lastNameCol.getPrefWidth());

        final TextField addEmail = new TextField();
        addEmail.setPromptText("email");
        addEmail.setMaxWidth(emailCol.getPrefWidth());

        final Button addButton = new Button("Add");
        addButton.setOnAction((ActionEvent e)
                -> {
                    data.add(new Person(
                                    addFirstName.getText(),
                                    new Typ("Hund", "F1", "F2"),
                                    new Date()));
                    addFirstName.clear();
                    addLastName.clear();
                    addEmail.clear();
                }
        );

        HBox hb = new HBox(3, addFirstName, addLastName, addEmail, addButton);

		VBox.setVgrow(table, Priority.ALWAYS);
        final VBox vbox = new VBox(5, label, table, hb);

        vbox.setPadding(new Insets(10, 10, 10, 10));
                
		VBox.setVgrow(vbox, Priority.ALWAYS);
        VBox mainVBox = new VBox(mb, vbox);
        
        Scene scene = new Scene(mainVBox, 550, 550);

        stage.setTitle("DB4All");
        stage.setScene(scene);
        stage.show();
    }
    
    void restartStage() {
    	stageMaker();
    }
    @Override
    public void start(Stage stage) {
    	this.stage = stage;
    	
    	checkDatabase();
    	stageMaker();
    }
    
    private MenuBar setupMenu() {
    	final Menu menu1 = new Menu("File");
    	final Menu menu2 = new Menu("Options");
    	final Menu menu_configuration = new Menu(Translations.CONFIGURATION_MENU.getText());
    	final Menu menuItem_language = new Menu(Translations.LANGUAGE_MENU.getText());
    	final MenuItem menuItem_databases = new MenuItem(Translations.DATABASES_MENU.getText());
    	makeLanguagesMenu(menuItem_language);
    	menu_configuration.getItems().addAll(menuItem_language, menuItem_databases);

    	final Menu menu3 = new Menu("Help");
    	 
    	MenuBar menuBar = new MenuBar();
    	menuBar.getMenus().addAll(menu1, menu2, menu_configuration, menu3);
    	
    	menu_configuration.setOnAction(new EventHandler<ActionEvent>() {
    		@Override
    	    public void handle(ActionEvent e) {
    			var dlg = new DialogDatabaseConfigurator(stage);
    			dlg.showAndWait();
    	    }
    	});
    	
    	return menuBar;
    }
    
    private void makeLanguagesMenu(Menu parent) {
    	ToggleGroup tg = new ToggleGroup();
    	
    	Globals.lang_translations
    	.forEach((k,v) -> {
    		final RadioMenuItem mi = new RadioMenuItem(v.get(Globals.getLang()));
    		mi.setUserData(k);
    		tg.getToggles().add(mi);
    		parent.getItems().add(mi);
    		
    		if (k == Globals.getLang()) {
    			tg.selectToggle(mi);
    		}
    	});
    	
    	tg.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
    		  public void changed(ObservableValue<? extends Toggle> changed, Toggle oldVal, Toggle newVal) {
    		    if (newVal == null)
    		      return;
    		    // Cast newVal to RadioButton.
    		    RadioMenuItem rmi = (RadioMenuItem) newVal;
    		    Globals.setLang((Globals.Lang)rmi.getUserData());
    		    
    		    stageMaker();
    		  }
    		});
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	loadResources();
        launch(args);
    }

    class EditingCell extends TableCell<Person, String> {

        private TextField textField;

        private EditingCell() {
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
                    System.out.println("Commiting " + textField.getText());
                    commitEdit(textField.getText());
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }

    class DateEditingCell extends TableCell<Person, Date> {

        private DatePicker datePicker;

        private DateEditingCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createDatePicker();
                setText(null);
                setGraphic(datePicker);
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(getDate().toString());
            setGraphic(null);
        }

        @Override
        public void updateItem(Date item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (datePicker != null) {
                        datePicker.setValue(getDate());
                    }
                    setText(null);
                    setGraphic(datePicker);
                } else {
                    setText(getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                    setGraphic(null);
                }
            }
        }

        private void createDatePicker() {
            datePicker = new DatePicker(getDate());
            datePicker.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            datePicker.setOnAction((e) -> {
                System.out.println("Committed: " + datePicker.getValue().toString());
                commitEdit(Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            });
//            datePicker.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
//                if (!newValue) {
//                    commitEdit(Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
//                }
//            });
        }

        private LocalDate getDate() {
            return getItem() == null ? LocalDate.now() : getItem().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    class ComboBoxEditingCell extends TableCell<Person, Typ> {

        private ComboBox<Typ> comboBox;

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

            setText(getTyp().getTyp());
            setGraphic(null);
        }

        @Override
        public void updateItem(Typ item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (comboBox != null) {
                        comboBox.setValue(getTyp());
                    }
                    setText(getTyp().getTyp());
                    setGraphic(comboBox);
                } else {
                    setText(getTyp().getTyp());
                    setGraphic(null);
                }
            }
        }

        private void createComboBox() {
            Callback<ListView<Typ>, ListCell<Typ>> comboFactory
            		= (ListView<Typ> param) -> new ComboBoxView();
            
            comboBox = new ComboBox<>(typData);
            comboBoxConverter(comboBox);
            comboBox.setCellFactory(comboFactory);
            comboBox.valueProperty().set(getTyp());
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

        private void comboBoxConverter(ComboBox<Typ> comboBox) {
            // Define rendering of the list of values in ComboBox drop down. 
            comboBox.setCellFactory((c) -> {
                return new ListCell<Typ>() {
                    @Override
                    protected void updateItem(Typ item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getTyp());
                        }
                    }
                };
            });
        }

        private Typ getTyp() {
            return getItem() == null ? new Typ("", "", "") : getItem();
        }
    }
    
    class ComboBoxView extends ListCell<Typ> {
    	// Create our layout here to be reused for each ListCell
        GridPane gridPane = new GridPane();
        Label lblName = new Label();
        Label lblTitle = new Label();
        Label lblLocation = new Label();

        // Static block to configure our layout
        {
            // Ensure all our column widths are constant
            gridPane.getColumnConstraints().addAll(
                    new ColumnConstraints(100, 100, 100),
                    new ColumnConstraints(100, 100, 100),
                    new ColumnConstraints(100, 100, 100)
            );

            gridPane.add(lblName, 0, 1);
            gridPane.add(lblTitle, 1, 1);
            gridPane.add(lblLocation, 2, 1);

        }
    	
        @Override 
        public void updateItem(Typ item, boolean empty) {
            super.updateItem(item, empty);
            
            if (!empty && item != null) {
            	lblName.setText(item.getTyp());
                lblTitle.setText(item.getAttr1());
                lblLocation.setText(item.getAttr2());

                // Set this ListCell's graphicProperty to display our GridPane
                setGraphic(gridPane);
            }
            else {
                setGraphic(null);
            }
        }    	
    }

    public static class Typ {

        private final SimpleStringProperty typ;
        private final SimpleStringProperty attr1;
        private final SimpleStringProperty attr2;

        public Typ(String typ, String attr1, String attr2) {
            this.typ = new SimpleStringProperty(typ);
            this.attr1 = new SimpleStringProperty(attr1);
            this.attr2 = new SimpleStringProperty(attr2);
        }

        public String getTyp() {
            return this.typ.get();
        }

        public String getAttr1() {
        	return this.attr1.get();
        }
        
        public String getAttr2() {
        	return this.attr2.get();
        }
        
        public StringProperty typProperty() {
            return this.typ;
        }
        
        public StringProperty attr1Property() {
        	return this.attr1;
        }
        
        public StringProperty attr2Property() {
        	return this.attr2;
        }

        public void setTyp(String typ) {
            this.typ.set(typ);
        }
        
        public void setAttr1(String attr1) {
        	this.attr1.set(attr1);
        }
        
        public void setAttr2(String attr2) {
        	this.attr2.set(attr2);
        }

        @Override
        public String toString() {
            return typ.get();
        }

    }
    
    public static class Project {
        private final SimpleStringProperty name;
        private final SimpleListProperty<Person> persons;

        public Project(String name, List<Person> persons) {
            this.name = new SimpleStringProperty(name);
            
            this.persons = new SimpleListProperty<>();
            this.persons.setAll(persons);
        }
        
        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return this.name;
        }

        public void setName(String name) {
            this.name.set(name);
        }
        
        public List<Person> getPersons() {
            return this.persons.get();
        }
        
        public SimpleListProperty<Person> personsProperty() {
            return this.persons;
        }
        
        public void setPersons(List<Person> persons) {
            this.persons.setAll(persons);
        }
        
    }

    public static class Person {

        private final SimpleStringProperty firstName;
        private final SimpleObjectProperty<Typ> typ;
        private final SimpleObjectProperty<Date> birthday;

        public Person(String firstName, Typ typ, Date bithday) {
            this.firstName = new SimpleStringProperty(firstName);
            this.typ = new SimpleObjectProperty(typ);
            this.birthday = new SimpleObjectProperty(bithday);
        }

        public String getFirstName() {
            return firstName.get();
        }

        public StringProperty firstNameProperty() {
            return this.firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName.set(firstName);
        }

        public Typ getTypObj() {
            return typ.get();
        }

        public ObjectProperty<Typ> typObjProperty() {
            return this.typ;
        }

        public void setTypObj(Typ typ) {
            this.typ.set(typ);
        }

        public Date getBirthday() {
            return birthday.get();
        }

        public ObjectProperty<Date> birthdayProperty() {
            return this.birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday.set(birthday);
        }

    }

    private static void loadResources() {
    	try {
    		Globals.database_icon = new Image( Main.class.getResource( "images/database-16x16.png" ).openStream() );
    		Globals.table_icon = new Image( Main.class.getResource( "images/table-16x16.png" ).openStream() );
    	} catch(Exception e) {
    		System.out.println(e);
//    		System.out.println(System.class.getResource("/databases/images/database-16x16.png"));
//    		System.out.println(ClassLoader.getSystemClassLoader().getResource(""));
//    		System.out.println(ClassLoader.getSystemResource(""));
//    		System.out.println(Object.class.getResource(""));
//    		System.out.println(Main.class.getResource(""));
    	}

    }
}