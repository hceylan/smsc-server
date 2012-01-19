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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.smscserver.ConnectionConfig;
import org.apache.smscserver.ConnectionConfigFactory;
import org.apache.smscserver.SmscServerContext;
import org.apache.smscserver.command.CommandFactory;
import org.apache.smscserver.command.CommandFactoryFactory;
import org.apache.smscserver.listener.Listener;
import org.apache.smscserver.listener.ListenerFactory;
import org.apache.smscserver.smsclet.Authority;
import org.apache.smscserver.smsclet.MessageManager;
import org.apache.smscserver.smsclet.SmscStatistics;
import org.apache.smscserver.smsclet.Smsclet;
import org.apache.smscserver.smsclet.UserManager;
import org.apache.smscserver.smscletcontainer.SmscletContainer;
import org.apache.smscserver.smscletcontainer.impl.DefaultSmscletContainer;
import org.apache.smscserver.usermanager.PropertiesUserManagerFactory;
import org.apache.smscserver.usermanager.impl.BaseUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * SMSC server configuration implementation. It holds all the components used.
 * 
 * @author hceylan
 */
public class DefaultSmscServerContext implements SmscServerContext {

    private final Logger LOG = LoggerFactory.getLogger(DefaultSmscServerContext.class);

    private static final List<Authority> ADMIN_AUTHORITIES = new ArrayList<Authority>();

    private MessageManager messageManager = null;// new DBMessageManagerFactory().createMessageManager();

    private UserManager userManager = new PropertiesUserManagerFactory().createUserManager();

    private SmscletContainer smscletContainer = new DefaultSmscletContainer();

    private SmscStatistics statistics = new DefaultSmscStatistics();

    private CommandFactory commandFactory = new CommandFactoryFactory().createCommandFactory();

    private ConnectionConfig connectionConfig = new ConnectionConfigFactory().createConnectionConfig();

    private Map<String, Listener> listeners = new HashMap<String, Listener>();

    /**
     * The thread pool executor to be used by the server using this context
     */
    private ThreadPoolExecutor threadPoolExecutor = null;

    public DefaultSmscServerContext() {
        // create the default listener
        this.listeners.put("default", new ListenerFactory().createListener());
    }

    public void addListener(String name, Listener listener) {
        this.listeners.put(name, listener);
    }

    /**
     * Create default users.
     */
    public void createDefaultUsers() throws Exception {
        UserManager userManager = this.getUserManager();

        // create admin user
        String adminName = userManager.getAdminName();
        if (!userManager.doesExist(adminName)) {
            this.LOG.info("Creating user : " + adminName);
            BaseUser adminUser = new BaseUser();
            adminUser.setName(adminName);
            adminUser.setPassword(adminName);
            adminUser.setEnabled(true);

            adminUser.setAuthorities(DefaultSmscServerContext.ADMIN_AUTHORITIES);

            adminUser.setMaxIdleTime(0);
            userManager.save(adminUser);
        }
    }

    /**
     * Close all the components.
     */
    public void dispose() {
        this.listeners.clear();
        this.smscletContainer.getSmsclets().clear();
        if (this.threadPoolExecutor != null) {
            this.LOG.debug("Shutting down the thread pool executor");
            this.threadPoolExecutor.shutdown();
            try {
                this.threadPoolExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
            } finally {
                // TODO: how to handle?
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    public CommandFactory getCommandFactory() {
        return this.commandFactory;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public ConnectionConfig getConnectionConfig() {
        return this.connectionConfig;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Listener getListener(String name) {
        return this.listeners.get(name);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Map<String, Listener> getListeners() {
        return this.listeners;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public MessageManager getMessageManager() {
        return this.messageManager;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Smsclet getSmsclet(String name) {
        return this.smscletContainer.getSmsclet(name);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public SmscletContainer getSmscletContainer() {
        return this.smscletContainer;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public SmscStatistics getSmscStatistics() {
        return this.statistics;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public synchronized ThreadPoolExecutor getThreadPoolExecutor() {
        if (this.threadPoolExecutor == null) {
            int minThreads = this.connectionConfig.getMinThreads();
            int maxThreads = this.connectionConfig.getMaxThreads();

            if (maxThreads < 1) {
                int maxBinds = this.connectionConfig.getMaxBinds();
                if (maxBinds > 0) {
                    maxThreads = maxBinds;
                } else {
                    maxThreads = 16;
                }
            }

            if (minThreads < 1) {
                minThreads = maxThreads / 4;
            }

            this.LOG.debug("Intializing shared thread pool executor with min/max threads of {}/{}", minThreads,
                    maxThreads);
            this.threadPoolExecutor = new OrderedThreadPoolExecutor(minThreads, maxThreads);
        }

        return this.threadPoolExecutor;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public UserManager getUserManager() {
        return this.userManager;
    }

    public Listener removeListener(String name) {
        return this.listeners.remove(name);
    }

    public void setCommandFactory(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public void setConnectionConfig(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
    }

    public void setListener(String name, Listener listener) {
        this.listeners.put(name, listener);
    }

    public void setListeners(Map<String, Listener> listeners) {
        this.listeners = listeners;
    }

    public void setMessageManager(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    public void setSmscletContainer(SmscletContainer smscletContainer) {
        this.smscletContainer = smscletContainer;
    }

    public void setSmscStatistics(SmscStatistics statistics) {
        this.statistics = statistics;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
