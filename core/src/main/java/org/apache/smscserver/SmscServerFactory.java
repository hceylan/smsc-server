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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.smscserver.command.CommandFactory;
import org.apache.smscserver.impl.DefaultSmscServer;
import org.apache.smscserver.impl.DefaultSmscServerContext;
import org.apache.smscserver.listener.Listener;
import org.apache.smscserver.smsclet.Smsclet;
import org.apache.smscserver.smsclet.UserManager;
import org.apache.smscserver.smscletcontainer.impl.DefaultSmscletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the starting point of all the servers. Creates server instances based on the provided configuration.
 * 
 * @author hceylan
 */
public class SmscServerFactory {

    private final Logger LOG = LoggerFactory.getLogger(SmscServerFactory.class);

    private final DefaultSmscServerContext serverContext;

    /**
     * Creates a server with the default configuration
     */
    public SmscServerFactory() {
        this.serverContext = new DefaultSmscServerContext();
    }

    /**
     * Add a {@link Listener} to this factory
     * 
     * @param name
     *            The name of the listener
     * @param listener
     *            The {@link Listener}
     */
    public void addListener(final String name, final Listener listener) {
        this.serverContext.addListener(name, listener);
    }

    /**
     * Create a {@link DefaultSmscServer} instance based on the provided configuration
     * 
     * @return The {@link DefaultSmscServer} instance
     */
    public SmscServer createServer() {
        this.LOG.info("SMSC Server is starting with the context {}", DefaultSmscServerContext.class.getCanonicalName());
        this.serverContext.info(this.LOG);

        return new DefaultSmscServer(this.serverContext);
    }

    /**
     * Retrieve the command factory used by servers created by this factory
     * 
     * @return The {@link CommandFactory}
     */
    public CommandFactory getCommandFactory() {
        return this.serverContext.getCommandFactory();
    }

    /**
     * Retrieve the connection configuration this server
     * 
     * @return The {@link MessageResource}
     */
    public ConnectionConfig getConnectionConfig() {
        return this.serverContext.getConnectionConfig();
    }

    /**
     * Get a specific {@link Listener} identified by its name
     * 
     * @param name
     *            The name of the listener
     * @return The {@link Listener} matching the provided name
     */
    public Listener getListener(final String name) {
        return this.serverContext.getListener(name);
    }

    /**
     * Get all listeners available on servers created by this factory
     * 
     * @return The current listeners
     */
    public Map<String, Listener> getListeners() {
        return this.serverContext.getListeners();
    }

    /**
     * Get all {@link Smsclet}s registered by servers created by this factory
     * 
     * @return All {@link Smsclet}s
     */
    public Map<String, Smsclet> getSmsclets() {
        return this.serverContext.getSmscletContainer().getSmsclets();
    }

    /**
     * Retrieve the user manager used by servers created by this factory
     * 
     * @return The user manager
     */
    public UserManager getUserManager() {
        return this.serverContext.getUserManager();
    }

    /**
     * Set the command factory to be used by servers created by this factory
     * 
     * @param commandFactory
     *            The {@link CommandFactory}
     * @throws IllegalStateException
     *             If a custom server context has been set
     */
    public void setCommandFactory(final CommandFactory commandFactory) {
        this.serverContext.setCommandFactory(commandFactory);
    }

    /**
     * Set the message resource to be used with this server
     * 
     * @param connectionConfig
     *            The {@link ConnectionConfig} to be used by servers created by this factory
     */
    public void setConnectionConfig(final ConnectionConfig connectionConfig) {
        this.serverContext.setConnectionConfig(connectionConfig);
    }

    /**
     * Set the listeners for servers created by this factory, replaces existing listeners
     * 
     * @param listeners
     *            The listeners to use for this server with the name as the key and the listener as the value
     * @throws IllegalStateException
     *             If a custom server context has been set
     */
    public void setListeners(final Map<String, Listener> listeners) {
        this.serverContext.setListeners(listeners);
    }

    /**
     * Set the {@link Smsclet}s to be active by servers created by this factory. Replaces existing {@link Smsclet}s
     * 
     * @param smsclets
     *            Smsclets as a map with the name as the key and the Smsclet as the value. The Smsclet container will
     *            iterate over the map in the order provided by the Map. If invocation order of Smsclets is of
     *            importance, make sure to provide a ordered Map, for example {@link LinkedHashMap}.
     * @throws IllegalStateException
     *             If a custom server context has been set
     */
    public void setSmsclets(final Map<String, Smsclet> smsclets) {
        this.serverContext.setSmscletContainer(new DefaultSmscletContainer(smsclets));
    }

    /**
     * Set the user manager to be used by servers created by this factory
     * 
     * @param userManager
     *            The {@link UserManager}
     * @throws IllegalStateException
     *             If a custom server context has been set
     */
    public void setUserManager(final UserManager userManager) {
        this.serverContext.setUserManager(userManager);

        userManager.setContext(this.serverContext);
    }
}
