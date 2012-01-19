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

import junit.framework.Assert;

import org.apache.smscserver.ConnectionConfigFactory;
import org.apache.smscserver.SmscServerFactory;

/**
 * 
 * @author hceylan
 * 
 */
public class UnlimitedMaxBindTest extends ClientTestTemplate {
    private static final String UNKNOWN_USERNAME = "foo";

    private static final String UNKNOWN_PASSWORD = "bar";

    @Override
    protected SmscServerFactory createServer() throws Exception {
        SmscServerFactory server = super.createServer();

        ConnectionConfigFactory ccFactory = new ConnectionConfigFactory();

        ccFactory.setMaxBindFailures(0);

        server.setConnectionConfig(ccFactory.createConnectionConfig());
        return server;
    }

    public void testBind() throws Exception {
        // must never be disconnected
        Assert.assertFalse(this.bind(UnlimitedMaxBindTest.UNKNOWN_USERNAME, UnlimitedMaxBindTest.UNKNOWN_PASSWORD)
                .getCommandStatus() == 0);
        Assert.assertFalse(this.bind(UnlimitedMaxBindTest.UNKNOWN_USERNAME, UnlimitedMaxBindTest.UNKNOWN_PASSWORD)
                .getCommandStatus() == 0);
        Assert.assertFalse(this.bind(UnlimitedMaxBindTest.UNKNOWN_USERNAME, UnlimitedMaxBindTest.UNKNOWN_PASSWORD)
                .getCommandStatus() == 0);
        Assert.assertFalse(this.bind(UnlimitedMaxBindTest.UNKNOWN_USERNAME, UnlimitedMaxBindTest.UNKNOWN_PASSWORD)
                .getCommandStatus() == 0);
        Assert.assertFalse(this.bind(UnlimitedMaxBindTest.UNKNOWN_USERNAME, UnlimitedMaxBindTest.UNKNOWN_PASSWORD)
                .getCommandStatus() == 0);
        Assert.assertFalse(this.bind(UnlimitedMaxBindTest.UNKNOWN_USERNAME, UnlimitedMaxBindTest.UNKNOWN_PASSWORD)
                .getCommandStatus() == 0);
        Assert.assertFalse(this.bind(UnlimitedMaxBindTest.UNKNOWN_USERNAME, UnlimitedMaxBindTest.UNKNOWN_PASSWORD)
                .getCommandStatus() == 0);
        Assert.assertFalse(this.bind(UnlimitedMaxBindTest.UNKNOWN_USERNAME, UnlimitedMaxBindTest.UNKNOWN_PASSWORD)
                .getCommandStatus() == 0);
        Assert.assertFalse(this.bind(UnlimitedMaxBindTest.UNKNOWN_USERNAME, UnlimitedMaxBindTest.UNKNOWN_PASSWORD)
                .getCommandStatus() == 0);
    }
}
