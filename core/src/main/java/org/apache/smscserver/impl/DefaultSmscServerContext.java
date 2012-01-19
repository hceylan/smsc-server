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
import org.apache.smscserver.SmscServerConfigurationException;
import org.apache.smscserver.SmscServerContext;
import org.apache.smscserver.command.CommandFactory;
import org.apache.smscserver.command.CommandFactoryFactory;
import org.apache.smscserver.listener.Listener;
import org.apache.smscserver.listener.ListenerFactory;
import org.apache.smscserver.messagemanager.DBMessageManagerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSmscServer.class);

    private static final List<Authority> ADMIN_AUTHORITIES = new ArrayList<Authority>();

    private final static String SMSC_HOME = DefaultSmscServerContext.getSmscHome();

    private static String getSmscHome() {
        try {
            String smscHome = System.getProperty("SMSC_HOME");
            if (smscHome != null) {
                return smscHome;
            }

            smscHome = new java.io.File(".").getCanonicalPath();
            DefaultSmscServerContext.LOG.warn("SMSC_HOME is not provided, using " + smscHome);

            return smscHome;
        } catch (Exception e) {
            throw new SmscServerConfigurationException("Error determining SMSC_HOME", e);
        }
    }

    private MessageManager messageManager = null;
    private UserManager userManager = new PropertiesUserManagerFactory().createUserManager();
    private SmscletContainer smscletContainer = new DefaultSmscletContainer();
    private SmscStatistics statistics = new DefaultSmscStatistics();
    private CommandFactory commandFactory = null;
    private ConnectionConfig connectionConfig = new ConnectionConfigFactory().createConnectionConfig();

    private Map<String, Listener> listeners = new HashMap<String, Listener>();

    /**
     * The thread pool executor to be used by the server using this context
     */
    private ThreadPoolExecutor threadPoolExecutor = null;

    public DefaultSmscServerContext() {
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
            DefaultSmscServerContext.LOG.info("Creating user : " + adminName);
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
            DefaultSmscServerContext.LOG.debug("Shutting down the thread pool executor");
            this.threadPoolExecutor.shutdown();
            try {
                this.threadPoolExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
            } finally {
                if (!this.threadPoolExecutor.isTerminated()) {
                    DefaultSmscServerContext.LOG.warn("Forcing shutdown on thread pool...");
                    this.threadPoolExecutor.shutdownNow();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    public CommandFactory getCommandFactory() {
        if (this.commandFactory == null) {
            this.commandFactory = new CommandFactoryFactory().createCommandFactory();
        }

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
        if (this.messageManager == null) {
            this.messageManager = new DBMessageManagerFactory("h2", "jdbc:h2:" + DefaultSmscServerContext.SMSC_HOME
                    + "/db/smsc").createMessageManager();
        }

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

            DefaultSmscServerContext.LOG.debug("Intializing shared thread pool executor with min/max threads of {}/{}",
                    minThreads, maxThreads);
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

    public void info() {
        DefaultSmscServerContext.LOG.info("Using {} as the connection configuration", this.getConnectionConfig()
                .getClass().getCanonicalName());
        DefaultSmscServerContext.LOG.info("Using {} as the message manager", this.getMessageManager().getClass()
                .getCanonicalName());
        DefaultSmscServerContext.LOG.info("Using {} as the SMSCLet Container", this.getSmscletContainer().getClass()
                .getCanonicalName());
        DefaultSmscServerContext.LOG.info("Using {} as the statistics provider", this.getSmscStatistics().getClass()
                .getCanonicalName());
        DefaultSmscServerContext.LOG.info("Using {} as the command factory", this.getCommandFactory().getClass()
                .getCanonicalName());
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
