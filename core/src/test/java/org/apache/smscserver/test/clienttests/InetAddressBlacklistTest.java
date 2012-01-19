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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.smscserver.SmscServerFactory;
import org.apache.smscserver.listener.ListenerFactory;

/**
 * 
 * @author hceylan
 * 
 */
public class InetAddressBlacklistTest extends ClientTestTemplate {
    @Override
    protected SmscServerFactory createServer() throws Exception {
        SmscServerFactory server = super.createServer();

        ListenerFactory factory = new ListenerFactory(server.getServerContext().getListener("default"));

        List<InetAddress> blockedAddresses = new ArrayList<InetAddress>();
        blockedAddresses.add(InetAddress.getByName("localhost"));

        factory.setBlockedAddresses(blockedAddresses);

        server.addListener("default", factory.createListener());

        return server;
    }

    public void testConnect() throws Exception {
        try {
            this.bind(ClientTestTemplate.TESTUSER1_USERNAME, ClientTestTemplate.TESTUSER_PASSWORD);

            Assert.fail("Must throw");
        } catch (Exception e) {
            // OK
        }
    }
}
