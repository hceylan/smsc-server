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

package org.apache.smscserver;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.smscserver.command.CommandFactory;
import org.apache.smscserver.listener.Listener;
import org.apache.smscserver.smsclet.SmscletContext;
import org.apache.smscserver.smscletcontainer.SmscletContainer;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * This is basically <code>org.apache.smscserver.smsclet.SmscletContext</code> with added connection manager, message
 * resource functionalities.
 * 
 * @author hceylan
 */
public interface SmscServerContext extends SmscletContext {

    /**
     * Release all components.
     */
    void dispose();

    /**
     * Get the command factory.
     */
    CommandFactory getCommandFactory();

    ConnectionConfig getConnectionConfig();

    Listener getListener(String name);

    Map<String, Listener> getListeners();

    /**
     * Get smsclet container.
     */
    SmscletContainer getSmscletContainer();

    /**
     * Returns the thread pool executor for this context.
     * 
     * @return the thread pool executor for this context.
     */
    ThreadPoolExecutor getThreadPoolExecutor();
}
