package org.apache.smscserver.test.messageManager;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.smscserver.messagemanager.DBMessageManagerFactory;
import org.apache.smscserver.messagemanager.impl.ShortMessageImpl;
import org.apache.smscserver.smsclet.MessageManager;
import org.apache.smscserver.test.TestUtil;
import org.apache.smscserver.util.IoUtils;
import org.h2.jdbcx.JdbcDataSource;

public class MessageManagerTest extends TestCase {

    private static final String SOURCE_ADDR = "source_addr";
    private static final String DEST_ADDR = "dest_addr";
    private static final String MESSAGE = "Hello World!";

    private JdbcDataSource datasource;

    private Connection connection;

    private MessageManager messageManager;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        this.messageManager = new DBMessageManagerFactory("h2", "jdbc:h2:mem:smscd").createMessageManager();

        this.datasource = new JdbcDataSource();
        this.datasource.setURL("jdbc:h2:mem:smscd");
        this.datasource.setUser("sa");
        this.datasource.setPassword("");

        this.connection = this.datasource.getConnection();

        File file = new File(TestUtil.getBaseDir(), "src/test/resources/messages-createtable-h2.sql");
        String ddl = IoUtils.readFully(new FileReader(file));

        Statement stm = this.connection.createStatement();
        stm.execute(ddl);

        super.setUp();
    }

    public void testStoreMessage() throws Exception {
        ShortMessageImpl sm = new ShortMessageImpl();

        sm.setSourceAddress(MessageManagerTest.SOURCE_ADDR);
        sm.setDestinationAddress(MessageManagerTest.DEST_ADDR);
        sm.setMessageLength(MessageManagerTest.MESSAGE.length());
        sm.setShortMessage(MessageManagerTest.MESSAGE);

        this.messageManager.storeShortMessage(sm);
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
