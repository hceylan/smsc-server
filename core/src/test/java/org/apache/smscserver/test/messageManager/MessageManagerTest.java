package org.apache.smscserver.test.messageManager;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.smscserver.messagemanager.DBMessageManagerFactory;
import org.apache.smscserver.smsclet.SmscException;
import org.hsqldb.jdbc.jdbcDataSource;

public class MessageManagerTest extends TestCase {

    private jdbcDataSource datasource;

    private Connection connection;

    protected DBMessageManagerFactory createMessageManagerFactory() throws SmscException {
        DBMessageManagerFactory manager = new DBMessageManagerFactory();

        manager.setDataSource(this.datasource);
        manager.setSqlCreateTable("INSERT INTO SMSC_USER (systemid, userpassword, enableflag, idletime, maxbindnumber, maxbindperip) VALUES ('{systemid}', '{userpassword}', {enableflag}, {idletime}, {maxbindnumber}, {maxbindperip})");
        manager.setSqlInsertMessage("UPDATE SMSC_USER SET userpassword='{userpassword}',enableflag={enableflag},idletime={idletime},maxbindnumber={maxbindnumber}, maxbindperip={maxbindperip} WHERE systemid='{systemid}'");
        manager.setSqlUpdateMessage("DELETE FROM SMSC_USER WHERE systemid = '{systemid}'");
        manager.setSqlUpdateMessage("SELECT * FROM SMSC_USER WHERE systemid = '{systemid}'");
        manager.setSqlSelectLatestReplacableMessage("SELECT systemid FROM SMSC_USER ORDER BY systemid");

        return manager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        this.datasource = new jdbcDataSource();
        this.datasource.setDatabase("jdbc:hsqldb:mem:smscd");
        this.datasource.setUser("sa");
        this.datasource.setPassword("");

        this.connection = this.datasource.getConnection();

        super.setUp();
    }

    public void testStoreMessage() {
        Assert.fail("TODO");
    }

    public void testStoreMessageScheduled() {
        Assert.fail("TODO");
    }

    public void testStoreMessageScheduledInPast() {
        Assert.fail("TODO");
    }

    public void testStoreMessageWithCancel() {
        Assert.fail("TODO");
    }

    public void testStoreMessageWithCancelMultiExisting() {
        Assert.fail("TODO");
    }

    public void testStoreMessageWithCancelNoOriginal() {
        Assert.fail("TODO");
    }

}
