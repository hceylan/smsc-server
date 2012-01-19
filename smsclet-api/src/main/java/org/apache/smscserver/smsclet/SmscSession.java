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

package org.apache.smscserver.smsclet;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.UUID;

/**
 * Defines an client session with the FTP server. The session is born when the client connects and dies when the client
 * disconnects. Smsclet methods will always get the same session for one user and one connection. So the attributes set
 * by <code>setAttribute()</code> will be always available later unless that attribute is removed or the client
 * disconnects.
 * 
 * @author hceylan
 */
public interface SmscSession {

    /**
     * Returns the value of the named attribute as an Object.
     * 
     * @param name
     *            The attribute name
     * @return The attribute value, or null if no attribute of the given name exists.
     */
    Object getAttribute(String name);

    /**
     * Get the bind time.
     * 
     * @return Time when the client bound into the server
     */
    Date getBindTime();

    /**
     * Returns the IP address of the client that sent the request.
     * 
     * @return The client {@link InetAddress}
     */
    InetSocketAddress getClientAddress();

    /**
     * Retrieve the certificates for the client, if running over SSL and with client authentication
     * 
     * @return The Certificate chain, or null if the certificates are not avialble
     */
    Certificate[] getClientCertificates();

    /**
     * Get connection time.
     * 
     * @return Time when the client connected to the server
     */
    Date getConnectionTime();

    /**
     * Get the number of failed binds.
     * 
     * @return The number of failed binds. When bind succeeds, this will return 0.
     */
    int getFailedBinds();

    /**
     * Get last access time.
     * 
     * @return The last time the session performed any action
     */
    Date getLastAccessTime();

    /**
     * Returns maximum idle time. This time equals to {@link ConnectionManagerImpl#getDefaultIdleSec()} until user bind,
     * and {@link User#getMaxIdleTime()} after user bind.
     * 
     * @return The number of seconds the client is allowed to be idle before disconnected.
     */
    int getMaxIdleTime();

    /**
     * Returns the IP address of the server
     * 
     * @return The server {@link InetAddress}
     */
    InetSocketAddress getServerAddress();

    /**
     * Get the unique ID for this session. This ID will be maintained for the entire session and is also available to
     * MDC logging using the "session" identifier.
     * 
     * @return The unique ID for this session
     */
    public UUID getSessionId();

    /**
     * Get user object.
     * 
     * @return The current {@link User}
     */
    User getUser();

    /**
     * Is the user bound in?
     * 
     * @return true if the user is bound
     */
    boolean isBound();

    /**
     * Indicates whether the control socket for this session is secure, that is, running over SSL/TLS
     * 
     * @return true if the control socket is secured
     */
    boolean isSecure();

    /**
     * Removes an attribute from this request.
     * 
     * @param name
     *            The attribute name
     */
    void removeAttribute(String name);

    /**
     * Stores an attribute in this request. It will be available until it was removed or when the connection ends.
     * 
     * @param name
     *            The attribute name
     * @param value
     *            The attribute value
     */
    void setAttribute(String name, Object value);

    /**
     * Set maximum idle time in seconds. This time equals to {@link ConnectionManagerImpl#getDefaultIdleSec()} until
     * user bind, and {@link User#getMaxIdleTime()} after user bind.
     * 
     * @param maxIdleTimeSec
     *            The number of seconds the client is allowed to be idle before disconnected.
     */
    void setMaxIdleTime(int maxIdleTimeSec);

    /**
     * Write a reply to the client
     * 
     * @param reply
     *            The reply that will be sent to the client
     * @throws SmscException
     */
    void write(SmscReply reply) throws SmscException;

}
