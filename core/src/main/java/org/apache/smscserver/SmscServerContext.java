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
import org.apache.smscserver.smsclet.MessageManager;
import org.apache.smscserver.smsclet.SmscSession;
import org.apache.smscserver.smsclet.SmscletContext;
import org.apache.smscserver.smscletcontainer.SmscletContainer;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * This is basically <code>org.apache.smscserver.test.smsclet.SmscletContext</code> with added connection manager,
 * message resource functionalities.
 * 
 * @author hceylan
 */
public interface SmscServerContext extends SmscletContext {

    /**
     * Releases all components.
     */
    void dispose();

    /**
     * Returns the command factory.
     * 
     * @return the command factory
     */
    CommandFactory getCommandFactory();

    /**
     * Returns the the connection configuration.
     * 
     * @return the connection configuration
     */
    ConnectionConfig getConnectionConfig();

    /**
     * Returns the the delivery manager configuration.
     * 
     * @return the the delivery manager configuration
     */
    DeliveryManagerConfig getDeliveryManagerConfig();

    /**
     * Returns the listener identified with the name
     * 
     * @param name
     *            the identifier of the listener
     * @return the listener identified with the name
     */
    Listener getListener(String name);

    /**
     * Returns map of all the listeners.
     * 
     * @return map of all the listeners
     */
    Map<String, Listener> getListeners();

    /**
     * Returns the message manager.
     * 
     * @return the message manager
     */
    MessageManager getMessageManager();

    /**
     * Returns the timeout time in milliseconds for the session lock.
     * 
     * @see SmscSession#lock
     * @return the timeout time in milliseconds for the session lock
     */
    long getSessionLockTimeout();

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
