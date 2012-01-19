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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.smscserver.impl.DefaultSmscServer;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * 
 */
public class PropertyPlaceholderTest extends TestCase {

    public void test() throws Throwable {
        System.setProperty("port2", "3333");

        FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(
                "src/test/resources/spring-config/config-property-placeholder.xml");

        DefaultSmscServer server = (DefaultSmscServer) ctx.getBean("server");

        Assert.assertEquals(2222, server.getServerContext().getListener("listener0").getPort());
        Assert.assertEquals(3333, server.getServerContext().getListener("listener1").getPort());
    }
}
