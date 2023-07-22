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
            return new StatusFail(FailReason.DBEngine, e.getErrorCode() + e.getMessage());
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
            	return new StatusFail(FailReason.DBEngine, e.getErrorCode() + e.getMessage());
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
                    "type"	INTEGER NOT NULL check (type in (1,2)),
                    "db_id"	INTEGER NOT NULL REFERENCES databases (id) ON DELETE CASCADE,
                    UNIQUE("db_id","name"),
                    PRIMARY KEY("id" AUTOINCREMENT)
                );
                
            CREATE TRIGGER IF NOT EXISTS I_double_primary_table_check
                BEFORE INSERT ON "tables"
                BEGIN
                    select case
                    when new.type = 1 then
                        -- check if there are other primary tables
                        (SELECT RAISE(FAIL, "primary table already exists")
                        FROM "tables"
                        WHERE "type" = 1 and db_id = new.db_id)
                    when new.type = 2 then
                        -- check if there is a primary teable already
                        (select raise(FAIL, "primary table is missing")
                        from 
                        (select count(*) as cnt
                        from "tables"
                        where "type" = 1 and db_id = new.db_id) as t
                        where t.cnt = 0)
                    end;
                END;

            CREATE TRIGGER IF NOT EXISTS U_double_primary_table_check
                BEFORE UPDATE ON "tables"
                BEGIN
                    select case
                    when new.type = 1 and old.type = 2 THEN
                        (SELECT RAISE(FAIL, "primary table already exists")
                        FROM "tables"
                        WHERE "type" = 1 and db_id = new.db_id)
                    when new.type = 2 and old.type = 1 then
                        RAISE(FAIL, "the master table must exist")
                    end;
                END;

            CREATE TRIGGER IF NOT EXISTS D_double_primary_table_check
                BEFORE DELETE ON "tables"
                when old.type = 1
                BEGIN
                    select raise(FAIL, "details tables still exist")
                    from 
                    (select count(*) as cnt
                    from "tables"
                    where "type" = 2 and db_id = old.db_id) as t
                    where t.cnt <> 0;
                end;

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
//        				SELECT last_insert_rowid();

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

}
