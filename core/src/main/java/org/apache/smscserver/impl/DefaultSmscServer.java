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

package org.apache.smscserver.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.smscserver.ConnectionConfig;
import org.apache.smscserver.SmscServer;
import org.apache.smscserver.SmscServerFactory;
import org.apache.smscserver.command.CommandFactory;
import org.apache.smscserver.listener.Listener;
import org.apache.smscserver.smsclet.SmscException;
import org.apache.smscserver.smsclet.Smsclet;
import org.apache.smscserver.smsclet.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * This is the starting point of all the servers. It invokes a new listener thread. <code>Server</code> implementation
 * is used to create the server socket and handle client connection.
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class DefaultSmscServer implements SmscServer {

    private final Logger LOG = LoggerFactory.getLogger(DefaultSmscServer.class);

    private SmscServerContext serverContext;

    private boolean suspended = false;

    private boolean started = false;

    /**
     * Internal constructor, do not use directly. Use {@link SmscServerFactory} instead
     */
    public DefaultSmscServer(final SmscServerContext serverContext) {
        this.serverContext = serverContext;
    }

    /**
     * Retrieve the command factory used with this server
     * 
     * @return The {@link CommandFactory}
     */
    public CommandFactory getCommandFactory() {
        return this.getServerContext().getCommandFactory();
    }

    /**
     * Retrieve the connection configuration this server
     * 
     * @return The {@link MessageResource}
     */
    public ConnectionConfig getConnectionConfig() {
        return this.getServerContext().getConnectionConfig();
    }

    /**
     * Get a specific listener identified by its name
     * 
     * @param name
     *            The name of the listener
     * @return The {@link Listener} matching the provided name
     */
    public Listener getListener(final String name) {
        return this.getServerContext().getListener(name);
    }

    /**
     * Get all listeners available one this server
     * 
     * @return The current listeners
     */
    public Map<String, Listener> getListeners() {
        return this.getServerContext().getListeners();
    }

    /**
     * Get the root server context.
     */
    public SmscServerContext getServerContext() {
        return this.serverContext;
    }

    /**
     * Get all {@link Smsclet}s registered at this server
     * 
     * @return All {@link Smsclet}s
     */
    public Map<String, Smsclet> getSmsclets() {
        return this.getServerContext().getSmscletContainer().getSmsclets();
    }

    /**
     * Retrieve the user manager used with this server
     * 
     * @return The user manager
     */
    public UserManager getUserManager() {
        return this.getServerContext().getUserManager();
    }

    /**
     * Get the server status.
     */
    public boolean isStopped() {
        return !this.started;
    }

    /**
     * Is the server suspended
     */
    public boolean isSuspended() {
        return this.suspended;
    }

    /**
     * Resume the server handler
     */
    public void resume() {
        if (!this.suspended) {
            return;
        }

        this.LOG.debug("Resuming server");
        Map<String, Listener> listeners = this.serverContext.getListeners();
        for (Listener listener : listeners.values()) {
            listener.resume();
        }

        this.suspended = false;
        this.LOG.debug("Server resumed");
    }

    /**
     * Start the server. Open a new listener thread.
     * 
     * @throws SmscException
     */
    public void start() throws SmscException {
        if (this.serverContext == null) {
            // we have already been stopped, can not be restarted
            throw new IllegalStateException("SmscServer has been stopped. Restart is not supported");
        }

        List<Listener> startedListeners = new ArrayList<Listener>();

        try {
            Map<String, Listener> listeners = this.serverContext.getListeners();
            for (Listener listener : listeners.values()) {
                listener.start(this.serverContext);
                startedListeners.add(listener);
            }

            // init the Smsclet container
            this.serverContext.getSmscletContainer().init(this.serverContext);

            this.started = true;

            this.LOG.info("SMSC server started");
        } catch (Exception e) {
            // must close listeners that we were able to start
            for (Listener listener : startedListeners) {
                listener.stop();
            }

            if (e instanceof SmscException) {
                throw (SmscException) e;
            } else {
                throw (RuntimeException) e;
            }

        }
    }

    /**
     * Stop the server. Stopping the server will close completely and it not supported to restart using {@link #start()}
     * .
     */
    public void stop() {
        if (this.serverContext == null) {
            // we have already been stopped, ignore
            return;
        }

        // stop all listeners
        Map<String, Listener> listeners = this.serverContext.getListeners();
        for (Listener listener : listeners.values()) {
            listener.stop();
        }

        // destroy the Smsclet container
        this.serverContext.getSmscletContainer().destroy();

        // release server resources
        if (this.serverContext != null) {
            this.serverContext.dispose();
            this.serverContext = null;
        }

        this.started = false;
    }

    /**
     * Suspend further requests
     */
    public void suspend() {
        if (!this.started) {
            return;
        }

        this.LOG.debug("Suspending server");
        // stop all listeners
        Map<String, Listener> listeners = this.serverContext.getListeners();
        for (Listener listener : listeners.values()) {
            listener.suspend();
        }

        this.suspended = true;
        this.LOG.debug("Server suspended");
    }
}
