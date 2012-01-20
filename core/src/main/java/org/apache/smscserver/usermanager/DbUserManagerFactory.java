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

package org.apache.smscserver.usermanager;

import javax.sql.DataSource;

import org.apache.smscserver.SmscServerConfigurationException;
import org.apache.smscserver.smsclet.UserManager;
import org.apache.smscserver.usermanager.impl.DbUserManager;

/**
 * Factory for database backed {@link UserManager} instances.
 * 
 * @author hceylan
 */
public class DbUserManagerFactory implements UserManagerFactory {

    private String adminName = "admin";
    private String insertUserStmt;
    private String updateUserStmt;
    private String deleteUserStmt;
    private String selectUserStmt;
    private String selectAllStmt;
    private String isAdminStmt;
    private String authenticateStmt;

    private DataSource dataSource;

    private PasswordEncryptor passwordEncryptor = new Md5PasswordEncryptor();

    public DbUserManagerFactory() {
        super();
    }

    private void check(Object field, String errorMessage) {
        if (field == null) {
            throw new SmscServerConfigurationException(errorMessage);
        }
    }

    public UserManager createUserManager() {
        this.check(this.dataSource, "Required data source not provided");
        this.check(this.insertUserStmt, "Required insert user SQL statement not provided");
        this.check(this.updateUserStmt, "Required update user SQL statement not provided");
        this.check(this.deleteUserStmt, "Required delete user SQL statement not provided");
        this.check(this.selectUserStmt, "Required select user SQL statement not provided");
        this.check(this.selectAllStmt, "Required select all users SQL statement not provided");
        this.check(this.isAdminStmt, "Required is admin user SQL statement not provided");
        this.check(this.authenticateStmt, "Required authenticate user SQL statement not provided");

        return new DbUserManager(this.dataSource, this.selectAllStmt, this.selectUserStmt, this.insertUserStmt,
                this.updateUserStmt, this.deleteUserStmt, this.authenticateStmt, this.isAdminStmt,
                this.passwordEncryptor, this.adminName);
    }

    /**
     * Set the name to use as the administrator of the server. The default value is "admin".
     * 
     * @param adminName
     *            The administrator user name
     */
    public void setAdminName(String adminName) {
        this.adminName = adminName;
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
     * Set the password encryptor to use for this user manager
     * 
     * @param passwordEncryptor
     *            The password encryptor
     */
    public void setPasswordEncryptor(PasswordEncryptor passwordEncryptor) {
        this.passwordEncryptor = passwordEncryptor;
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