/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.smscserver.message.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.smscserver.DeliveryManager;
import org.apache.smscserver.DeliveryManagerConfig;
import org.apache.smscserver.SmscServerContext;
import org.apache.smscserver.smsclet.SmscIoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * <p>
 * Manager manage the delivery of short messages to clients
 * 
 * @author hceylan
 */
public class DefaultDeliveryManager implements DeliveryManager {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDeliveryManager.class);

    private static final int DEFAULT_KEEPALIVE_TIME = 30000;

    private final SmscServerContext serverContext;
    private boolean started;
    private boolean suspended;
    private int minThreads;
    private int maxThreads;

    private ThreadPoolExecutor managerExecuter;
    private ThreadPoolExecutor deliveryExecuter;
    private final SynchronousQueue<Runnable> workQueue;

    private BlockingQueue<Runnable> sessionQueue;

    private int managerThreads;

    private long[] deliveryPeriods;

    private int deliveryPollTime;

    public DefaultDeliveryManager(SmscServerContext serverContext) {
        this.serverContext = serverContext;

        this.workQueue = new SynchronousQueue<Runnable>(true);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void closeBoundSession(SmscIoSession ioSession) {
        this.sessionQueue.remove(ioSession);
    }

    private void createManagerThreadPoolExecutor() {
        if (this.managerExecuter == null) {
            DeliveryManagerConfig config = this.serverContext.getDeliveryManagerConfig();

            this.managerThreads = config.getManagerThreads();
            this.minThreads = config.getMinThreads();
            this.maxThreads = config.getMaxThreads();

            if (this.managerThreads < 1) {
                this.managerThreads = Runtime.getRuntime().availableProcessors();
            }

            if (this.minThreads < 1) {
                this.minThreads = Runtime.getRuntime().availableProcessors();
                this.maxThreads = Runtime.getRuntime().availableProcessors();
            }

            if (this.minThreads < 1) {
                this.minThreads = this.maxThreads / 4;
            }

            if (this.maxThreads > this.minThreads) {
                this.maxThreads = this.minThreads;
            }

            DefaultDeliveryManager.LOG.info("Intializing thread pool executor for Delivery Manager", this.minThreads,
                    this.maxThreads);
            this.sessionQueue = new IOSessionQueue();

            this.startManager();
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    public synchronized void destroy() {
        if (this.started) {
            this.managerExecuter.shutdownNow();
            this.managerExecuter = null;
        }
    }

    /**
     * @return the threadPool
     */
    public ThreadPoolExecutor getDeliveryExecuter() {
        if (!this.started || this.suspended) {
            return null;
        }

        if (this.deliveryExecuter == null) {
            this.deliveryExecuter = new ThreadPoolExecutor(this.minThreads, this.maxThreads,
                    DefaultDeliveryManager.DEFAULT_KEEPALIVE_TIME, TimeUnit.MILLISECONDS, this.workQueue);
        }

        return this.deliveryExecuter;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long[] getDeliveryPeriods() {
        return this.deliveryPeriods;
    }

    /**
     * Returns the server context.
     * 
     * @return the serverContext
     */
    public SmscServerContext getServerContext() {
        return this.serverContext;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void newBoundSession(SmscIoSession ioSession) {
        this.sessionQueue.add(new MessagePoller(this, ioSession));
    }

    /**
     * Adds the message poller back to the queue.
     * 
     * @param messagePoller
     *            the message poller to add back to the queue
     */
    public void reschedule(MessagePoller messagePoller) {
        messagePoller.setNextCheckTime(System.currentTimeMillis() + (this.deliveryPollTime * 1000));

        this.sessionQueue.add(messagePoller);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public synchronized void resume() {
        if (!this.started) {
            throw new IllegalStateException("Delivery Manager has not been started");
        }

        if (!this.suspended) {
            this.startManager();

            this.suspended = false;
        }
    }

    /**
     * Starts the delivery manager.
     * 
     * @throws IllegalStateException
     *             if the manager has already been started
     */
    public synchronized void start() {
        if (this.started) {
            throw new IllegalStateException("Delivery Manager already started");
        }

        this.serverContext.getDeliveryManagerConfig();

        this.createManagerThreadPoolExecutor();
        DeliveryManagerConfig config = this.serverContext.getDeliveryManagerConfig();
        this.deliveryPeriods = config.getDeliveryPeriods();
        this.deliveryPollTime = config.getDeliveryPollTime();

        this.started = true;
    }

    private void startManager() {
        this.managerExecuter = new ThreadPoolExecutor(this.managerThreads, this.managerThreads, 1, TimeUnit.HOURS,
                this.sessionQueue);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void suspend() {
        if (!this.started) {
            throw new IllegalStateException("Delivery Manager has not been started");
        }

        if (!this.suspended) {
            this.deliveryExecuter.shutdownNow();
            this.deliveryExecuter = null;

            this.managerExecuter.shutdownNow();
            this.managerExecuter = null;

            this.suspended = true;
        }
    }

}
