package databases.dbAPI;

import databases.Translations;
import databases.dbAPI.Globals.*;

public abstract class AbstractStatus {
	final private CallStatus status;
	final protected Translations mainDescription;
	
	AbstractStatus(CallStatus status, Translations mainDescription) {
		this.status = status;
		this.mainDescription = mainDescription;
	}
	
	public abstract String getText();
	public CallStatus getStatus() { return this.status; }
}
