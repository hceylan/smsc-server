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

package org.apache.smscserver.listener.nio;

import java.net.InetAddress;
import java.util.List;

import org.apache.mina.filter.firewall.Subnet;
import org.apache.smscserver.ipfilter.IpFilterType;
import org.apache.smscserver.ipfilter.RemoteIpFilter;
import org.apache.smscserver.ipfilter.SessionFilter;
import org.apache.smscserver.listener.Listener;
import org.apache.smscserver.listener.ListenerFactory;
import org.apache.smscserver.ssl.SslConfiguration;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * Common base class for listener implementations
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public abstract class AbstractListener implements Listener {

    /**
     * Creates a SessionFilter that blacklists the given IP addresses and/or Subnets.
     * 
     * @param blockedAddresses
     *            the addresses to block
     * @param blockedSubnets
     *            the subnets to block
     * @return a SessionFilter that blacklists the given IP addresses and/or Subnets.
     */
    private static SessionFilter createBlackListFilter(List<InetAddress> blockedAddresses, List<Subnet> blockedSubnets) {
        if ((blockedAddresses == null) && (blockedSubnets == null)) {
            return null;
        }
        // Initialize the IP filter with Deny type
        RemoteIpFilter ipFilter = new RemoteIpFilter(IpFilterType.DENY);
        if (blockedSubnets != null) {
            ipFilter.addAll(blockedSubnets);
        }
        if (blockedAddresses != null) {
            for (InetAddress address : blockedAddresses) {
                ipFilter.add(new Subnet(address, 32));
            }
        }
        return ipFilter;
    }

    private final String serverAddress;

    private int port;

    private final SslConfiguration ssl;

    private final boolean implicitSsl;

    private final int idleTimeout;

    private final List<InetAddress> blockedAddresses;

    private final List<Subnet> blockedSubnets;

    private final SessionFilter sessionFilter;

    /**
     * @deprecated Use the constructor with IpFilter instead. Constructor for internal use, do not use directly. Instead
     *             use {@link ListenerFactory}
     */
    @Deprecated
    public AbstractListener(String serverAddress, int port, boolean implicitSsl, SslConfiguration sslConfiguration,
            int idleTimeout, List<InetAddress> blockedAddresses, List<Subnet> blockedSubnets) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.implicitSsl = implicitSsl;
        this.ssl = sslConfiguration;
        this.idleTimeout = idleTimeout;
        this.sessionFilter = AbstractListener.createBlackListFilter(blockedAddresses, blockedSubnets);
        this.blockedAddresses = blockedAddresses;
        this.blockedSubnets = blockedSubnets;
    }

    /**
     * Constructor for internal use, do not use directly. Instead use {@link ListenerFactory}
     */
    public AbstractListener(String serverAddress, int port, boolean implicitSsl, SslConfiguration sslConfiguration,
            int idleTimeout, SessionFilter sessionFilter) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.implicitSsl = implicitSsl;
        this.ssl = sslConfiguration;
        this.idleTimeout = idleTimeout;
        this.sessionFilter = sessionFilter;
        this.blockedAddresses = null;
        this.blockedSubnets = null;
    }

    /**
     * Retrives the {@link InetAddress} for which this listener blocks connections
     * 
     * @return The list of {@link InetAddress}es
     */
    public List<InetAddress> getBlockedAddresses() {
        return this.blockedAddresses;
    }

    /**
     * Retrieves the {@link Subnet}s for this listener blocks connections
     * 
     * @return The list of {@link Subnet}s
     */
    public List<Subnet> getBlockedSubnets() {
        return this.blockedSubnets;
    }

    /**
     * Get the number of seconds during which no network activity is allowed before a session is closed due to
     * inactivity.
     * 
     * @return The idle time out
     */
    public int getIdleTimeout() {
        return this.idleTimeout;
    }

    /**
     * {@inheritDoc}
     */
    public int getPort() {
        return this.port;
    }

    /**
     * {@inheritDoc}
     */
    public String getServerAddress() {
        return this.serverAddress;
    }

    public SessionFilter getSessionFilter() {
        return this.sessionFilter;
    }

    /**
     * {@inheritDoc}
     */
    public SslConfiguration getSslConfiguration() {
        return this.ssl;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isImplicitSsl() {
        return this.implicitSsl;
    }

    /**
     * Used internally to update the port after binding
     * 
     * @param port
     */
    protected void setPort(int port) {
        this.port = port;
    }
}
