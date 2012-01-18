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

package org.apache.smscserver.clienttests;

import ie.omk.smpp.Connection;
import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.net.TcpLink;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.smscserver.ConnectionConfigFactory;
import org.apache.smscserver.SmscServer;
import org.apache.smscserver.SmscServerFactory;
import org.apache.smscserver.impl.DefaultSmscIoSession;
import org.apache.smscserver.impl.DefaultSmscServer;
import org.apache.smscserver.listener.ListenerFactory;
import org.apache.smscserver.test.TestUtil;
import org.apache.smscserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.smscserver.usermanager.PropertiesUserManagerFactory;
import org.apache.smscserver.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author hceylan
 * 
 */
public abstract class ClientTestTemplate extends TestCase {

    private final Logger LOG = LoggerFactory.getLogger(ClientTestTemplate.class);

    protected static final String ADMIN_PASSWORD = "admin";

    protected static final String ADMIN_USERNAME = "admin";

    protected static final String TESTUSER2_USERNAME = "testuser2";

    protected static final String TESTUSER1_USERNAME = "testuser1";

    protected static final String TESTUSER_PASSWORD = "password";

    protected SmscServer server;

    protected Connection connection;

    private static final File USERS_FILE = new File(TestUtil.getBaseDir(), "src/test/resources/users.properties");

    private static final File TEST_TMP_DIR = new File("test-tmp");

    protected static final File ROOT_DIR = new File(ClientTestTemplate.TEST_TMP_DIR, "smscroot");

    protected BindResp bind(Connection connection, int type, String sysID, String password) throws Exception {
        return connection.bind(type, sysID, password, "default");
    }

    protected BindResp bind(Connection connection, String sysID, String password) throws Exception {
        return connection.bind(Connection.TRANSCEIVER, sysID, password, "default");
    }

    protected BindResp bind(int type, String sysID, String password) throws Exception {
        return this.bind(this.connection, type, sysID, password);
    }

    protected BindResp bind(String sysID, String password) throws Exception {
        return this.bind(this.connection, Connection.TRANSCEIVER, sysID, password);
    }

    protected BindResp bindTest() throws Exception {
        return this.bind(ClientTestTemplate.TESTUSER1_USERNAME, ClientTestTemplate.TESTUSER_PASSWORD);
    }

    protected void cleanTmpDirs() throws IOException {
        if (ClientTestTemplate.TEST_TMP_DIR.exists()) {
            IoUtils.delete(ClientTestTemplate.TEST_TMP_DIR);
        }
    }

    /**
     * @throws Exception
     */
    protected void connectClient() throws Exception {
        this.connection = this.createConnection();
    }

    protected Connection createConnection() throws Exception {
        Connection myConnection = new Connection(this.createLink(), false);

        myConnection.addObserver(new ConnectionObserver() {

            /**
             * {@inheritDoc}
             * 
             */
            public void packetReceived(Connection source, SMPPPacket packet) {
                ClientTestTemplate.this.LOG.debug("Received packet: " + packet.toString());
            }

            /**
             * {@inheritDoc}
             * 
             */
            public void update(Connection source, SMPPEvent event) {
                ClientTestTemplate.this.LOG.debug("Connection event: " + event.getType());
            }
        });

        return myConnection;
    }

    protected ConnectionConfigFactory createConnectionConfigFactory() {
        return new ConnectionConfigFactory();
    }

    private TcpLink createLink() throws UnknownHostException, IOException {
        InetAddress smscAddr = InetAddress.getByName("localhost");
        TcpLink myLink = new TcpLink(smscAddr);// Open the connection (not required)..
        myLink.open();

        return myLink;
    }

    protected SmscServerFactory createServer() throws Exception {
        Assert.assertTrue(ClientTestTemplate.USERS_FILE.getAbsolutePath() + " must exist",
                ClientTestTemplate.USERS_FILE.exists());

        SmscServerFactory serverFactory = new SmscServerFactory();

        serverFactory.setConnectionConfig(this.createConnectionConfigFactory().createConnectionConfig());

        ListenerFactory listenerFactory = new ListenerFactory();

        listenerFactory.setPort(0);

        serverFactory.addListener("default", listenerFactory.createListener());

        PropertiesUserManagerFactory umFactory = new PropertiesUserManagerFactory();
        umFactory.setAdminName("admin");
        umFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());
        umFactory.setFile(ClientTestTemplate.USERS_FILE);

        serverFactory.setUserManager(umFactory.createUserManager());

        return serverFactory;
    }

    protected void disconnect() {
        this.disconnect(this.connection);
        this.connection = null;
    }

    protected void disconnect(Connection connection) {
        if (connection != null) {
            try {
                this.connection.unbind();
            } catch (Exception e) {
                // ignore
            }

            try {
                connection.closeLink();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    protected DefaultSmscIoSession getActiveSession() {
        return ((DefaultSmscServer) this.server).getListener("default").getActiveSessions().iterator().next();
    }

    protected int getListenerPort() {
        return ((DefaultSmscServer) this.server).getListener("default").getPort();
    }

    /**
     * @throws IOException
     */
    protected void initDirs() throws IOException {
        this.cleanTmpDirs();

        ClientTestTemplate.TEST_TMP_DIR.mkdirs();
        ClientTestTemplate.ROOT_DIR.mkdirs();
    }

    /**
     * @throws IOException
     * @throws Exception
     */
    protected void initServer() throws IOException, Exception {
        if (this.isStartServer()) {
            // cast to internal class to get access to getters
            this.server = this.createServer().createServer();

            if (this.isStartServer()) {
                this.server.start();
            }
        }
    }

    protected boolean isStartServer() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        this.initDirs();

        this.initServer();

        this.connectClient();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        this.disconnect();

        if (this.server != null) {
            try {
                this.server.stop();
            } catch (NullPointerException e) {
                // a bug in the IBM JVM might cause Thread.interrupt() to throw an NPE
                // see http://www-01.ibm.com/support/docview.wss?uid=swg1IZ52037&wv=1
            }

            this.server = null;
        }

        this.cleanTmpDirs();
    }
}
