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
import java.util.Map;

import javax.sql.DataSource;

import org.apache.smscserver.SmscServerConfigurationException;
import org.apache.smscserver.smsclet.Authentication;
import org.apache.smscserver.smsclet.AuthenticationFailedException;
import org.apache.smscserver.smsclet.Authority;
import org.apache.smscserver.smsclet.SmscException;
import org.apache.smscserver.smsclet.User;
import org.apache.smscserver.usermanager.DbUserManagerFactory;
import org.apache.smscserver.usermanager.PasswordEncryptor;
import org.apache.smscserver.usermanager.UsernamePasswordAuthentication;
import org.apache.smscserver.util.DBUtils;
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
 * @author hceylan
 */
public class DbUserManager extends AbstractUserManager {

    private static final Logger LOG = LoggerFactory.getLogger(DbUserManager.class);

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

            DbUserManager.LOG.info("Database connection for user manager successfully opened.");
        } catch (SQLException e) {
            String msg = "Failed to open connection to user database";
            DbUserManager.LOG.error(msg, e);
            throw new SmscServerConfigurationException(msg, e);
        } finally {
            DBUtils.closeQuitely(con);
        }
    }

    private Connection createConnection() throws SQLException {
        return DBUtils.createConnection(this.dataSource);
    }

    /**
     * Delete user. Delete the row from the table.
     */
    public void delete(String name) throws SmscException {
        Statement stmt = null;
        String sql = null;
        try {
            // create sql query
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(AbstractUserManager.ATTR_SYSTEM_ID, DBUtils.escapeString(name));
            sql = StringUtils.replaceString(this.deleteUserStmt, map);
            DbUserManager.LOG.debug(sql);

            // execute query
            stmt = this.createConnection().createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            throw DBUtils.handleException(sql, e);
        } finally {
            DBUtils.closeQuitelyWithConnection(stmt);
        }
    }

    /**
     * User existance check.
     */
    public boolean doesExist(String name) throws SmscException {
        Statement stmt = null;
        ResultSet rs = null;
        String sql = null;

        try {
            // create the sql
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(AbstractUserManager.ATTR_SYSTEM_ID, DBUtils.escapeString(name));
            sql = StringUtils.replaceString(this.selectUserStmt, map);
            DbUserManager.LOG.debug(sql);

            // execute query
            stmt = this.createConnection().createStatement();
            rs = stmt.executeQuery(sql);

            return rs.next();
        } catch (Exception e) {
            throw DBUtils.handleException(sql, e);
        } finally {
            DBUtils.closeQuitelyWithConnection(rs, stmt);
        }
    }

    /**
     * Get all user names from the database.
     */
    public String[] getAllUserNames() throws SmscException {
        Statement stmt = null;
        ResultSet rs = null;
        String sql = null;

        try {
            // create sql query
            sql = this.selectAllStmt;
            DbUserManager.LOG.debug(sql);

            // execute query
            stmt = this.createConnection().createStatement();
            rs = stmt.executeQuery(sql);

            // populate list
            ArrayList<String> names = new ArrayList<String>();
            while (rs.next()) {
                names.add(rs.getString(AbstractUserManager.ATTR_SYSTEM_ID));
            }
            return names.toArray(new String[0]);
        } catch (Exception e) {
            throw DBUtils.handleException(sql, e);
        } finally {
            DBUtils.closeQuitelyWithConnection(rs, stmt);
        }
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public String getSqlUserAdmin() {
        return this.isAdminStmt;
    }

    public String getSqlUserAuthenticate() {
        return this.authenticateStmt;
    }

    public String getSqlUserDelete() {
        return this.deleteUserStmt;
    }

    public String getSqlUserInsert() {
        return this.insertUserStmt;
    }

    public String getSqlUserSelect() {
        return this.selectUserStmt;
    }

    public String getSqlUserSelectAll() {
        return this.selectAllStmt;
    }

    public String getSqlUserUpdate() {
        return this.updateUserStmt;
    }

    /**
     * Get the user object. Fetch the row from the table.
     */
    public User getUserByName(String name) throws SmscException {
        BaseUser user = this.selectUserByName(name);

        if (user != null) {
            // reset the password, not to be sent to API users
            user.setPassword(null);
        }

        return user;
    }

    @Override
    protected User internalAuthenticate(Authentication authentication) throws SmscException {
        if (authentication instanceof UsernamePasswordAuthentication) {
            UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;

            String username = upauth.getUsername();
            String password = upauth.getPassword();

            if (username == null) {
                throw new AuthenticationFailedException("Authentication failed");
            }

            if (password == null) {
                password = "";
            }

            Statement stmt = null;
            ResultSet rs = null;
            String sql = null;

            try {
                // create the sql query
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(AbstractUserManager.ATTR_SYSTEM_ID, DBUtils.escapeString(username));
                sql = StringUtils.replaceString(this.authenticateStmt, map);
                DbUserManager.LOG.debug(sql);

                // execute query
                stmt = this.createConnection().createStatement();
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    try {
                        String storedPassword = rs.getString(AbstractUserManager.ATTR_PASSWORD);
                        if (this.getPasswordEncryptor().matches(password, storedPassword)) {
                            User user = this.getUserByName(username);

                            this.authorizeConcurency(authentication, user);

                            return user;
                        } else {
                            throw new AuthenticationFailedException("Authentication failed");
                        }
                    } catch (SmscException e) {
                        throw new AuthenticationFailedException("Authentication failed", e);
                    }
                } else {
                    throw new AuthenticationFailedException("Authentication failed");
                }
            } catch (Exception e) {
                if (e instanceof AuthenticationFailedException) {
                    throw (AuthenticationFailedException) e;
                }
                throw DBUtils.handleException(sql, e);
            } finally {
                DBUtils.closeQuitelyWithConnection(rs, stmt);
            }
        } else {
            throw new IllegalArgumentException("Authentication not supported by this user manager");
        }
    }

    /**
     * @return true if user with this bind is administrator
     */
    @Override
    public boolean isAdmin(String bind) throws SmscException {
        // check input
        if (bind == null) {
            return false;
        }

        Statement stmt = null;
        ResultSet rs = null;
        String sql = null;

        try {
            // create the sql query
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(AbstractUserManager.ATTR_SYSTEM_ID, DBUtils.escapeString(bind));
            sql = StringUtils.replaceString(this.isAdminStmt, map);
            DbUserManager.LOG.debug(sql);

            // execute query
            stmt = this.createConnection().createStatement();
            rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (Exception e) {
            throw DBUtils.handleException(sql, e);
        } finally {
            DBUtils.closeQuitelyWithConnection(rs, stmt);
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
        String sql = null;

        try {
            // create sql query
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(AbstractUserManager.ATTR_SYSTEM_ID, DBUtils.escapeString(user.getName()));

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
                    DBUtils.closeQuitely(rs);
                }
            }

            map.put(AbstractUserManager.ATTR_PASSWORD, DBUtils.escapeString(password));
            map.put(AbstractUserManager.ATTR_ENABLE, String.valueOf(user.getEnabled()));
            map.put(AbstractUserManager.ATTR_MAX_IDLE_TIME, user.getMaxIdleTime());

            // request that always will succeed
            ConcurrentBindRequest concurrentBindRequest = new ConcurrentBindRequest(0, 0);
            concurrentBindRequest = (ConcurrentBindRequest) user.authorize(concurrentBindRequest);

            if (concurrentBindRequest != null) {
                map.put(AbstractUserManager.ATTR_MAX_BIND_NUMBER, concurrentBindRequest.getMaxConcurrentBinds());
                map.put(AbstractUserManager.ATTR_MAX_BIND_PER_IP, concurrentBindRequest.getMaxConcurrentBindsPerIP());
            } else {
                map.put(AbstractUserManager.ATTR_MAX_BIND_NUMBER, 0);
                map.put(AbstractUserManager.ATTR_MAX_BIND_PER_IP, 0);
            }

            if (!this.doesExist(user.getName())) {
                sql = StringUtils.replaceString(this.insertUserStmt, map);
            } else {
                sql = StringUtils.replaceString(this.updateUserStmt, map);
            }
            DbUserManager.LOG.debug(sql);

            // execute query
            stmt = this.createConnection().createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            throw DBUtils.handleException(sql, e);
        } finally {
            DBUtils.closeQuitelyWithConnection(stmt);
        }
    }

    private BaseUser selectUserByName(String name) throws SmscException {
        Statement stmt = null;
        ResultSet rs = null;
        String sql = null;

        try {
            // create sql query
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(AbstractUserManager.ATTR_SYSTEM_ID, DBUtils.escapeString(name));
            sql = StringUtils.replaceString(this.selectUserStmt, map);
            DbUserManager.LOG.debug(sql);

            // execute query
            stmt = this.createConnection().createStatement();
            rs = stmt.executeQuery(sql);

            // populate user object
            BaseUser thisUser = null;
            if (rs.next()) {
                thisUser = new BaseUser();
                thisUser.setName(rs.getString(AbstractUserManager.ATTR_SYSTEM_ID));
                thisUser.setPassword(rs.getString(AbstractUserManager.ATTR_PASSWORD));
                thisUser.setEnabled(rs.getBoolean(AbstractUserManager.ATTR_ENABLE));
                thisUser.setMaxIdleTime(rs.getInt(AbstractUserManager.ATTR_MAX_IDLE_TIME));

                List<Authority> authorities = new ArrayList<Authority>();

                authorities.add(new ConcurrentBindPermission(rs.getInt(AbstractUserManager.ATTR_MAX_BIND_NUMBER), rs
                        .getInt(AbstractUserManager.ATTR_MAX_BIND_PER_IP)));

                thisUser.setAuthorities(authorities);
            }
            return thisUser;
        } catch (Exception e) {
            throw DBUtils.handleException(sql, e);
        } finally {
            DBUtils.closeQuitelyWithConnection(rs, stmt);
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