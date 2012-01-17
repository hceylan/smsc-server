/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.smscserver.usermanager.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.smscserver.SmscServerConfigurationException;
import org.apache.smscserver.smsclet.Authentication;
import org.apache.smscserver.smsclet.AuthenticationFailedException;
import org.apache.smscserver.smsclet.Authority;
import org.apache.smscserver.smsclet.SmscException;
import org.apache.smscserver.smsclet.User;
import org.apache.smscserver.usermanager.AnonymousAuthentication;
import org.apache.smscserver.usermanager.DbUserManagerFactory;
import org.apache.smscserver.usermanager.PasswordEncryptor;
import org.apache.smscserver.usermanager.UsernamePasswordAuthentication;
import org.apache.smscserver.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * This is another database based user manager class. It has been tested in MySQL and Oracle 8i database. The schema
 * file is </code>res/smsc-db.sql</code>
 * 
 * All the user attributes are replaced during run-time. So we can use your database schema. Then you need to modify the
 * SQLs in the configuration file.
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class DbUserManager extends AbstractUserManager {

    private final Logger LOG = LoggerFactory.getLogger(DbUserManager.class);

    private String insertUserStmt;

    private String updateUserStmt;

    private String deleteUserStmt;

    private String selectUserStmt;

    private String selectAllStmt;

    private String isAdminStmt;

    private String authenticateStmt;

    private DataSource dataSource;

    /**
     * Internal constructor, do not use directly. Use {@link DbUserManagerFactory} instead.
     */
    public DbUserManager(DataSource dataSource, String selectAllStmt, String selectUserStmt, String insertUserStmt,
            String updateUserStmt, String deleteUserStmt, String authenticateStmt, String isAdminStmt,
            PasswordEncryptor passwordEncryptor, String adminName) {
        super(adminName, passwordEncryptor);
        this.dataSource = dataSource;
        this.selectAllStmt = selectAllStmt;
        this.selectUserStmt = selectUserStmt;
        this.insertUserStmt = insertUserStmt;
        this.updateUserStmt = updateUserStmt;
        this.deleteUserStmt = deleteUserStmt;
        this.authenticateStmt = authenticateStmt;
        this.isAdminStmt = isAdminStmt;

        Connection con = null;
        try {
            // test the connection
            con = this.createConnection();

            this.LOG.info("Database connection opened.");
        } catch (SQLException ex) {
            this.LOG.error("Failed to open connection to user database", ex);
            throw new SmscServerConfigurationException("Failed to open connection to user database", ex);
        } finally {
            this.closeQuitely(con);
        }
    }

    /**
     * User authentication.
     */
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        if (authentication instanceof UsernamePasswordAuthentication) {
            UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;

            String user = upauth.getUsername();
            String password = upauth.getPassword();

            if (user == null) {
                throw new AuthenticationFailedException("Authentication failed");
            }

            if (password == null) {
                password = "";
            }

            Statement stmt = null;
            ResultSet rs = null;
            try {

                // create the sql query
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(AbstractUserManager.ATTR_LOGIN, this.escapeString(user));
                String sql = StringUtils.replaceString(this.authenticateStmt, map);
                this.LOG.info(sql);

                // execute query
                stmt = this.createConnection().createStatement();
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    try {
                        String storedPassword = rs.getString(AbstractUserManager.ATTR_PASSWORD);
                        if (this.getPasswordEncryptor().matches(password, storedPassword)) {
                            return this.getUserByName(user);
                        } else {
                            throw new AuthenticationFailedException("Authentication failed");
                        }
                    } catch (SmscException e) {
                        throw new AuthenticationFailedException("Authentication failed", e);
                    }
                } else {
                    throw new AuthenticationFailedException("Authentication failed");
                }
            } catch (SQLException ex) {
                this.LOG.error("DbUserManager.authenticate()", ex);
                throw new AuthenticationFailedException("Authentication failed", ex);
            } finally {
                this.closeQuitely(rs);
                this.closeQuitely(stmt);
            }
        } else if (authentication instanceof AnonymousAuthentication) {
            try {
                if (this.doesExist("anonymous")) {
                    return this.getUserByName("anonymous");
                } else {
                    throw new AuthenticationFailedException("Authentication failed");
                }
            } catch (AuthenticationFailedException e) {
                throw e;
            } catch (SmscException e) {
                throw new AuthenticationFailedException("Authentication failed", e);
            }
        } else {
            throw new IllegalArgumentException("Authentication not supported by this user manager");
        }
    }

    protected void closeQuitely(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    private void closeQuitely(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    private void closeQuitely(Statement stmt) {
        if (stmt != null) {
            Connection con = null;
            try {
                con = stmt.getConnection();
            } catch (Exception e) {
            }
            try {
                stmt.close();
            } catch (SQLException e) {
                // ignore
            }
            this.closeQuitely(con);
        }
    }

    /**
     * Open connection to database.
     */
    protected Connection createConnection() throws SQLException {
        Connection connection = this.dataSource.getConnection();
        connection.setAutoCommit(true);

        return connection;
    }

    /**
     * Delete user. Delete the row from the table.
     */
    public void delete(String name) throws SmscException {
        // create sql query
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(AbstractUserManager.ATTR_LOGIN, this.escapeString(name));
        String sql = StringUtils.replaceString(this.deleteUserStmt, map);
        this.LOG.info(sql);

        // execute query
        Statement stmt = null;
        try {
            stmt = this.createConnection().createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            this.LOG.error("DbUserManager.delete()", ex);
            throw new SmscException("DbUserManager.delete()", ex);
        } finally {
            this.closeQuitely(stmt);
        }
    }

    /**
     * User existance check.
     */
    public boolean doesExist(String name) throws SmscException {
        Statement stmt = null;
        ResultSet rs = null;
        try {

            // create the sql
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(AbstractUserManager.ATTR_LOGIN, this.escapeString(name));
            String sql = StringUtils.replaceString(this.selectUserStmt, map);
            this.LOG.info(sql);

            // execute query
            stmt = this.createConnection().createStatement();
            rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException ex) {
            this.LOG.error("DbUserManager.doesExist()", ex);
            throw new SmscException("DbUserManager.doesExist()", ex);
        } finally {
            this.closeQuitely(rs);
            this.closeQuitely(stmt);
        }
    }

    /**
     * Escape string to be embedded in SQL statement.
     */
    private String escapeString(String input) {
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

    /**
     * Get all user names from the database.
     */
    public String[] getAllUserNames() throws SmscException {

        Statement stmt = null;
        ResultSet rs = null;
        try {

            // create sql query
            String sql = this.selectAllStmt;
            this.LOG.info(sql);

            // execute query
            stmt = this.createConnection().createStatement();
            rs = stmt.executeQuery(sql);

            // populate list
            ArrayList<String> names = new ArrayList<String>();
            while (rs.next()) {
                names.add(rs.getString(AbstractUserManager.ATTR_LOGIN));
            }
            return names.toArray(new String[0]);
        } catch (SQLException ex) {
            this.LOG.error("DbUserManager.getAllUserNames()", ex);
            throw new SmscException("DbUserManager.getAllUserNames()", ex);
        } finally {
            this.closeQuitely(rs);
            this.closeQuitely(stmt);
        }
    }

    /**
     * Retrive the data source used by the user manager
     * 
     * @return The current data source
     */
    public DataSource getDataSource() {
        return this.dataSource;
    }

    /**
     * Get the SQL SELECT statement used to find whether an user is admin or not.
     * 
     * @return The SQL statement
     */
    public String getSqlUserAdmin() {
        return this.isAdminStmt;
    }

    /**
     * Get the SQL SELECT statement used to authenticate user.
     * 
     * @return The SQL statement
     */
    public String getSqlUserAuthenticate() {
        return this.authenticateStmt;
    }

    /**
     * Get the SQL DELETE statement used to delete an existing user.
     * 
     * @return The SQL statement
     */
    public String getSqlUserDelete() {
        return this.deleteUserStmt;
    }

    /**
     * Get the SQL INSERT statement used to add a new user.
     * 
     * @return The SQL statement
     */
    public String getSqlUserInsert() {
        return this.insertUserStmt;
    }

    /**
     * Get the SQL SELECT statement used to select an existing user.
     * 
     * @return The SQL statement
     */
    public String getSqlUserSelect() {
        return this.selectUserStmt;
    }

    /**
     * Get the SQL SELECT statement used to select all user ids.
     * 
     * @return The SQL statement
     */
    public String getSqlUserSelectAll() {
        return this.selectAllStmt;
    }

    /**
     * Get the SQL UPDATE statement used to update an existing user.
     * 
     * @return The SQL statement
     */
    public String getSqlUserUpdate() {
        return this.updateUserStmt;
    }

    /**
     * Get the user object. Fetch the row from the table.
     */
    public User getUserByName(String name) throws SmscException {
        Statement stmt = null;
        ResultSet rs = null;
        try {

            BaseUser user = this.selectUserByName(name);

            if (user != null) {
                // reset the password, not to be sent to API users
                user.setPassword(null);
            }
            return user;

        } catch (SQLException ex) {
            this.LOG.error("DbUserManager.getUserByName()", ex);
            throw new SmscException("DbUserManager.getUserByName()", ex);
        } finally {
            this.closeQuitely(rs);
            this.closeQuitely(stmt);
        }
    }

    /**
     * @return true if user with this login is administrator
     */
    @Override
    public boolean isAdmin(String login) throws SmscException {

        // check input
        if (login == null) {
            return false;
        }

        Statement stmt = null;
        ResultSet rs = null;
        try {

            // create the sql query
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(AbstractUserManager.ATTR_LOGIN, this.escapeString(login));
            String sql = StringUtils.replaceString(this.isAdminStmt, map);
            this.LOG.info(sql);

            // execute query
            stmt = this.createConnection().createStatement();
            rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException ex) {
            this.LOG.error("DbUserManager.isAdmin()", ex);
            throw new SmscException("DbUserManager.isAdmin()", ex);
        } finally {
            this.closeQuitely(rs);
            this.closeQuitely(stmt);
        }
    }

    /**
     * Save user. If new insert a new row, else update the existing row.
     */
    public void save(User user) throws SmscException {
        // null value check
        if (user.getName() == null) {
            throw new NullPointerException("User name is null.");
        }

        Statement stmt = null;
        try {

            // create sql query
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(AbstractUserManager.ATTR_LOGIN, this.escapeString(user.getName()));

            String password = null;
            if (user.getPassword() != null) {
                // password provided, encrypt it and store the encrypted value
                password = this.getPasswordEncryptor().encrypt(user.getPassword());
            } else {
                // password was not provided, either load from the existing user and store that again
                // or store as null
                ResultSet rs = null;

                try {
                    User userWithPassword = this.selectUserByName(user.getName());

                    if (userWithPassword != null) {
                        // user exists, reuse password
                        password = userWithPassword.getPassword();
                    }
                } finally {
                    this.closeQuitely(rs);
                }
            }
            map.put(AbstractUserManager.ATTR_PASSWORD, this.escapeString(password));

            String home = user.getHomeDirectory();
            if (home == null) {
                home = "/";
            }
            map.put(AbstractUserManager.ATTR_HOME, this.escapeString(home));
            map.put(AbstractUserManager.ATTR_ENABLE, String.valueOf(user.getEnabled()));

            map.put(AbstractUserManager.ATTR_WRITE_PERM, String.valueOf(user.authorize(new WriteRequest()) != null));
            map.put(AbstractUserManager.ATTR_MAX_IDLE_TIME, user.getMaxIdleTime());

            TransferRateRequest transferRateRequest = new TransferRateRequest();
            transferRateRequest = (TransferRateRequest) user.authorize(transferRateRequest);

            if (transferRateRequest != null) {
                map.put(AbstractUserManager.ATTR_MAX_UPLOAD_RATE, transferRateRequest.getMaxUploadRate());
                map.put(AbstractUserManager.ATTR_MAX_DOWNLOAD_RATE, transferRateRequest.getMaxDownloadRate());
            } else {
                map.put(AbstractUserManager.ATTR_MAX_UPLOAD_RATE, 0);
                map.put(AbstractUserManager.ATTR_MAX_DOWNLOAD_RATE, 0);
            }

            // request that always will succeed
            ConcurrentLoginRequest concurrentLoginRequest = new ConcurrentLoginRequest(0, 0);
            concurrentLoginRequest = (ConcurrentLoginRequest) user.authorize(concurrentLoginRequest);

            if (concurrentLoginRequest != null) {
                map.put(AbstractUserManager.ATTR_MAX_LOGIN_NUMBER, concurrentLoginRequest.getMaxConcurrentLogins());
                map.put(AbstractUserManager.ATTR_MAX_LOGIN_PER_IP, concurrentLoginRequest.getMaxConcurrentLoginsPerIP());
            } else {
                map.put(AbstractUserManager.ATTR_MAX_LOGIN_NUMBER, 0);
                map.put(AbstractUserManager.ATTR_MAX_LOGIN_PER_IP, 0);
            }

            String sql = null;
            if (!this.doesExist(user.getName())) {
                sql = StringUtils.replaceString(this.insertUserStmt, map);
            } else {
                sql = StringUtils.replaceString(this.updateUserStmt, map);
            }
            this.LOG.info(sql);

            // execute query
            stmt = this.createConnection().createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            this.LOG.error("DbUserManager.save()", ex);
            throw new SmscException("DbUserManager.save()", ex);
        } finally {
            this.closeQuitely(stmt);
        }
    }

    private BaseUser selectUserByName(String name) throws SQLException {
        // create sql query
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(AbstractUserManager.ATTR_LOGIN, this.escapeString(name));
        String sql = StringUtils.replaceString(this.selectUserStmt, map);
        this.LOG.info(sql);

        Statement stmt = null;
        ResultSet rs = null;
        try {
            // execute query
            stmt = this.createConnection().createStatement();
            rs = stmt.executeQuery(sql);

            // populate user object
            BaseUser thisUser = null;
            if (rs.next()) {
                thisUser = new BaseUser();
                thisUser.setName(rs.getString(AbstractUserManager.ATTR_LOGIN));
                thisUser.setPassword(rs.getString(AbstractUserManager.ATTR_PASSWORD));
                thisUser.setHomeDirectory(rs.getString(AbstractUserManager.ATTR_HOME));
                thisUser.setEnabled(rs.getBoolean(AbstractUserManager.ATTR_ENABLE));
                thisUser.setMaxIdleTime(rs.getInt(AbstractUserManager.ATTR_MAX_IDLE_TIME));

                List<Authority> authorities = new ArrayList<Authority>();
                if (rs.getBoolean(AbstractUserManager.ATTR_WRITE_PERM)) {
                    authorities.add(new WritePermission());
                }

                authorities.add(new ConcurrentLoginPermission(rs.getInt(AbstractUserManager.ATTR_MAX_LOGIN_NUMBER), rs
                        .getInt(AbstractUserManager.ATTR_MAX_LOGIN_PER_IP)));
                authorities.add(new TransferRatePermission(rs.getInt(AbstractUserManager.ATTR_MAX_DOWNLOAD_RATE), rs
                        .getInt(AbstractUserManager.ATTR_MAX_UPLOAD_RATE)));

                thisUser.setAuthorities(authorities);
            }
            return thisUser;

        } finally {
            this.closeQuitely(rs);
            this.closeQuitely(stmt);
        }
    }

    /**
     * Set the data source to be used by the user manager
     * 
     * @param dataSource
     *            The data source to use
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Set the SQL SELECT statement used to find whether an user is admin or not. All the dynamic values will be
     * replaced during runtime.
     * 
     * @param sql
     *            The SQL statement
     */
    public void setSqlUserAdmin(String sql) {
        this.isAdminStmt = sql;
    }

    /**
     * Set the SQL SELECT statement used to authenticate user. All the dynamic values will be replaced during runtime.
     * 
     * @param sql
     *            The SQL statement
     */
    public void setSqlUserAuthenticate(String sql) {
        this.authenticateStmt = sql;
    }

    /**
     * Set the SQL DELETE statement used to delete an existing user. All the dynamic values will be replaced during
     * runtime.
     * 
     * @param sql
     *            The SQL statement
     */
    public void setSqlUserDelete(String sql) {
        this.deleteUserStmt = sql;
    }

    /**
     * Set the SQL INSERT statement used to add a new user. All the dynamic values will be replaced during runtime.
     * 
     * @param sql
     *            The SQL statement
     */
    public void setSqlUserInsert(String sql) {
        this.insertUserStmt = sql;
    }

    /**
     * Set the SQL SELECT statement used to select an existing user. All the dynamic values will be replaced during
     * runtime.
     * 
     * @param sql
     *            The SQL statement
     */
    public void setSqlUserSelect(String sql) {
        this.selectUserStmt = sql;
    }

    /**
     * Set the SQL SELECT statement used to select all user ids. All the dynamic values will be replaced during runtime.
     * 
     * @param sql
     *            The SQL statement
     */
    public void setSqlUserSelectAll(String sql) {
        this.selectAllStmt = sql;
    }

    /**
     * Set the SQL UPDATE statement used to update an existing user. All the dynamic values will be replaced during
     * runtime.
     * 
     * @param sql
     *            The SQL statement
     */
    public void setSqlUserUpdate(String sql) {
        this.updateUserStmt = sql;
    }
}