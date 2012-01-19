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
import junit.framework.TestCase;

import org.apache.smscserver.command.CommandFactory;
import org.apache.smscserver.command.impl.BindCommand;
import org.apache.smscserver.command.impl.EnquireLinkCommand;
import org.apache.smscserver.impl.DefaultSmscServer;
import org.apache.smscserver.listener.Listener;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

/**
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * 
 */
public class SpringConfigTest extends TestCase {

    public void test() throws Throwable {
        // FIXME: Hasan variable not expanded
        System.setProperty("SMSC_HOME", "./target/");

        XmlBeanFactory factory = new XmlBeanFactory(new FileSystemResource(
                "src/test/resources/spring-config/config-spring-1.xml"));

        DefaultSmscServer server = (DefaultSmscServer) factory.getBean("server");

        Assert.assertEquals(500, server.getServerContext().getConnectionConfig().getMaxBinds());
        Assert.assertEquals(124, server.getServerContext().getConnectionConfig().getMaxBindFailures());
        Assert.assertEquals(125, server.getServerContext().getConnectionConfig().getBindFailureDelay());

        Map<String, Listener> listeners = server.getServerContext().getListeners();
        Assert.assertEquals(3, listeners.size());

        Listener listener = listeners.get("listener1");
        Assert.assertNotNull(listener);
        Assert.assertTrue(listener instanceof MyCustomListener);
        Assert.assertEquals(2223, listener.getPort());

        listener = listeners.get("listener2");
        Assert.assertNotNull(listener);
        Assert.assertTrue(listener instanceof MyCustomListener);
        Assert.assertEquals(2224, listener.getPort());

        CommandFactory cf = server.getCommandFactory();
        Assert.assertTrue(cf.getCommand(9) instanceof BindCommand);
        Assert.assertTrue(cf.getCommand(21) instanceof EnquireLinkCommand);

        Assert.assertEquals(2, server.getSmsclets().size());
        Assert.assertEquals(123, ((TestSmsclet) server.getSmsclets().get("smsclet1")).getFoo());
        Assert.assertEquals(223, ((TestSmsclet) server.getSmsclets().get("smsclet2")).getFoo());

        Assert.assertNotNull(server.getServerContext().getMessageManager());
    }
}
