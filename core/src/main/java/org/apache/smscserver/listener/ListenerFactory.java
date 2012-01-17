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
import java.net.UnknownHostException;
import java.util.List;

import org.apache.mina.filter.firewall.Subnet;
import org.apache.smscserver.SmscServerConfigurationException;
import org.apache.smscserver.ipfilter.SessionFilter;
import org.apache.smscserver.listener.nio.NioListener;
import org.apache.smscserver.ssl.SslConfiguration;

/**
 * Factory for listeners. Listeners themselves are immutable and must be created using this factory.
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class ListenerFactory {

    private String serverAddress;

    private int port;

    private SslConfiguration ssl;

    private boolean implicitSsl = false;

    private int idleTimeout = 300;

    private List<InetAddress> blockedAddresses;

    private List<Subnet> blockedSubnets;

    /**
     * The Session filter
     */
    private SessionFilter sessionFilter = null;

    /**
     * Default constructor
     */
    public ListenerFactory() {
        // do nothing
    }

    /**
     * Copy constructor, will copy properties from the provided listener.
     * 
     * @param listener
     *            The listener which properties will be used for this factory
     */
    public ListenerFactory(Listener listener) {
        this.serverAddress = listener.getServerAddress();
        this.port = listener.getPort();
        this.ssl = listener.getSslConfiguration();
        this.implicitSsl = listener.isImplicitSsl();
        this.idleTimeout = listener.getIdleTimeout();
        // TODO remove the next two lines if and when we remove the deprecated methods.
        this.blockedAddresses = listener.getBlockedAddresses();
        this.blockedSubnets = listener.getBlockedSubnets();
        this.sessionFilter = listener.getSessionFilter();
    }

    /**
     * Create a listener based on the settings of this factory. The listener is immutable.
     * 
     * @return The created listener
     */
    public Listener createListener() {
        try {
            InetAddress.getByName(this.serverAddress);
        } catch (UnknownHostException e) {
            throw new SmscServerConfigurationException("Unknown host", e);
        }
        // Deal with the old style black list and new session Filter here.
        if (this.sessionFilter != null) {
            if ((this.blockedAddresses != null) || (this.blockedSubnets != null)) {
                throw new IllegalStateException(
                        "Usage of SessionFilter in combination with blockedAddesses/subnets is not supported. ");
            }
        }
        if ((this.blockedAddresses != null) || (this.blockedSubnets != null)) {
            return new NioListener(this.serverAddress, this.port, this.implicitSsl, this.ssl, this.idleTimeout,
                    this.blockedAddresses, this.blockedSubnets);
        } else {
            return new NioListener(this.serverAddress, this.port, this.implicitSsl, this.ssl, this.idleTimeout,
                    this.sessionFilter);
        }
    }

    /**
     * @deprecated Replaced by the IpFilter. Retrieves the {@link InetAddress} for which listeners created by this
     *             factory blocks connections
     * 
     * @return The list of {@link InetAddress}es
     */
    @Deprecated
    public List<InetAddress> getBlockedAddresses() {
        return this.blockedAddresses;
    }

    /**
     * @deprecated Replaced by the IpFilter. Retrives the {@link Subnet}s for which listeners created by this factory
     *             blocks connections
     * 
     * @return The list of {@link Subnet}s
     */
    @Deprecated
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
     * Get the port on which listeners created by this factory is waiting for requests.
     * 
     * @return The port
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Get the {@link InetAddress} used for binding the local socket. Defaults to null, that is, the server binds to all
     * available network interfaces
     * 
     * @return The local socket {@link InetAddress}, if set
     */
    public String getServerAddress() {
        return this.serverAddress;
    }

    /**
     * Returns the currently configured <code>SessionFilter</code>, if any.
     * 
     * @return the currently configured <code>SessionFilter</code>, if any. Returns <code>null</code>, if no
     *         <code>SessionFilter</code> is configured.
     */
    public SessionFilter getSessionFilter() {
        return this.sessionFilter;
    }

    /**
     * Get the {@link SslConfiguration} used for listeners created by this factory
     * 
     * @return The {@link SslConfiguration}
     */
    public SslConfiguration getSslConfiguration() {
        return this.ssl;
    }

    /**
     * Is listeners created by this factory in SSL mode automatically or must the client explicitly request to use SSL
     * 
     * @return true is listeners created by this factory is automatically in SSL mode, false otherwise
     */
    public boolean isImplicitSsl() {
        return this.implicitSsl;
    }

    /**
     * @deprecated Replaced by the IpFilter. Sets the {@link InetAddress} that listeners created by this factory will
     *             block from connecting
     * 
     * @param blockedAddresses
     *            The list of {@link InetAddress}es
     */
    @Deprecated
    public void setBlockedAddresses(List<InetAddress> blockedAddresses) {
        this.blockedAddresses = blockedAddresses;
    }

    /**
     * Sets the {@link Subnet}s that listeners created by this factory will block from connecting
     * 
     * @param blockedSubnets
     *            The list of {@link Subnet}s
     * @deprecated Replaced by the IpFilter.
     */
    @Deprecated
    public void setBlockedSubnets(List<Subnet> blockedSubnets) {
        this.blockedSubnets = blockedSubnets;
    }

    /**
     * Set the number of seconds during which no network activity is allowed before a session is closed due to
     * inactivity.
     * 
     * @param idleTimeout
     *            The idle timeout in seconds
     */
    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    /**
     * Should listeners created by this factory be in SSL mode automatically or must the client explicitly request to
     * use SSL
     * 
     * @param implicitSsl
     *            true is listeners created by this factory should automatically be in SSL mode, false otherwise
     */
    public void setImplicitSsl(boolean implicitSsl) {
        this.implicitSsl = implicitSsl;
    }

    /**
     * Set the port on which listeners created by this factory will accept requests. Or set to 0 (zero) is the port
     * should be automatically assigned
     * 
     * @param port
     *            The port to use.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Set the {@link InetAddress} used for binding the local socket. Defaults to null, that is, the server binds to all
     * available network interfaces
     * 
     * @param serverAddress
     *            The local socket {@link InetAddress}
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * Sets the session filter to the given filter.
     * 
     * @param sessionFilter
     *            the session filter.
     */
    public void setSessionFilter(SessionFilter sessionFilter) {
        this.sessionFilter = sessionFilter;
    }

    /**
     * Set the {@link SslConfiguration} to use by listeners created by this factory
     * 
     * @param ssl
     *            The {@link SslConfiguration}
     */
    public void setSslConfiguration(SslConfiguration ssl) {
        this.ssl = ssl;
    }
}