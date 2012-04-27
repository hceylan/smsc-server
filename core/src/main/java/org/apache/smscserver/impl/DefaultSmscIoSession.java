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

import java.net.SocketAddress;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.smscserver.ServerSmscStatistics;
import org.apache.smscserver.SmscServerContext;
import org.apache.smscserver.listener.Listener;
import org.apache.smscserver.smsclet.SmscIoSession;
import org.apache.smscserver.smsclet.SmscRequest;
import org.apache.smscserver.smsclet.SmscRuntimeException;
import org.apache.smscserver.smsclet.SmscSession;
import org.apache.smscserver.smsclet.User;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 * 
 */
public class DefaultSmscIoSession implements SmscIoSession {

    private static final String ATTRIBUTE_SESSION_ID = SmscIoSession.ATTRIBUTE_PREFIX + "session-id";
    private static final String ATTRIBUTE_USER = SmscIoSession.ATTRIBUTE_PREFIX + "user";
    private static final String ATTRIBUTE_BIND_TIME = SmscIoSession.ATTRIBUTE_PREFIX + "bind-time";
    private static final String ATTRIBUTE_FAILED_BINDS = SmscIoSession.ATTRIBUTE_PREFIX + "failed-binds";
    private static final String ATTRIBUTE_LISTENER = SmscIoSession.ATTRIBUTE_PREFIX + "listener";
    private static final String ATTRIBUTE_MAX_IDLE_TIME = SmscIoSession.ATTRIBUTE_PREFIX + "max-idle-time";
    private static final String ATTRIBUTE_LAST_ACCESS_TIME = SmscIoSession.ATTRIBUTE_PREFIX + "last-access-time";
    private static final String ATTRIBUTE_CACHED_REMOTE_ADDRESS = SmscIoSession.ATTRIBUTE_PREFIX
            + "cached-remote-address";

    private final IoSession wrappedSession;
    private final SmscServerContext serverContext;
    private SmscRequest request;
    private final ReentrantLock lock;

    private final AtomicInteger sequenceNumber = new AtomicInteger();

    public DefaultSmscIoSession(IoSession wrappedSession, SmscServerContext context) {
        this.wrappedSession = wrappedSession;
        this.serverContext = context;

        this.lock = new ReentrantLock();
    }

    public void clearUser() {
        DefaultSmscStatistics statistics = (DefaultSmscStatistics) this.serverContext.getSmscStatistics();
        statistics.setUnbind(this);
        this.setAttribute(DefaultSmscIoSession.ATTRIBUTE_USER, null);
        this.serverContext.getDeliveryManager().closeBoundSession(this);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public CloseFuture close() {
        return this.wrappedSession.close();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public CloseFuture close(boolean immediately) {
        return this.wrappedSession.close(immediately);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean containsAttribute(Object key) {
        return this.wrappedSession.containsAttribute(key);
    }

    /**
     * {@inheritDoc}
     * 
     */
    @SuppressWarnings("deprecation")
    public Object getAttachment() {
        return this.wrappedSession.getAttachment();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Object getAttribute(Object key) {
        return this.wrappedSession.getAttribute(key);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Object getAttribute(Object key, Object defaultValue) {
        return this.wrappedSession.getAttribute(key, defaultValue);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Set<Object> getAttributeKeys() {
        return this.wrappedSession.getAttributeKeys();
    }

    public Date getBindTime() {
        return (Date) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_BIND_TIME);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getBothIdleCount() {
        return this.wrappedSession.getBothIdleCount();
    }

    public Certificate[] getClientCertificates() {
        if (this.getFilterChain().contains(SslFilter.class)) {
            SslFilter sslFilter = (SslFilter) this.getFilterChain().get(SslFilter.class);

            SSLSession sslSession = sslFilter.getSslSession(this);

            if (sslSession != null) {
                try {
                    return sslSession.getPeerCertificates();
                } catch (SSLPeerUnverifiedException e) {
                    // ignore, certificate will not be available to the session
                }
            }

        }

        // no certificates available
        return null;

    }

    /**
     * {@inheritDoc}
     * 
     */
    public CloseFuture getCloseFuture() {
        return this.wrappedSession.getCloseFuture();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public IoSessionConfig getConfig() {
        return this.wrappedSession.getConfig();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long getCreationTime() {
        return this.wrappedSession.getCreationTime();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Object getCurrentWriteMessage() {
        return this.wrappedSession.getCurrentWriteMessage();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public WriteRequest getCurrentWriteRequest() {
        return this.wrappedSession.getCurrentWriteRequest();
    }

    public int getFailedBinds() {
        return (Integer) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_FAILED_BINDS, 0);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public IoFilterChain getFilterChain() {
        return this.wrappedSession.getFilterChain();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public IoHandler getHandler() {
        return this.wrappedSession.getHandler();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long getId() {
        return this.wrappedSession.getId();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getIdleCount(IdleStatus status) {
        return this.wrappedSession.getIdleCount(status);
    }

    public Date getLastAccessTime() {
        return (Date) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_LAST_ACCESS_TIME);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long getLastBothIdleTime() {
        return this.wrappedSession.getLastBothIdleTime();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long getLastIdleTime(IdleStatus status) {
        return this.wrappedSession.getLastIdleTime(status);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long getLastIoTime() {
        return this.wrappedSession.getLastIoTime();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long getLastReaderIdleTime() {
        return this.wrappedSession.getLastReaderIdleTime();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long getLastReadTime() {
        return this.wrappedSession.getLastReadTime();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long getLastWriterIdleTime() {
        return this.wrappedSession.getLastWriterIdleTime();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long getLastWriteTime() {
        return this.wrappedSession.getLastWriteTime();
    }

    public Listener getListener() {
        return (Listener) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_LISTENER);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public SocketAddress getLocalAddress() {
        return this.wrappedSession.getLocalAddress();
    }

    public int getMaxIdleTime() {
        return (Integer) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_MAX_IDLE_TIME, 0);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getNextSequnce() {
        return this.sequenceNumber.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long getReadBytes() {
        return this.wrappedSession.getReadBytes();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public double getReadBytesThroughput() {
        return this.wrappedSession.getReadBytesThroughput();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getReaderIdleCount() {
        return this.wrappedSession.getReaderIdleCount();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long getReadMessages() {
        return this.wrappedSession.getReadMessages();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public double getReadMessagesThroughput() {
        return this.wrappedSession.getReadMessagesThroughput();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public SocketAddress getRemoteAddress() {
        // when closing a socket, the remote address might be reset to null
        // therefore, we attempt to keep a cached copy around

        SocketAddress address = this.wrappedSession.getRemoteAddress();
        if ((address == null) && this.containsAttribute(DefaultSmscIoSession.ATTRIBUTE_CACHED_REMOTE_ADDRESS)) {
            return (SocketAddress) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_CACHED_REMOTE_ADDRESS);
        } else {
            this.setAttribute(DefaultSmscIoSession.ATTRIBUTE_CACHED_REMOTE_ADDRESS, address);
            return address;
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long getScheduledWriteBytes() {
        return this.wrappedSession.getScheduledWriteBytes();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getScheduledWriteMessages() {
        return this.wrappedSession.getScheduledWriteMessages();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public IoService getService() {
        return this.wrappedSession.getService();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public SocketAddress getServiceAddress() {
        return this.wrappedSession.getServiceAddress();
    }

    /**
     * @return
     */
    public UUID getSessionId() {
        synchronized (this.wrappedSession) {
            if (!this.wrappedSession.containsAttribute(DefaultSmscIoSession.ATTRIBUTE_SESSION_ID)) {
                this.wrappedSession.setAttribute(DefaultSmscIoSession.ATTRIBUTE_SESSION_ID, UUID.randomUUID());
            }
            return (UUID) this.wrappedSession.getAttribute(DefaultSmscIoSession.ATTRIBUTE_SESSION_ID);
        }
    }

    public SmscSession getSmscletSession() {
        return new DefaultSmscSession(this);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public TransportMetadata getTransportMetadata() {
        return this.wrappedSession.getTransportMetadata();
    }

    public User getUser() {
        return (User) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_USER);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public WriteRequestQueue getWriteRequestQueue() {
        return this.wrappedSession.getWriteRequestQueue();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getWriterIdleCount() {
        return this.wrappedSession.getWriterIdleCount();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long getWrittenBytes() {
        return this.wrappedSession.getWrittenBytes();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public double getWrittenBytesThroughput() {
        return this.wrappedSession.getWrittenBytesThroughput();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public long getWrittenMessages() {
        return this.wrappedSession.getWrittenMessages();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public double getWrittenMessagesThroughput() {
        return this.wrappedSession.getWrittenMessagesThroughput();
    }

    public synchronized void increaseFailedBinds() {
        int failedBinds = (Integer) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_FAILED_BINDS, 0);
        failedBinds++;
        this.setAttribute(DefaultSmscIoSession.ATTRIBUTE_FAILED_BINDS, failedBinds);
    }

    /**
     * Increase the number of bytes read on the data connection
     * 
     * @param increment
     *            The number of bytes written
     */
    public void increaseReadDataBytes(int increment) {
        if (this.wrappedSession instanceof AbstractIoSession) {
            ((AbstractIoSession) this.wrappedSession).increaseReadBytes(increment, System.currentTimeMillis());
        }
    }

    /**
     * Increase the number of bytes written on the data connection
     * 
     * @param increment
     *            The number of bytes written
     */
    public void increaseWrittenDataBytes(int increment) {
        if (this.wrappedSession instanceof AbstractIoSession) {
            ((AbstractIoSession) this.wrappedSession).increaseScheduledWriteBytes(increment);
            ((AbstractIoSession) this.wrappedSession).increaseWrittenBytes(increment, System.currentTimeMillis());
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean isBothIdle() {
        return this.wrappedSession.isBothIdle();
    }

    /**
     * Is bound
     */
    public boolean isBound() {
        return this.containsAttribute(DefaultSmscIoSession.ATTRIBUTE_USER);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean isClosing() {
        return this.wrappedSession.isClosing();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean isConnected() {
        return this.wrappedSession.isConnected();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean isIdle(IdleStatus status) {
        return this.wrappedSession.isIdle(status);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean isReaderIdle() {
        return this.wrappedSession.isReaderIdle();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean isReadSuspended() {
        return this.wrappedSession.isReadSuspended();
    }

    /**
     * Indicates whether the control socket for this session is secure, that is, running over SSL/TLS
     * 
     * @return true if the control socket is secured
     */
    public boolean isSecure() {
        return this.getFilterChain().contains(SslFilter.class);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean isWriterIdle() {
        return this.wrappedSession.isWriterIdle();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean isWriteSuspended() {
        return this.wrappedSession.isWriteSuspended();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public synchronized boolean lock() {
        long timeout = this.serverContext.getSessionLockTimeout();

        try {
            return this.lock.tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new SmscRuntimeException("Unable to acquire lock");
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    public ReadFuture read() {
        return this.wrappedSession.read();
    }

    public void reinitialize() {
        this.unbindUser();
        this.removeAttribute(DefaultSmscIoSession.ATTRIBUTE_USER);
        this.removeAttribute(DefaultSmscIoSession.ATTRIBUTE_BIND_TIME);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Object removeAttribute(Object key) {
        return this.wrappedSession.removeAttribute(key);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean removeAttribute(Object key, Object value) {
        return this.wrappedSession.removeAttribute(key, value);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean replaceAttribute(Object key, Object oldValue, Object newValue) {
        return this.wrappedSession.replaceAttribute(key, oldValue, newValue);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void resumeRead() {
        this.wrappedSession.resumeRead();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void resumeWrite() {
        this.wrappedSession.resumeWrite();
    }

    /**
     * {@inheritDoc}
     * 
     */
    @SuppressWarnings("deprecation")
    public Object setAttachment(Object attachment) {
        return this.wrappedSession.setAttachment(attachment);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Object setAttribute(Object key) {
        return this.wrappedSession.setAttribute(key);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Object setAttribute(Object key, Object value) {
        return this.wrappedSession.setAttribute(key, value);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Object setAttributeIfAbsent(Object key) {
        return this.wrappedSession.setAttributeIfAbsent(key);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Object setAttributeIfAbsent(Object key, Object value) {
        return this.wrappedSession.setAttributeIfAbsent(key, value);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void setCurrentWriteRequest(WriteRequest currentWriteRequest) {
        this.wrappedSession.setCurrentWriteRequest(currentWriteRequest);
    }

    public void setListener(Listener listener) {
        this.setAttribute(DefaultSmscIoSession.ATTRIBUTE_LISTENER, listener);
    }

    public void setMaxIdleTime(int maxIdleTime) {
        this.setAttribute(DefaultSmscIoSession.ATTRIBUTE_MAX_IDLE_TIME, maxIdleTime);

        int listenerTimeout = this.getListener().getIdleTimeout();

        // the listener timeout should be the upper limit, unless set to unlimited
        // if the user limit is set to be unlimited, use the listener value is the threshold
        // (already used as the default for all sessions)
        // else, if the user limit is less than the listener idle time, use the user limit
        if ((listenerTimeout <= 0) || ((maxIdleTime > 0) && (maxIdleTime < listenerTimeout))) {
            this.wrappedSession.getConfig().setBothIdleTime(maxIdleTime);
        }
    }

    public void setRequest(SmscRequest request) {
        if (this.request != null) {
            throw new SmscRuntimeException("SmscIOSession " + this.wrappedSession.getId()
                    + " already bound to the request " + this.request.getId());
        }

        this.request = request;
    }

    public void setUser(User user, boolean receiver) {
        DefaultSmscStatistics statistics = (DefaultSmscStatistics) this.serverContext.getSmscStatistics();
        this.setAttribute(DefaultSmscIoSession.ATTRIBUTE_USER, user);
        statistics.setBind(this);

        if (receiver) {
            this.serverContext.getDeliveryManager().newBoundSession(this);
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void startDelivery() {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void stopDelivery() {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void suspendRead() {
        this.wrappedSession.suspendRead();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void suspendWrite() {
        this.wrappedSession.suspendWrite();
    }

    public void unbindUser() {
        if (((ServerSmscStatistics) this.serverContext.getSmscStatistics()) != null) {
            ((ServerSmscStatistics) this.serverContext.getSmscStatistics()).setUnbind(this);

            LoggerFactory.getLogger(this.getClass()).debug("Statistics unbind decreased due to user unbind");
        } else {
            LoggerFactory.getLogger(this.getClass()).warn(
                    "Statistics not available in session, can not decrease unbind count");
        }
    }

    public synchronized void unlock() {
        this.lock.unlock();
    }

    public void updateLastAccessTime() {
        this.setAttribute(DefaultSmscIoSession.ATTRIBUTE_LAST_ACCESS_TIME, new Date());

    }

    /**
     * {@inheritDoc}
     * 
     */
    public void updateThroughput(long currentTime, boolean force) {
        this.wrappedSession.updateThroughput(currentTime, force);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public WriteFuture write(Object message) {
        if (this.request == null) {
            throw new SmscRuntimeException("Illegal write from unbound SmscIOSession " + this.wrappedSession.getId());
        }

        return this.wrappedSession.write(message);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public WriteFuture write(Object message, SocketAddress destination) {
        if (this.request == null) {
            throw new SmscRuntimeException("Illegal write from unbound SmscIOSession " + this.wrappedSession.getId());
        }

        return this.wrappedSession.write(message, destination);
    }
}
