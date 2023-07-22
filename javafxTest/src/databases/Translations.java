package databases;

import java.util.HashMap;
import java.util.Map;

public enum Translations {
	NO_ERROR(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Operacja zakończona sukcesem.");
		put(Globals.Lang.EN, "Operation succeeded.");}
	}),
	OPERATION_ERROR(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Operacja nie powiodła się.");
		put(Globals.Lang.EN, "Operation failed.");}
	}),
	NO_TEXT(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "");
		put(Globals.Lang.EN, "");}
	}),
	UNKNOWN_REASON(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Nieznana przyczyna.");
		put(Globals.Lang.EN, "Unknown reason.");}
	}),
	DB_NOT_CONNECTED(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Baza danych nie została podłączona.");
		put(Globals.Lang.EN, "Database not connected.");}
	}),
	WRONG_TYPE(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Błąd wewnętrzny: zastosowano zły typ danych.");
		put(Globals.Lang.EN, "Internal error: Wrong datatype was used.");}
	}),
	NO_DATABASES(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Nie zdefiniowano żadnej bazy danych.\nCzy chcesz założyć bazę danych teraz?");
		put(Globals.Lang.EN, "No database has been defined yet.\nDo you want to define one now?");}
	}),
	YES(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Tak");
		put(Globals.Lang.EN, "Yes");}
	}),
	NO(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Nie");
		put(Globals.Lang.EN, "No");}
	}),
	WARNING(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Ostrzeżenie");
		put(Globals.Lang.EN, "Warning");}
	}),
	INTERNAL_ERROR(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Błąd wewnętrzny");
		put(Globals.Lang.EN, "Internal error");}
	}),
	ERROR(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Błąd");
		put(Globals.Lang.EN, "Error");}
	}),
	INFORMATION(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Informacja");
		put(Globals.Lang.EN, "Information");}
	}),
	LACK_OF_DATABASE(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Brak bazy danych");
		put(Globals.Lang.EN, "Database missing");}
	}),
	CONFIGURATION_MENU(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Konfiguracja");
		put(Globals.Lang.EN, "Configuration");}
	}),
	LANGUAGE_MENU(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Język");
		put(Globals.Lang.EN, "Language");}
	}),
	DATABASES_MENU(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Bazy danych");
		put(Globals.Lang.EN, "Databases");}
	}),
	GUI_NOT_INITIALIZED(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Błąd wewnętrzny: Nie zainicjowany interfejs graficzny.");
		put(Globals.Lang.EN, "Internal error: Ggraphical interface has not been initialized.");}
	}),
	CLOSE(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Zamknij");
		put(Globals.Lang.EN, "Close");}
	}),
	SAVE(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Zapisz");
		put(Globals.Lang.EN, "Save");}
	}),
	CANCEL(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Anuluj");
		put(Globals.Lang.EN, "Cancel");}
	}),
	DB_CONFIGURATOR(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Konfigurator bazy danych");
		put(Globals.Lang.EN, "Database configurator");}
	}),
	NAME(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Nazwa");
		put(Globals.Lang.EN, "Name");}
	}),
	TABLE(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Tabela");
		put(Globals.Lang.EN, "Table");}
	}),
	DATABASE(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Baza danych");
		put(Globals.Lang.EN, "Database");}
	}),
	ADD_DATABASE(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Dodaj bazę danych");
		put(Globals.Lang.EN, "Add database");}
	}),
	EDIT_DATABASE(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Zmień bazę danych");
		put(Globals.Lang.EN, "Change database");}
	}),
	DELETE_DATABASE(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Usuń bazę danych");
		put(Globals.Lang.EN, "Delete database");}
	}),
	ADD_TABLE(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Dodaj tabelę");
		put(Globals.Lang.EN, "Add table");}
	}),
	EDIT_TABLE(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Zmień tabelę");
		put(Globals.Lang.EN, "Change table");}
	}),
	DELETE_TABLE(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Usuń tabelę");
		put(Globals.Lang.EN, "Delete table");}
	}),
	NAME_LABEL(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Nazwa:");
		put(Globals.Lang.EN, "Name:");}
	}),
	INSERTING_DB_FAILED(new HashMap<Globals.Lang, String>(){
		{put(Globals.Lang.PL, "Zapis nowej bazy danych nie powiódł się.");
		put(Globals.Lang.EN, "Inserting a new database failed.");}
	});
	
	
	private final Map<Globals.Lang, String> value;
	
	private Translations(Map<Globals.Lang, String> v) {
		this.value = v;
	}
	
	public String getText() {
		return value.get(Globals.getLang());
	}
}
