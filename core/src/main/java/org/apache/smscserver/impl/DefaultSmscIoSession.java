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

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.swing.filechooser.FileSystemView;

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
public class DefaultSmscIoSession implements SmscIoSession, IoSession {

    public static final String ATTRIBUTE_PREFIX = "org.apache.smscserver.";

    private static final String ATTRIBUTE_SESSION_ID = DefaultSmscIoSession.ATTRIBUTE_PREFIX + "session-id";
    private static final String ATTRIBUTE_USER = DefaultSmscIoSession.ATTRIBUTE_PREFIX + "user";
    private static final String ATTRIBUTE_LANGUAGE = DefaultSmscIoSession.ATTRIBUTE_PREFIX + "language";
    private static final String ATTRIBUTE_LOGIN_TIME = DefaultSmscIoSession.ATTRIBUTE_PREFIX + "login-time";
    private static final String ATTRIBUTE_FAILED_LOGINS = DefaultSmscIoSession.ATTRIBUTE_PREFIX + "failed-logins";
    private static final String ATTRIBUTE_LISTENER = DefaultSmscIoSession.ATTRIBUTE_PREFIX + "listener";
    private static final String ATTRIBUTE_MAX_IDLE_TIME = DefaultSmscIoSession.ATTRIBUTE_PREFIX + "max-idle-time";
    private static final String ATTRIBUTE_LAST_ACCESS_TIME = DefaultSmscIoSession.ATTRIBUTE_PREFIX + "last-access-time";
    private static final String ATTRIBUTE_CACHED_REMOTE_ADDRESS = DefaultSmscIoSession.ATTRIBUTE_PREFIX
            + "cached-remote-address";

    private final IoSession wrappedSession;
    private final SmscServerContext context;

    private boolean consumed = false;

    private SmscRequest request;

    public DefaultSmscIoSession(IoSession wrappedSession, SmscServerContext context) {
        this.wrappedSession = wrappedSession;
        this.context = context;
    }

    /* Begin wrapped IoSession methods */
    /**
     * @see IoSession#close()
     */
    public CloseFuture close() {
        return this.wrappedSession.close();
    }

    /**
     * @see IoSession#close(boolean)
     */
    public CloseFuture close(boolean immediately) {
        return this.wrappedSession.close(immediately);
    }

    /**
     * @see IoSession#containsAttribute(Object)
     */
    public boolean containsAttribute(Object key) {
        return this.wrappedSession.containsAttribute(key);
    }

    /**
     * @see IoSession#getAttachment()
     */
    @SuppressWarnings("deprecation")
    public Object getAttachment() {
        return this.wrappedSession.getAttachment();
    }

    /**
     * @see IoSession#getAttribute(Object)
     */
    public Object getAttribute(Object key) {
        return this.wrappedSession.getAttribute(key);
    }

    /**
     * @see IoSession#getAttribute(Object, Object)
     */
    public Object getAttribute(Object key, Object defaultValue) {
        return this.wrappedSession.getAttribute(key, defaultValue);
    }

    /**
     * @see IoSession#getAttributeKeys()
     */
    public Set<Object> getAttributeKeys() {
        return this.wrappedSession.getAttributeKeys();
    }

    /**
     * @see IoSession#getBothIdleCount()
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
     * @see IoSession#getCloseFuture()
     */
    public CloseFuture getCloseFuture() {
        return this.wrappedSession.getCloseFuture();
    }

    /**
     * @see IoSession#getConfig()
     */
    public IoSessionConfig getConfig() {
        return this.wrappedSession.getConfig();
    }

    /**
     * @see IoSession#getCreationTime()
     */
    public long getCreationTime() {
        return this.wrappedSession.getCreationTime();
    }

    /**
     * @see IoSession#getCurrentWriteMessage()
     */
    public Object getCurrentWriteMessage() {
        return this.wrappedSession.getCurrentWriteMessage();
    }

    /**
     * @see IoSession#getCurrentWriteRequest()
     */
    public WriteRequest getCurrentWriteRequest() {
        return this.wrappedSession.getCurrentWriteRequest();
    }

    public int getFailedLogins() {
        return (Integer) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_FAILED_LOGINS, 0);
    }

    /**
     * @see IoSession#getFilterChain()
     */
    public IoFilterChain getFilterChain() {
        return this.wrappedSession.getFilterChain();
    }

    /**
     * @see IoSession#getHandler()
     */
    public IoHandler getHandler() {
        return this.wrappedSession.getHandler();
    }

    /**
     * @see IoSession#getId()
     */
    public long getId() {
        return this.wrappedSession.getId();
    }

    /**
     * @see IoSession#getIdleCount(IdleStatus)
     */
    public int getIdleCount(IdleStatus status) {
        return this.wrappedSession.getIdleCount(status);
    }

    public Date getLastAccessTime() {
        return (Date) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_LAST_ACCESS_TIME);
    }

    /**
     * @see IoSession#getLastBothIdleTime()
     */
    public long getLastBothIdleTime() {
        return this.wrappedSession.getLastBothIdleTime();
    }

    /**
     * @see IoSession#getLastIdleTime(IdleStatus)
     */
    public long getLastIdleTime(IdleStatus status) {
        return this.wrappedSession.getLastIdleTime(status);
    }

    /**
     * @see IoSession#getLastIoTime()
     */
    public long getLastIoTime() {
        return this.wrappedSession.getLastIoTime();
    }

    /**
     * @see IoSession#getLastReaderIdleTime()
     */
    public long getLastReaderIdleTime() {
        return this.wrappedSession.getLastReaderIdleTime();
    }

    /**
     * @see IoSession#getLastReadTime()
     */
    public long getLastReadTime() {
        return this.wrappedSession.getLastReadTime();
    }

    /**
     * @see IoSession#getLastWriterIdleTime()
     */
    public long getLastWriterIdleTime() {
        return this.wrappedSession.getLastWriterIdleTime();
    }

    /**
     * @see IoSession#getLastWriteTime()
     */
    public long getLastWriteTime() {
        return this.wrappedSession.getLastWriteTime();
    }

    public Listener getListener() {
        return (Listener) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_LISTENER);
    }

    /**
     * @see IoSession#getLocalAddress()
     */
    public SocketAddress getLocalAddress() {
        return this.wrappedSession.getLocalAddress();
    }

    public Date getLoginTime() {
        return (Date) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_LOGIN_TIME);
    }

    public int getMaxIdleTime() {
        return (Integer) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_MAX_IDLE_TIME, 0);
    }

    /**
     * @see IoSession#getReadBytes()
     */
    public long getReadBytes() {
        return this.wrappedSession.getReadBytes();
    }

    /**
     * @see IoSession#getReadBytesThroughput()
     */
    public double getReadBytesThroughput() {
        return this.wrappedSession.getReadBytesThroughput();
    }

    /**
     * @see IoSession#getReaderIdleCount()
     */
    public int getReaderIdleCount() {
        return this.wrappedSession.getReaderIdleCount();
    }

    /**
     * @see IoSession#getReadMessages()
     */
    public long getReadMessages() {
        return this.wrappedSession.getReadMessages();
    }

    /**
     * @see IoSession#getReadMessagesThroughput()
     */
    public double getReadMessagesThroughput() {
        return this.wrappedSession.getReadMessagesThroughput();
    }

    /**
     * @see IoSession#getRemoteAddress()
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
     * @see IoSession#getScheduledWriteBytes()
     */
    public long getScheduledWriteBytes() {
        return this.wrappedSession.getScheduledWriteBytes();
    }

    /**
     * @see IoSession#getScheduledWriteMessages()
     */
    public int getScheduledWriteMessages() {
        return this.wrappedSession.getScheduledWriteMessages();
    }

    /**
     * @see IoSession#getService()
     */
    public IoService getService() {
        return this.wrappedSession.getService();
    }

    /**
     * @see IoSession#getServiceAddress()
     */
    public SocketAddress getServiceAddress() {
        return this.wrappedSession.getServiceAddress();
    }

    /**
     * @see SmscSession#getSessionId()
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
     * @see IoSession#getTransportMetadata()
     */
    public TransportMetadata getTransportMetadata() {
        return this.wrappedSession.getTransportMetadata();
    }

    public User getUser() {
        return (User) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_USER);
    }

    /**
     * @see IoSession#getWriteRequestQueue()
     */
    public WriteRequestQueue getWriteRequestQueue() {
        return this.wrappedSession.getWriteRequestQueue();
    }

    /**
     * @see IoSession#getWriterIdleCount()
     */
    public int getWriterIdleCount() {
        return this.wrappedSession.getWriterIdleCount();
    }

    /**
     * @see IoSession#getWrittenBytes()
     */
    public long getWrittenBytes() {
        return this.wrappedSession.getWrittenBytes();
    }

    /**
     * @see IoSession#getWrittenBytesThroughput()
     */
    public double getWrittenBytesThroughput() {
        return this.wrappedSession.getWrittenBytesThroughput();
    }

    /**
     * @see IoSession#getWrittenMessages()
     */
    public long getWrittenMessages() {
        return this.wrappedSession.getWrittenMessages();
    }

    /**
     * @see IoSession#getWrittenMessagesThroughput()
     */
    public double getWrittenMessagesThroughput() {
        return this.wrappedSession.getWrittenMessagesThroughput();
    }

    public synchronized void increaseFailedLogins() {
        int failedLogins = (Integer) this.getAttribute(DefaultSmscIoSession.ATTRIBUTE_FAILED_LOGINS, 0);
        failedLogins++;
        this.setAttribute(DefaultSmscIoSession.ATTRIBUTE_FAILED_LOGINS, failedLogins);
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
     * @see IoSession#isBothIdle()
     */
    public boolean isBothIdle() {
        return this.wrappedSession.isBothIdle();
    }

    /**
     * @see IoSession#isClosing()
     */
    public boolean isClosing() {
        return this.wrappedSession.isClosing();
    }

    /**
     * @see IoSession#isConnected()
     */
    public boolean isConnected() {
        return this.wrappedSession.isConnected();
    }

    /**
     * @see IoSession#isIdle(IdleStatus)
     */
    public boolean isIdle(IdleStatus status) {
        return this.wrappedSession.isIdle(status);
    }

    /**
     * Is logged-in
     */
    public boolean isLoggedIn() {
        return this.containsAttribute(DefaultSmscIoSession.ATTRIBUTE_USER);
    }

    /**
     * @see IoSession#isReaderIdle()
     */
    public boolean isReaderIdle() {
        return this.wrappedSession.isReaderIdle();
    }

    /**
     * @see IoSession#isReadSuspended()
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
     * @see IoSession#isWriterIdle()
     */
    public boolean isWriterIdle() {
        return this.wrappedSession.isWriterIdle();
    }

    /**
     * @see IoSession#isWriteSuspended()
     */
    public boolean isWriteSuspended() {
        return this.wrappedSession.isWriteSuspended();
    }

    /**
     * @see IoSession#read()
     */
    public ReadFuture read() {
        return this.wrappedSession.read();
    }

    public void reinitialize() {
        this.unbindUser();
        this.removeAttribute(DefaultSmscIoSession.ATTRIBUTE_USER);
        this.removeAttribute(DefaultSmscIoSession.ATTRIBUTE_LOGIN_TIME);
    }

    /**
     * @see IoSession#removeAttribute(Object)
     */
    public Object removeAttribute(Object key) {
        return this.wrappedSession.removeAttribute(key);
    }

    /**
     * @see IoSession#removeAttribute(Object, Object)
     */
    public boolean removeAttribute(Object key, Object value) {
        return this.wrappedSession.removeAttribute(key, value);
    }

    /**
     * @see IoSession#replaceAttribute(Object, Object, Object)
     */
    public boolean replaceAttribute(Object key, Object oldValue, Object newValue) {
        return this.wrappedSession.replaceAttribute(key, oldValue, newValue);
    }

    /**
     * @see IoSession#resumeRead()
     */
    public void resumeRead() {
        this.wrappedSession.resumeRead();
    }

    /**
     * @see IoSession#resumeWrite()
     */
    public void resumeWrite() {
        this.wrappedSession.resumeWrite();
    }

    /**
     * @see IoSession#setAttachment(Object)
     */
    @SuppressWarnings("deprecation")
    public Object setAttachment(Object attachment) {
        return this.wrappedSession.setAttachment(attachment);
    }

    /**
     * @see IoSession#setAttribute(Object)
     */
    public Object setAttribute(Object key) {
        return this.wrappedSession.setAttribute(key);
    }

    /**
     * @see IoSession#setAttribute(Object, Object)
     */
    public Object setAttribute(Object key, Object value) {
        return this.wrappedSession.setAttribute(key, value);
    }

    /**
     * @see IoSession#setAttributeIfAbsent(Object)
     */
    public Object setAttributeIfAbsent(Object key) {
        return this.wrappedSession.setAttributeIfAbsent(key);
    }

    /**
     * @see IoSession#setAttributeIfAbsent(Object, Object)
     */
    public Object setAttributeIfAbsent(Object key, Object value) {
        return this.wrappedSession.setAttributeIfAbsent(key, value);
    }

    /**
     * @see IoSession#setCurrentWriteRequest(WriteRequest)
     */
    public void setCurrentWriteRequest(WriteRequest currentWriteRequest) {
        this.wrappedSession.setCurrentWriteRequest(currentWriteRequest);
    }

    public void setLanguage(String language) {
        this.setAttribute(DefaultSmscIoSession.ATTRIBUTE_LANGUAGE, language);

    }

    public void setListener(Listener listener) {
        this.setAttribute(DefaultSmscIoSession.ATTRIBUTE_LISTENER, listener);
    }

    public void setLogin(FileSystemView fsview) {
        this.setAttribute(DefaultSmscIoSession.ATTRIBUTE_LOGIN_TIME, new Date());
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

    public void setUser(User user) {
        DefaultSmscStatistics statistics = (DefaultSmscStatistics) this.context.getSmscStatistics();
        if (user != null) {
            this.setAttribute(DefaultSmscIoSession.ATTRIBUTE_USER, user);
            statistics.setBind(this);
        } else {
            statistics.setUnbind(this);
            this.setAttribute(DefaultSmscIoSession.ATTRIBUTE_USER, user);
        }
    }

    /**
     * @see IoSession#suspendRead()
     */
    public void suspendRead() {
        this.wrappedSession.suspendRead();
    }

    /**
     * @see IoSession#suspendWrite()
     */
    public void suspendWrite() {
        this.wrappedSession.suspendWrite();
    }

    public void unbindUser() {
        if (((ServerSmscStatistics) this.context.getSmscStatistics()) != null) {
            ((ServerSmscStatistics) this.context.getSmscStatistics()).setUnbind(this);

            LoggerFactory.getLogger(this.getClass()).debug("Statistics unbind decreased due to user unbind");
        } else {
            LoggerFactory.getLogger(this.getClass()).warn(
                    "Statistics not available in session, can not decrease unbind count");
        }
    }

    public void updateLastAccessTime() {
        this.setAttribute(DefaultSmscIoSession.ATTRIBUTE_LAST_ACCESS_TIME, new Date());

    }

    /**
     * @see IoSession#updateThroughput(long, boolean)
     */
    public void updateThroughput(long currentTime, boolean force) {
        this.wrappedSession.updateThroughput(currentTime, force);
    }

    /**
     * @see IoSession#write(Object)
     */
    public WriteFuture write(Object message) {
        if (this.request == null) {
            throw new SmscRuntimeException("Illegal write from unbound SmscIOSession " + this.wrappedSession.getId());
        }

        if (this.consumed) {
            throw new SmscRuntimeException("SmscIOSession " + this.wrappedSession.getId()
                    + " already consumed the request " + this.request.getId());
        }

        WriteFuture future = this.wrappedSession.write(message);
        this.consumed = true;

        return future;
    }

    /**
     * @see IoSession#write(Object, SocketAddress)
     */
    public WriteFuture write(Object message, SocketAddress destination) {
        if (this.request == null) {
            throw new SmscRuntimeException("Illegal write from unbound SmscIOSession " + this.wrappedSession.getId());
        }

        if (this.consumed) {
            throw new SmscRuntimeException("SmscIOSession " + this.wrappedSession.getId()
                    + " already consumed the request " + this.request.getId());
        }

        WriteFuture future = this.wrappedSession.write(message, destination);
        this.consumed = true;

        return future;
    }
}
