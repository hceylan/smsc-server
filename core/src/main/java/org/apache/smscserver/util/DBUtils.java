package org.apache.smscserver.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.smscserver.smsclet.SmscException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * Standard date related utility methods.
 * 
 * @author hceylan
 */
public class DBUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DBUtils.class);

    public static Object asString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);
    }

    /**
     * Quitely closes a connection
     * 
     * @param connection
     *            the connection to close
     */
    public static void closeQuitely(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                DBUtils.LOG.warn("Exception while closing connection, ignoring...", e);
            }
        }
    }

    /**
     * Quitely closes a resultset
     * 
     * @param rs
     *            the resultset to close
     */
    public static void closeQuitely(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                DBUtils.LOG.warn("Exception while closing resultset, ignoring...", e);
            }
        }
    }

    public static void closeQuitely(ResultSet rs, Statement stmt) {
        DBUtils.closeQuitely(rs);
        DBUtils.closeQuitely(stmt);
    }

    public static void closeQuitely(Statement stmt) {
        try {
            stmt.close();
        } catch (SQLException e) {
            DBUtils.LOG.warn("Exception while closing statement, ignoring...", e);
        }
    }

    public static void closeQuitelyWithConnection(ResultSet rs, Statement stmt) {
        DBUtils.closeQuitely(rs);
        DBUtils.closeQuitelyWithConnection(stmt);
    }

    /**
     * Quitely closes a resultset
     * 
     * @param rs
     *            the resultset to close
     */
    public static void closeQuitelyWithConnection(Statement stmt) {
        if (stmt != null) {
            Connection con = null;
            try {
                con = stmt.getConnection();
            } catch (Exception e) {
            }

            DBUtils.closeQuitely(stmt);

            DBUtils.closeQuitely(con);
        }
    }

    /**
     * Open connection to database.
     * 
     * @param dataSource
     */
    public static Connection createConnection(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(true);

        return connection;
    }

    /**
     * Escape string to be embedded in SQL statement.
     */
    public static String escapeString(String input) {
        if (input == null) {
            return input;
        }

        StringBuilder valBuf = new StringBuilder(input);
        for (int i = 0; i < valBuf.length(); i++) {
            char ch = valBuf.charAt(i);
            if ((ch == '\'') || (ch == '\\') || (ch == '$') || (ch == '^') || (ch == '[') || (ch == ']') || (ch == '{')
                    || (ch == '}')) {

                valBuf.insert(i, '\\');
                i++;
            }
        }
        return valBuf.toString();
    }

    public static SmscException handleException(String sql, Exception e) {
        String message = "Error executing sql statement: " + sql;

        DBUtils.LOG.error(message, e);

        return new SmscException(message, e);
    }
}
