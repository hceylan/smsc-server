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

package org.apache.smscserver.test.usermanager.impl;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

import org.apache.smscserver.smsclet.SmscException;
import org.apache.smscserver.test.TestUtil;
import org.apache.smscserver.usermanager.DbUserManagerFactory;
import org.apache.smscserver.usermanager.UserManagerFactory;
import org.apache.smscserver.util.IoUtils;
import org.h2.jdbcx.JdbcDataSource;

/**
 * 
 * @author hceylan
 * 
 */
public class DbUserManagerTest extends UserManagerTestTemplate {

    private JdbcDataSource ds;

    private Connection connection;

    private void createDatabase() throws Exception {
        this.connection = this.ds.getConnection();
        this.connection.setAutoCommit(true);

        String ddl = IoUtils.readFully(new FileReader(this.getInitSqlScript()));

        Statement stm = this.connection.createStatement();
        stm.execute(ddl);
    }

    @Override
    protected UserManagerFactory createUserManagerFactory() throws SmscException {
        DbUserManagerFactory manager = new DbUserManagerFactory();

        manager.setDataSource(this.ds);
        manager.setSqlUserInsert("INSERT INTO SMSC_USER (systemid, userpassword, enableflag, idletime, maxbindnumber, maxbindperip) VALUES ('{systemid}', '{userpassword}', {enableflag}, {idletime}, {maxbindnumber}, {maxbindperip})");
        manager.setSqlUserUpdate("UPDATE SMSC_USER SET userpassword='{userpassword}',enableflag={enableflag},idletime={idletime},maxbindnumber={maxbindnumber}, maxbindperip={maxbindperip} WHERE systemid='{systemid}'");
        manager.setSqlUserDelete("DELETE FROM SMSC_USER WHERE systemid = '{systemid}'");
        manager.setSqlUserSelect("SELECT * FROM SMSC_USER WHERE systemid = '{systemid}'");
        manager.setSqlUserSelectAll("SELECT systemid FROM SMSC_USER ORDER BY systemid");
        manager.setSqlUserAuthenticate("SELECT systemid, userpassword FROM SMSC_USER WHERE systemid='{systemid}'");
        manager.setSqlUserAdmin("SELECT systemid FROM SMSC_USER WHERE systemid='{systemid}' AND systemid='admin'");

        return manager;
    }

    protected File getInitSqlScript() {
        return new File(TestUtil.getBaseDir(), "src/test/resources/dbusermanagertest-h2.sql");
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        this.ds = new JdbcDataSource();
        this.ds.setURL("jdbc:h2:mem:smscd");
        this.ds.setUser("sa");
        this.ds.setPassword("");

        this.createDatabase();

        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        Statement stm = this.connection.createStatement();
        stm.execute("SHUTDOWN");

        super.tearDown();
    }

}
