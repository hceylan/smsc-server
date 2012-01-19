package org.apache.smscserver.test.messageManager;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.smscserver.messagemanager.DBMessageManagerFactory;
import org.h2.jdbcx.JdbcDataSource;

public class MessageManagerTest extends TestCase {

    private JdbcDataSource datasource;

    private Connection connection;

    private DBMessageManagerFactory dbMessageManagerFactory;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        this.dbMessageManagerFactory = new DBMessageManagerFactory("h2", "jdbc:h2:mem:smscd");

        this.datasource = new JdbcDataSource();
        this.datasource.setURL("jdbc:h2:mem:smscd");
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
