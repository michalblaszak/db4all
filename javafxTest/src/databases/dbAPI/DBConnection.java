package databases.dbAPI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import databases.Translations;
import databases.Variant;
import databases.Globals.Lang;
import databases.dbAPI.Globals.*;
import databases.dbAPI.Dod.*;


public class DBConnection {	
	static Connection conn = null;

    public static AbstractStatus connect() {
        try {
            // db parameters
            String url = "jdbc:sqlite:databases.sqlite";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            AbstractStatus ret = initDatabase();
            
            return ret;            
        } catch (SQLException e) {
            return new StatusFail(FailReason.DBEngine, Translations.ERROR_CODE.getText() + e.getErrorCode() + "\n"+ e.getMessage());
        } 
    }

    // Helper functions to standardize DB function invocation.
    @FunctionalInterface
    public interface DBFunction {
    	AbstractStatus exec() throws SQLException;
    }
    
    public static AbstractStatus databaseRequest(DBFunction fun) {
        if (conn != null) {
        	try {
        		return fun.exec();
        	} catch (SQLException e) {
            	return new StatusFail(FailReason.DBEngine, Translations.ERROR_CODE.getText() + e.getErrorCode() + "\n" + e.getMessage());
            }
        } else {
        	return new StatusFail(FailReason.APP_API, Translations.DB_NOT_CONNECTED.getText());
        }
    }

    // DB functions
    public static AbstractStatus initDatabase() {
    	return databaseRequest( () -> {
    		String sql = """
    		BEGIN;
            -- Databases
            CREATE TABLE IF NOT EXISTS "databases" (
                "ID"	INTEGER NOT NULL UNIQUE,
                "Name"	TEXT NOT NULL UNIQUE,
                PRIMARY KEY("ID" AUTOINCREMENT)
            );

            -- Tables
            CREATE TABLE IF NOT EXISTS "tables" (
                    "id"	INTEGER NOT NULL UNIQUE,
                    "name"	TEXT NOT NULL,
                    "db_id"	INTEGER NOT NULL REFERENCES databases (id) ON DELETE CASCADE,
                    UNIQUE("db_id","name"),
                    PRIMARY KEY("id" AUTOINCREMENT)
                );
                
            -- Columns
            --   Types:
            --     1: TextLine
            --     2: TextBox
            --     3: Integer
            --     4: Float
            --     5: Boolean
            --     6: date/time
            CREATE TABLE IF NOT EXISTS "columns" (
                "ID"	INTEGER NOT NULL UNIQUE,
                "table_id"	INTEGER NOT NULL REFERENCES "tables" (id) ON DELETE CASCADE,
                "name"	TEXT,
                "datatype"	INTEGER NOT NULL CHECK(datatype in (1,2,3,4)),
                UNIQUE("table_id","name"),
                PRIMARY KEY("ID" AUTOINCREMENT)
            );

            -- Data
            CREATE TABLE IF NOT EXISTS "data" (
                "ID"    INTEGER NOT NULL UNIQUE,
                "column_id" INTEGER NOT NULL REFERENCES "columns" (id) ON DELETE CASCADE,
                "value" BLOB
            );
            END;
            """;
		
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
    		return new StatusOK();
    	});
    }
    
    public static AbstractStatus countDatabases() {
    	return databaseRequest( () -> {
        	String sql = "SELECT count(*) FROM databases";
        	int cnt = 0;

        	Statement stmt = conn.createStatement();
        	ResultSet rs = stmt.executeQuery(sql);
        	if (rs.next()) {
        		cnt = rs.getInt(1);
        	}
        	rs.close();
        	
        	return new StatusOK(cnt);
        });
    }
    
    public static AbstractStatus getDatabases() {
    	return databaseRequest( () -> {
    		String sql = "SELECT id, name FROM databases";
    		
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			List<RecDatabase> ret = new ArrayList<RecDatabase>();
			while (rs.next()) {
				ret.add(new RecDatabase(rs.getInt(1), rs.getString(2)));
			}
			
			rs.close();
			
			return new StatusOK(new Variant(ret));
    	});
    }

    public static AbstractStatus insertDatabase(RecDatabase rec) {
    	return databaseRequest( () -> {
        	String sql = "INSERT INTO databases (name) VALUES (?)";

        	PreparedStatement stmt = conn.prepareStatement(sql);
        	stmt.setString(1, rec.name());
       
        	stmt.executeUpdate();
        	
			Statement stmt_id = conn.createStatement();
			ResultSet rs_id = stmt_id.executeQuery("SELECT last_insert_rowid()");

        	AbstractStatus ret;
        	
        	if (rs_id.next()) {
            	RecDatabase ret_rec;

            	ret_rec = new RecDatabase(rs_id.getInt(1), rec.name());
        		ret = new StatusOK(new Variant(ret_rec));
        	} else {
        		ret = new StatusFail(FailReason.DBEngine, Translations.INSERTING_DB_FAILED.getText());
        	}
        	rs_id.close();
        	
        	return ret;
    	});
    }

    public static AbstractStatus updateDatabase(RecDatabase rec) {
    	return databaseRequest( () -> {
        	String sql = "UPDATE databases SET name = ? where id = ?";

        	PreparedStatement stmt = conn.prepareStatement(sql);
        	stmt.setString(1, rec.name());
        	stmt.setInt(2, rec.id());
       
        	stmt.executeUpdate();
        	
        	return new StatusOK();
    	});
    }
    
    public static AbstractStatus getTables(int db_id) {
    	return databaseRequest( () -> {
    		String sql = "SELECT id, name, db_id FROM tables WHERE db_id = ?";
    		
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, db_id);
			
			ResultSet rs = stmt.executeQuery();
			List<RecTable> ret = new ArrayList<RecTable>();
			while (rs.next()) {
				ret.add(new RecTable(rs.getInt(1), rs.getString(2), rs.getInt(3)));
			}
			
			rs.close();
			
			return new StatusOK(new Variant(ret));
    	});
    }

    public static AbstractStatus insertTable(RecTable rec) {
    	return databaseRequest( () -> {
        	String sql = "INSERT INTO tables (name, db_id) VALUES (?, ?)";

        	PreparedStatement stmt = conn.prepareStatement(sql);
        	stmt.setString(1, rec.name());
        	stmt.setInt(2, rec.db_id());
       
        	stmt.executeUpdate();
        	
			Statement stmt_id = conn.createStatement();
			ResultSet rs_id = stmt_id.executeQuery("SELECT last_insert_rowid()");

        	AbstractStatus ret;
        	
        	if (rs_id.next()) {
            	RecTable ret_rec;

            	ret_rec = new RecTable(rs_id.getInt(1), rec.name(), rec.db_id());
        		ret = new StatusOK(new Variant(ret_rec));
        	} else {
        		ret = new StatusFail(FailReason.DBEngine, Translations.INSERTING_DB_FAILED.getText());
        	}
        	rs_id.close();
        	
        	return ret;
    	});
    }

    public static AbstractStatus updateTable(RecTable rec) {
    	return databaseRequest( () -> {
        	String sql = "UPDATE tables SET name = ?, db_id = ? where id = ?";

        	PreparedStatement stmt = conn.prepareStatement(sql);
        	stmt.setString(1, rec.name());
        	stmt.setInt(2, rec.db_id());
        	stmt.setInt(3, rec.id());
       
        	stmt.executeUpdate();
        	
        	return new StatusOK();
    	});
    }
}
