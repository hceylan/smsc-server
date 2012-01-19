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

package org.apache.smscserver.listener;

import java.net.InetAddress;
import java.util.List;
import java.util.Set;

import org.apache.mina.filter.firewall.Subnet;
import org.apache.smscserver.SmscServerContext;
import org.apache.smscserver.impl.DefaultSmscIoSession;
import org.apache.smscserver.ipfilter.SessionFilter;
import org.apache.smscserver.smsclet.SmscIoSession;
import org.apache.smscserver.ssl.SslConfiguration;

/**
 * Interface for the component responsible for waiting for incoming socket requests and kicking off
 * {@link DefaultSmscIoSession}s
 * 
 * @author hceylan
 */
public interface Listener {

    /**
     * Returns the currently active sessions for this listener. If no sessions are active, an empty {@link Set} would be
     * returned.
     * 
     * @return The currently active sessions
     */
    Set<SmscIoSession> getActiveSessions();

    /**
     * @deprecated Replaced by IpFilter. Retrieves the {@link InetAddress} for which this listener blocks connections.
     * 
     * @return The list of {@link InetAddress}es. This method returns a valid list if and only if there is an
     *         <code>IpFilter</code> set, and, if it is an instance of <code>DefaultIpFilter</code> and it is of type
     *         <code>IpFilterType.DENY</code>. This functionality is provided for backward compatibility purpose only.
     */
    @Deprecated
    List<InetAddress> getBlockedAddresses();

    /**
     * @deprecated Replaced by IpFilter. Retrieves the {@link Subnet}s for this listener blocks connections.
     * 
     * @return The list of {@link Subnet}s. This method returns a valid list if and only if there is an
     *         <code>IpFilter</code> set, and, if it is an instance of <code>DefaultIpFilter</code> and it is of type
     *         <code>IpFilterType.DENY</code>. This functionality is provided for backward compatibility purpose only.
     */
    @Deprecated
    List<Subnet> getBlockedSubnets();

    /**
     * Get the number of seconds during which no network activity is allowed before a session is closed due to
     * inactivity.
     * 
     * @return The idle time out
     */
    int getIdleTimeout();

    /**
     * Get the port on which this listener is waiting for requests. For listeners where the port is automatically
     * assigned, this will return the bound port.
     * 
     * @return The port
     */
    int getPort();

    /**
     * Get the {@link InetAddress} used for binding the local socket. Defaults to null, that is, the server binds to all
     * available network interfaces
     * 
     * @return The local socket {@link InetAddress}, if set
     */
    String getServerAddress();

    /**
     * Returns the <code>SessionFilter</code> associated with this listener. May return <code>null</code>.
     * 
     * @return the <code>SessionFilter</code> associated with this listener. May return <code>null</code>.
     */
    SessionFilter getSessionFilter();

    /**
     * Get the {@link SslConfiguration} used for this listener
     * 
     * @return The current {@link SslConfiguration}
     */
    SslConfiguration getSslConfiguration();

    /**
     * Is this listener in SSL mode automatically or must the client explicitly request to use SSL
     * 
     * @return true is the listener is automatically in SSL mode, false otherwise
     */
    boolean isImplicitSsl();

    /**
     * Checks if the listener is currently started.
     * 
     * @return False if the listener is started
     */
    boolean isStopped();

    /**
     * Checks if the listener is currently suspended
     * 
     * @return True if the listener is suspended
     */
    boolean isSuspended();

    /**
     * Resumes a suspended listener. The method should not return until the listener has started accepting socket
     * requests.
     */
    void resume();

    /**
     * Start the listener, will initiate the listener waiting on the socket. The method should not return until the
     * listener has started accepting socket requests.
     * 
     * @param serverContext
     *            The current {@link SmscServerContext}
     * 
     * @throws Exception
     *             On error during start up
     */
    void start(SmscServerContext serverContext);

    /**
     * Stop the listener, it should no longer except socket requests. The method should not return until the listener
     * has stopped accepting socket requests.
     */
    void stop();

    /**
     * Temporarily stops the listener from accepting socket requests. Resume the listener by using the {@link #resume()}
     * method. The method should not return until the listener has stopped accepting socket requests.
     */
    void suspend();
}