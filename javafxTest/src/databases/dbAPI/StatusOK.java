package databases.dbAPI;

import databases.Translations;
import databases.Variant;
import databases.Globals.Lang;
import databases.dbAPI.Globals.*;

public class StatusOK extends AbstractStatus {
	final Variant value;
	
	public StatusOK() {
		super(CallStatus.OK, Translations.NO_ERROR);
		value = null;
	}
	
	public StatusOK(Variant v) {
		super(CallStatus.OK, Translations.NO_ERROR);
		value = v;
	}
	
	public StatusOK(int v) {
		this(new Variant(v));
	}
	
	public String getText() {
		return mainDescription.getText(); 
	}
	public Variant getValue() {
		return value;
	}
}
