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
import org.apache.smscserver.smsclet.ShortMessage;
import org.apache.smscserver.smsclet.ShortMessageStatus;
import org.apache.smscserver.test.TestUtil;
import org.apache.smscserver.util.DBUtils;
import org.apache.smscserver.util.IoUtils;
import org.h2.jdbcx.JdbcDataSource;

public class MessageManagerTest extends TestCase {

    private static final String DEFAULT = "def";
    private static final String SOURCE_ADDR = "source_addr";
    private static final String DEST_ADDR = "dest_addr";
    private static final String MESSAGE = "Hello World!";

    private JdbcDataSource datasource;

    private Connection connection;

    private MessageManager messageManager;

    private ShortMessageImpl createMessage() {
        ShortMessageImpl sm = new ShortMessageImpl();

        sm.setSourceAddress(MessageManagerTest.SOURCE_ADDR);
        sm.setDestinationAddress(MessageManagerTest.DEST_ADDR);
        sm.setMessageLength(MessageManagerTest.MESSAGE.length());
        sm.setShortMessage(MessageManagerTest.MESSAGE);
        sm.setServiceType(MessageManagerTest.DEFAULT);

        return sm;
    }

    /**
     * {@inheritDoc}
     * 
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

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    protected void tearDown() throws Exception {
        DBUtils.closeQuitely(this.connection);
        this.connection = null;
    }

    public void testStoreMessage() throws Exception {
        ShortMessage sm = this.createMessage();

        this.messageManager.storeShortMessage(sm);
    }

    public void testStoreMessageWithCancelNoOriginal() throws Exception {
        ShortMessageImpl sm1 = this.createMessage();
        this.messageManager.storeShortMessage(sm1);

        sm1.setStatus(ShortMessageStatus.DELIVERED);
        this.messageManager.storeShortMessage(sm1);

        ShortMessageImpl sm2 = this.createMessage();
        sm2.setReplaceIfPresent(true);
        this.messageManager.replace(sm2, false);

        sm1 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm1.getId());
        sm2 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm2.getId());

        Assert.assertNull(sm1.getReplacedBy());
        Assert.assertNull(sm2.getReplaced());
    }

    public void testStoreMessageWithReplace() throws Exception {
        ShortMessageImpl sm1 = this.createMessage();
        this.messageManager.storeShortMessage(sm1);

        ShortMessageImpl sm2 = this.createMessage();
        sm2.setReplaceIfPresent(true);
        this.messageManager.replace(sm2, false);

        sm1 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm1.getId());
        sm2 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm2.getId());

        Assert.assertEquals(sm2.getId(), sm1.getReplacedBy());
        Assert.assertEquals(sm1.getId(), sm2.getReplaced());
    }

    public void testStoreMessageWithReplaceMultiExisting() throws Exception {
        ShortMessageImpl sm1 = this.createMessage();
        this.messageManager.storeShortMessage(sm1);

        Thread.sleep(1000);

        ShortMessageImpl sm2 = this.createMessage();
        this.messageManager.storeShortMessage(sm2);

        ShortMessageImpl smr = this.createMessage();
        smr.setReplaceIfPresent(true);
        this.messageManager.replace(smr, false);

        sm1 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm1.getId());
        sm2 = (ShortMessageImpl) this.messageManager.selectShortMessage(sm2.getId());
        smr = (ShortMessageImpl) this.messageManager.selectShortMessage(smr.getId());

        Assert.assertNull(sm1.getReplacedBy());
        Assert.assertEquals(smr.getId(), sm2.getReplacedBy());
        Assert.assertEquals(sm2.getId(), smr.getReplaced());
    }
}
