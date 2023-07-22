package databases.dbAPI;

public record RecDatabase(int id, String name) {
	public RecDatabase() {
		this(-1, "");
	}
}
