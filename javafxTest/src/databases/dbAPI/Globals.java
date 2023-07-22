package databases.dbAPI;

public interface Globals {
	public enum CallStatus {
		OK,
		FAIL
	}
	public enum FailReason {
		UNKNOWN,
		DBEngine,
		APP_API
	}

}
