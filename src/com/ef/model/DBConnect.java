
package com.ef.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Mihailo
 */
public class DBConnect {
    
    public Connection conn;
    private static DBConnect instance;
    
    /**
     * Private constructor in which object connects to database.
     */
    private DBConnect() throws SQLException{
        this.conn = DriverManager.getConnection("jdbc:mysql://localhost/access_log","log_user","123");
    }
    
    
    /**
     * Checks if this object is already instanced, if it is, it checks if connection to database is closed. 
     * If this object is not instanced or if it is but the connection to database is closed, it creates new DBConnect, 
     * if not then it returns already existing instance of that object.
     * 
     * @return instance - already existing instance of DBConnect object or new instance of DBConnect object.
     */
    public static DBConnect getInstance () throws SQLException{
        if(instance == null || instance.conn.isClosed() ){
            instance = new DBConnect();
        }
        return instance;
    }
}
