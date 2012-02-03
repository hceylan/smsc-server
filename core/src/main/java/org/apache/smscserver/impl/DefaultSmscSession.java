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

import java.net.InetSocketAddress;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.UUID;

import org.apache.smscserver.smsclet.SmscException;
import org.apache.smscserver.smsclet.SmscIoSession;
import org.apache.smscserver.smsclet.SmscReply;
import org.apache.smscserver.smsclet.SmscSession;
import org.apache.smscserver.smsclet.User;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * SMSC session
 * 
 * @author hceylan
 */
public class DefaultSmscSession implements SmscSession {

    private final DefaultSmscIoSession ioSession;

    /**
     * Default constructor.
     */
    public DefaultSmscSession(final DefaultSmscIoSession ioSession) {
        this.ioSession = ioSession;
    }

    /**
     * Get attribute
     */
    public Object getAttribute(final String name) {
        if (name.startsWith(SmscIoSession.ATTRIBUTE_PREFIX)) {
            throw new IllegalArgumentException("Illegal lookup of internal attribute");
        }

        return this.ioSession.getAttribute(name);
    }

    /**
     * Get the bind time.
     */
    public Date getBindTime() {
        return this.ioSession.getBindTime();
    }

    /**
     * Get remote address
     */
    public InetSocketAddress getClientAddress() {
        if (this.ioSession.getRemoteAddress() instanceof InetSocketAddress) {
            return ((InetSocketAddress) this.ioSession.getRemoteAddress());
        } else {
            return null;
        }
    }

    public Certificate[] getClientCertificates() {
        return this.ioSession.getClientCertificates();
    }

    /**
     * Get connection time.
     */
    public Date getConnectionTime() {
        return new Date(this.ioSession.getCreationTime());
    }

    public int getFailedBinds() {
        return this.ioSession.getFailedBinds();
    }

    /**
     * Get last access time.
     */
    public Date getLastAccessTime() {
        return this.ioSession.getLastAccessTime();
    }

    public int getMaxIdleTime() {
        return this.ioSession.getMaxIdleTime();
    }

    public InetSocketAddress getServerAddress() {
        if (this.ioSession.getLocalAddress() instanceof InetSocketAddress) {
            return ((InetSocketAddress) this.ioSession.getLocalAddress());
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public UUID getSessionId() {
        return this.ioSession.getSessionId();
    }

    /**
     * Get user.
     */
    public User getUser() {
        return this.ioSession.getUser();
    }

    /**
     * Increase the number of bytes read on the data connection
     * 
     * @param increment
     *            The number of bytes written
     */
    public void increaseReadDataBytes(int increment) {
        this.ioSession.increaseReadDataBytes(increment);
    }

    /**
     * Increase the number of bytes written on the data connection
     * 
     * @param increment
     *            The number of bytes written
     */
    public void increaseWrittenDataBytes(int increment) {
        this.ioSession.increaseWrittenDataBytes(increment);
    }

    /**
     * Is bound
     */
    public boolean isBound() {
        return this.ioSession.isBound();
    }

    public boolean isSecure() {
        // TODO Auto-generated method stub
        return this.ioSession.isSecure();
    }

    public void removeAttribute(final String name) {
        if (name.startsWith(SmscIoSession.ATTRIBUTE_PREFIX)) {
            throw new IllegalArgumentException("Illegal removal of internal attribute");
        }

        this.ioSession.removeAttribute(name);
    }

    /**
     * Set attribute.
     */
    public void setAttribute(final String name, final Object value) {
        if (name.startsWith(SmscIoSession.ATTRIBUTE_PREFIX)) {
            throw new IllegalArgumentException("Illegal setting of internal attribute");
        }

        this.ioSession.setAttribute(name, value);
    }

    public void setMaxIdleTime(final int maxIdleTime) {
        this.ioSession.setMaxIdleTime(maxIdleTime);
    }

    public void write(SmscReply reply) throws SmscException {
        this.ioSession.write(reply);
    }

}
