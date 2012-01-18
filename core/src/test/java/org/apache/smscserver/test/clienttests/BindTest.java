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

package org.apache.smscserver.test.clienttests;

import ie.omk.smpp.Connection;
import junit.framework.Assert;

import org.apache.smscserver.smsclet.SmscStatistics;

/**
 * 
 * @author hceylan
 * 
 */
public class BindTest extends ClientTestTemplate {
    private static final String UNKNOWN_USERNAME = "foo";

    private static final String UNKNOWN_PASSWORD = "bar";

    public void testBindCount() throws Exception {
        SmscStatistics stats = this.server.getServerContext().getSmscStatistics();

        Assert.assertTrue(this.bind(ClientTestTemplate.ADMIN_USERNAME, ClientTestTemplate.ADMIN_PASSWORD)
                .getCommandStatus() == 0);
        int n = stats.getCurrentBindNumber();
        Assert.assertEquals(1, n);

        this.connection.unbind();

        Assert.assertEquals(0, stats.getCurrentBindNumber());
    }

    public void testBindDisabledUser() throws Exception {
        Assert.assertFalse(this.bind("testuser4", "password").getCommandStatus() == 0);
    }

    public void testBindWithEmptyCorrectPassword() throws Exception {
        Assert.assertTrue(this.bind("testuser3", null).getCommandStatus() == 0);
    }

    public void testCommandWithoutLogin() throws Exception {
        // TODO: Hasan
    }

    public void testLogin() throws Exception {
        Assert.assertTrue(this.bind(ClientTestTemplate.ADMIN_USERNAME, ClientTestTemplate.ADMIN_PASSWORD)
                .getCommandStatus() == 0);
    }

    public void testLoginIncorrectPassword() throws Exception {
        Assert.assertFalse(this.bind(ClientTestTemplate.ADMIN_USERNAME, BindTest.UNKNOWN_PASSWORD).getCommandStatus() == 0);
    }

    public void testLoginNoUser() throws Exception {
        Assert.assertFalse(this.bind(null, null).getCommandStatus() == 0);
    }

    public void testLoginUnknownUser() throws Exception {
        Assert.assertFalse(this.bind(BindTest.UNKNOWN_USERNAME, BindTest.UNKNOWN_PASSWORD).getCommandStatus() == 0);
    }

    public void testLoginWithEmptyPassword() throws Exception {
        Assert.assertFalse(this.bind(ClientTestTemplate.ADMIN_USERNAME, null).getCommandStatus() == 0);
    }

    public void testLoginWithMaxConnections() throws Exception {
        Connection connection1 = null;
        Connection connection2 = null;
        Connection connection3 = null;
        Connection connection4 = null;
        try {
            connection1 = this.createConnection();
            connection2 = this.createConnection();
            connection3 = this.createConnection();
            connection4 = this.createConnection();

            Assert.assertTrue(this.bind(connection1, ClientTestTemplate.TESTUSER1_USERNAME,
                    ClientTestTemplate.TESTUSER_PASSWORD).getCommandStatus() == 0);
            Assert.assertTrue(this.bind(connection2, ClientTestTemplate.TESTUSER1_USERNAME,
                    ClientTestTemplate.TESTUSER_PASSWORD).getCommandStatus() == 0);
            Assert.assertTrue(this.bind(ClientTestTemplate.TESTUSER1_USERNAME, ClientTestTemplate.TESTUSER_PASSWORD)
                    .getCommandStatus() == 0);

            try {
                Assert.assertFalse(this.bind(ClientTestTemplate.TESTUSER1_USERNAME,
                        ClientTestTemplate.TESTUSER_PASSWORD).getCommandStatus() == 0);
            } catch (Exception e) {
                // expected
            }
        } finally {
            this.disconnect(connection1);
            this.disconnect(connection2);
            this.disconnect(connection3);
            this.disconnect(connection4);
        }
    }

    /*
     * public void testLoginWithMaxConnectionsPerIp() throws Exception { String[] ips = getHostAddresses();
     * 
     * if(ips.length > 1) { FTPClient client2 = new FTPClient(); client2.connect(ips[0], port); FTPClient client3 = new
     * FTPClient(); client3.connect(ips[0], port); FTPClient client4 = new FTPClient(); client4.connect(ips[1], port);
     * FTPClient client5 = new FTPClient(); client5.connect(ips[1], port); FTPClient client6 = new FTPClient();
     * client6.connect(ips[1], port);
     * 
     * assertTrue(client2.login(TESTUSER2_USERNAME, TESTUSER_PASSWORD)); assertTrue(client3.login(TESTUSER2_USERNAME,
     * TESTUSER_PASSWORD)); assertTrue(client4.login(TESTUSER2_USERNAME, TESTUSER_PASSWORD));
     * assertTrue(client5.login(TESTUSER2_USERNAME, TESTUSER_PASSWORD));
     * 
     * try{ assertTrue(client6.login(TESTUSER2_USERNAME, TESTUSER_PASSWORD));
     * fail("Must throw FTPConnectionClosedException"); } catch(FTPConnectionClosedException e) { // expected } } else {
     * // ignore test } }
     */
    /*
     * public void testLoginWithMaxConnectionsMulti() throws Exception { for(int i = 0; i<50; i++) {
     * testLoginWithMaxConnections(); } }
     */

    public void testReBind() throws Exception {
        Assert.assertFalse(this.bind(ClientTestTemplate.ADMIN_USERNAME, BindTest.UNKNOWN_PASSWORD).getCommandStatus() == 0);
        Assert.assertTrue(this.bind(ClientTestTemplate.ADMIN_USERNAME, ClientTestTemplate.ADMIN_PASSWORD)
                .getCommandStatus() == 0);
    }

}
