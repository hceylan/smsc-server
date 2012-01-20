package org.apache.smscserver.test.messageManager;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

import junit.framework.TestCase;

import org.apache.smscserver.messagemanager.DBMessageManagerFactory;
import org.apache.smscserver.messagemanager.impl.ShortMessageImpl;
import org.apache.smscserver.smsclet.MessageManager;
import org.apache.smscserver.test.TestUtil;
import org.apache.smscserver.util.DBUtils;
import org.apache.smscserver.util.IoUtils;
import org.h2.jdbcx.JdbcDataSource;

public abstract class MessageManagerTemplate extends TestCase {

    private static final String SOURCE_ADDR = "source_addr";
    private static final String DEST_ADDR = "dest_addr";
    private static final String MESSAGE = "Hello World!";
    private JdbcDataSource datasource;
    private Connection connection;
    protected MessageManager messageManager;

    public MessageManagerTemplate() {
        super();
    }

    public MessageManagerTemplate(String name) {
        super(name);
    }

    protected ShortMessageImpl createMessage(String serviceType) {
        ShortMessageImpl sm = new ShortMessageImpl();

        sm.setSourceAddress(MessageManagerTemplate.SOURCE_ADDR);
        sm.setDestinationAddress(MessageManagerTemplate.DEST_ADDR);
        sm.setMessageLength(MessageManagerTemplate.MESSAGE.length());
        sm.setShortMessage(MessageManagerTemplate.MESSAGE);
        sm.setServiceType(serviceType);

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

        this.messageManager = null;
        this.datasource = null;
        this.connection = null;
    }

}