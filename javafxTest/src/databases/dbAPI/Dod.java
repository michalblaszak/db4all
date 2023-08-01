package databases.dbAPI;

public interface Dod {
	public interface Rec {
		public String name();
	}
	
	// RecRoot used as a root in TreeView
	public record RecRoot() implements Rec {
		@Override
		public String name() {
			return "ROOT";
		}
	}
	
	public record RecDatabase (int id, String name) implements Rec {
		public RecDatabase() {
			this(-1, "");
		}
	}

	public record RecTable(int id, String name, int db_id) implements Rec {
		public RecTable() {
			this(-1, "", -1);
		}
	}
}
