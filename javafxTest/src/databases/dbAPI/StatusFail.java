package databases.dbAPI;

import databases.Translations;
import databases.Globals.Lang;
import databases.dbAPI.Globals.*;

public class StatusFail extends AbstractStatus {
	final private FailReason reason;
	final String reason_description;
	
	StatusFail() {
		super(CallStatus.FAIL, Translations.OPERATION_ERROR);
		this.reason = FailReason.UNKNOWN;
		this.reason_description = Translations.UNKNOWN_REASON.getText();
	}
	StatusFail(FailReason reason, String reason_description) {
		super(CallStatus.FAIL, Translations.OPERATION_ERROR);
		this.reason = reason; // DBEngin, API
		this.reason_description = reason_description;
	}
	
	public String getText() {
		return mainDescription.getText() + "\n"
				+ reason_description;
	}
}
