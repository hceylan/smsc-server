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
import junit.framework.Assert;

/**
 * 
 * @author hceylan
 * 
 */
public class DefaultMaxLoginTest extends ClientTestTemplate {

    public void testLogin() throws Exception {
        Connection connection1 = null;
        Connection connection2 = null;
        Connection connection3 = null;
        try {
            connection1 = this.createConnection();
            connection2 = this.createConnection();
            connection3 = this.createConnection();

            Assert.assertTrue(this.bind(connection1, ClientTestTemplate.TESTUSER1_USERNAME,
                    ClientTestTemplate.TESTUSER_PASSWORD).getCommandStatus() == 0);
            Assert.assertTrue(this.bind(connection2, ClientTestTemplate.TESTUSER1_USERNAME,
                    ClientTestTemplate.TESTUSER_PASSWORD).getCommandStatus() == 0);
            Assert.assertTrue(this.bind(connection3, ClientTestTemplate.TESTUSER1_USERNAME,
                    ClientTestTemplate.TESTUSER_PASSWORD).getCommandStatus() == 0);

            Assert.assertFalse(this.bind(ClientTestTemplate.TESTUSER1_USERNAME, ClientTestTemplate.TESTUSER_PASSWORD)
                    .getCommandStatus() == 0);
        } finally {
            this.unbind(connection1);
            this.unbind(connection2);
            this.unbind(connection3);
        }
    }
}
