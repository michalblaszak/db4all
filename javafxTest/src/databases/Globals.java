package databases;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Globals {
	public enum Lang {
		PL,
		EN;

		public static Stream<Lang> stream() {
	        return Stream.of(Lang.values()); 
	    }
	}
	
	public final static HashMap<Globals.Lang, HashMap<Globals.Lang, String>> lang_translations = new HashMap<Globals.Lang, HashMap<Globals.Lang, String>>(){
		{
			put(Globals.Lang.PL, new HashMap<Globals.Lang, String>(){
				{put(Globals.Lang.PL, "Polski");
				put(Globals.Lang.EN, "Polish");}
			});
			put(Globals.Lang.EN, new HashMap<Globals.Lang, String>(){
				{put(Globals.Lang.PL, "Angielski");
				put(Globals.Lang.EN, "English");}
			});
		}};

	public static Lang lang = Lang.PL;

	static void setLang(Lang lang) {
		Globals.lang = lang;
	}
	
	static Lang getLang() { return lang; }
	
	public static Image database_icon, table_icon;
	
	public static void showAlert(Stage parent, AlertType alertType, String alertText, String titleText) {
		Globals.showAlert(parent, alertType, alertText, titleText, titleText);
	}

	public static void showAlert(Stage parent, AlertType alertType, String alertText, String titleText, String headerText) {
    	Alert a = new Alert(alertType, alertText);
    	a.initOwner(parent);
    	a.setTitle(titleText);
    	a.setHeaderText(headerText);
        a.show();
	}
}
