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

package org.apache.smscserver.test.spring;

import java.util.Map;

import junit.framework.Assert;

import org.apache.smscserver.impl.DefaultSmscServer;
import org.apache.smscserver.smsclet.Smsclet;

/**
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * 
 */
public class SmscletsConfigTest extends SpringConfigTestTemplate {

    private Map<String, Smsclet> createSmsclets(String config) {
        DefaultSmscServer server = (DefaultSmscServer) this.createServer("<smsclets>" + config + "</smsclets>");

        return server.getSmsclets();
    }

    public void testSmsclet() throws Throwable {
        Map<String, Smsclet> smsclets = this.createSmsclets("<smsclet name=\"foo\">" + "<beans:bean class=\""
                + TestSmsclet.class.getName() + "\">" + "<beans:property name=\"foo\" value=\"123\" />"
                + "</beans:bean></smsclet>");

        Assert.assertEquals(1, smsclets.size());
        Assert.assertEquals(123, ((TestSmsclet) smsclets.get("foo")).getFoo());
    }

    public void testSmscletMap() throws Throwable {
        Map<String, Smsclet> smsclets = this.createSmsclets("<beans:map>" + "<beans:entry key=\"foo\">"
                + "<beans:bean class=\"" + TestSmsclet.class.getName() + "\">"
                + "<beans:property name=\"foo\" value=\"123\" />" + "</beans:bean>" + "</beans:entry></beans:map>");

        Assert.assertEquals(1, smsclets.size());
        Assert.assertEquals(123, ((TestSmsclet) smsclets.get("foo")).getFoo());
    }

}
