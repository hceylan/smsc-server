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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.smscserver.MessageObserver;
import org.apache.smscserver.ServerSmscStatistics;
import org.apache.smscserver.StatisticsObserver;
import org.apache.smscserver.smsclet.SmscIoSession;
import org.apache.smscserver.smsclet.SmscReply;
import org.apache.smscserver.smsclet.SmscRequest;
import org.apache.smscserver.smsclet.User;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * This is SMSC statistics implementation.
 * 
 * TODO revisit concurrency, right now we're a bit over zealous with both Atomic* counters and synchronization
 * 
 * @author hceylan
 */
public class DefaultSmscStatistics implements ServerSmscStatistics {

    private static class UserBindss {
        private final Map<InetAddress, AtomicInteger> perAddress = new ConcurrentHashMap<InetAddress, AtomicInteger>();

        public AtomicInteger totalBinds;

        public UserBindss(InetAddress address) {
            // init with the first connection
            this.totalBinds = new AtomicInteger(1);
            this.perAddress.put(address, new AtomicInteger(1));
        }

        public AtomicInteger bindsFromInetAddress(InetAddress address) {
            AtomicInteger binds = this.perAddress.get(address);
            if (binds == null) {
                binds = new AtomicInteger(0);
                this.perAddress.put(address, binds);
            }
            return binds;
        }
    }

    private StatisticsObserver observer = null;

    private MessageObserver messageObserver = null;

    private Date startTime = new Date();

    private final AtomicInteger messageReceivedCount = new AtomicInteger(0);

    private final AtomicInteger messageSentCount = new AtomicInteger(0);

    private final AtomicInteger currBinds = new AtomicInteger(0);

    private final AtomicInteger totalBinds = new AtomicInteger(0);

    private final AtomicInteger totalFailedBinds = new AtomicInteger(0);

    private final AtomicInteger currConnections = new AtomicInteger(0);

    private final AtomicInteger totalConnections = new AtomicInteger(0);

    /**
     * The user bind information.
     */
    private final Map<String, UserBindss> userBindTable = new ConcurrentHashMap<String, UserBindss>();

    public static final String BIND_NUMBER = "bind_number";

    /**
     * Get current number of binds.
     */
    public int getCurrentBindNumber() {
        return this.currBinds.get();
    }

    /**
     * Get current number of connections.
     */
    public int getCurrentConnectionNumber() {
        return this.currConnections.get();
    }

    /**
     * Get the bind number for the specific user
     */
    public synchronized int getCurrentUserBindNumber(final User user) {
        if (this.userBindTable.get(user.getName()) == null) {// not found the bind user's statistics info
            return 0;
        } else {
            return this.userBindTable.get(user.getName()).totalBinds.get();
        }
    }

    /**
     * Get the bind number for the specific user from the ipAddress
     * 
     * @param user
     *            bind user account
     * @param ipAddress
     *            the ip address of the remote user
     */
    public synchronized int getCurrentUserBindNumber(final User user, final InetAddress ipAddress) {
        if (this.userBindTable.get(user.getName()) == null) {// not found the bind user's statistics info
            return 0;
        } else {
            return this.userBindTable.get(user.getName()).bindsFromInetAddress(ipAddress).get();
        }
    }

    /**
     * Get server start time.
     */
    public Date getStartTime() {
        if (this.startTime != null) {
            return (Date) this.startTime.clone();
        } else {
            return null;
        }
    }

    /**
     * Get total number of binds.
     */
    public int getTotalBindNumber() {
        return this.totalBinds.get();
    }

    /**
     * Get total number of connections.
     */
    public int getTotalConnectionNumber() {
        return this.totalConnections.get();
    }

    /**
     * Get total failed bind number.
     */
    public int getTotalFailedBindNumber() {
        return this.totalFailedBinds.get();
    }

    /**
     * Get number of messages received.
     */
    public int getTotalMessageReceivedNumber() {
        return this.messageReceivedCount.get();
    }

    /**
     * Get number of messages sent.
     */
    public int getTotalMessageSentNumber() {
        return this.messageReceivedCount.get();
    }

    /**
     * Observer bind notification.
     */
    private void notifyBind(final SmscIoSession session) {
        if (this.observer != null) {
            this.observer.notifyBind();
        }
    }

    /**
     * Observer failed bind notification.
     */
    private void notifyBindFail(final SmscIoSession session) {
        StatisticsObserver observer = this.observer;
        if (observer != null) {
            if (session.getRemoteAddress() instanceof InetSocketAddress) {
                observer.notifyBindFail(((InetSocketAddress) session.getRemoteAddress()).getAddress());

            }
        }
    }

    /**
     * Observer close connection notification.
     */
    private void notifyCloseConnection(final SmscIoSession session) {
        if (this.observer != null) {
            this.observer.notifyCloseConnection();
        }
    }

    /**
     * Observer message received notification.
     */
    private void notifyMessageReceived(final SmscIoSession session, final SmscRequest request) {
        if (this.observer != null) {
            this.observer.notifyMessageReceived();
        }

        if (this.messageObserver != null) {
            this.messageObserver.notifyMessageReceived(session, request);
        }
    }

    /**
     * Observer message sent notification.
     */
    private void notifyMessageSent(final SmscIoSession session, final SmscReply reply) {
        if (this.observer != null) {
            this.observer.notifyMessageSent();
        }

        if (this.messageObserver != null) {
            this.messageObserver.notifyMessageSent(session, reply);
        }
    }

    /**
     * Observer open connection notification.
     */
    private void notifyOpenConnection(final SmscIoSession session) {
        if (this.observer != null) {
            this.observer.notifyOpenConnection();
        }
    }

    /**
     * Observer unbind notification.
     */
    private void notifyUnbind(final SmscIoSession session) {
        if (this.observer != null) {
            this.observer.notifyUnbind();
        }
    }

    /**
     * Reset the cumulative counters.
     */
    public synchronized void resetStatisticsCounters() {
        this.startTime = new Date();

        this.messageReceivedCount.set(0);
        this.messageSentCount.set(0);

        this.totalBinds.set(0);
        this.totalFailedBinds.set(0);
        this.totalConnections.set(0);
    }

    /**
     * New bind.
     */
    public synchronized void setBind(final SmscIoSession session) {
        this.currBinds.incrementAndGet();
        this.totalBinds.incrementAndGet();
        User user = session.getUser();

        synchronized (user) {// thread safety is needed. Since the bind occurs
            // at low frequency, this overhead is endurable
            UserBindss statisticsTable = this.userBindTable.get(user.getName());
            if (statisticsTable == null) {
                // the hash table that records the bind information of the user
                // and its ip address.

                InetAddress address = null;
                if (session.getRemoteAddress() instanceof InetSocketAddress) {
                    address = ((InetSocketAddress) session.getRemoteAddress()).getAddress();
                }
                statisticsTable = new UserBindss(address);
                this.userBindTable.put(user.getName(), statisticsTable);
            } else {
                statisticsTable.totalBinds.incrementAndGet();

                if (session.getRemoteAddress() instanceof InetSocketAddress) {
                    InetAddress address = ((InetSocketAddress) session.getRemoteAddress()).getAddress();
                    statisticsTable.bindsFromInetAddress(address).incrementAndGet();
                }

            }
        }

        this.notifyBind(session);
    }

    /**
     * Increment failed bind count.
     */
    public synchronized void setBindFail(final SmscIoSession session) {
        this.totalFailedBinds.incrementAndGet();
        this.notifyBindFail(session);
    }

    /**
     * Decrement open connection count.
     */
    public synchronized void setCloseConnection(final SmscIoSession session) {
        if (this.currConnections.get() > 0) {
            this.currConnections.decrementAndGet();
        }
        this.notifyCloseConnection(session);
    }

    /**
     * Set the message observer.
     */
    public void setMessageObserver(final MessageObserver observer) {
        this.messageObserver = observer;
    }

    /**
     * Increment message received count.
     */
    public synchronized void setMessageReceived(final SmscIoSession session, final SmscRequest request) {
        this.messageReceivedCount.incrementAndGet();
        this.notifyMessageReceived(session, request);
    }

    /**
     * Increment download count.
     */
    public synchronized void setMessageSent(final SmscIoSession session, final SmscReply reply) {
        this.messageSentCount.incrementAndGet();
        this.notifyMessageSent(session, reply);
    }

    /**
     * Set the observer.
     */
    public void setObserver(final StatisticsObserver observer) {
        this.observer = observer;
    }

    /**
     * Increment open connection count.
     */
    public synchronized void setOpenConnection(final SmscIoSession session) {
        this.currConnections.incrementAndGet();
        this.totalConnections.incrementAndGet();
        this.notifyOpenConnection(session);
    }

    /**
     * User unbind
     */
    public synchronized void setUnbind(final SmscIoSession session) {
        User user = session.getUser();
        if (user == null) {
            return;
        }

        this.currBinds.decrementAndGet();

        synchronized (user) {
            UserBindss userBinds = this.userBindTable.get(user.getName());

            if (userBinds != null) {
                userBinds.totalBinds.decrementAndGet();
                if (session.getRemoteAddress() instanceof InetSocketAddress) {
                    InetAddress address = ((InetSocketAddress) session.getRemoteAddress()).getAddress();
                    userBinds.bindsFromInetAddress(address).decrementAndGet();
                }
            }

        }

        this.notifyUnbind(session);
    }

}
