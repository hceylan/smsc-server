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

import ie.omk.smpp.net.TcpLink;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.firewall.Subnet;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.smscserver.SmscHandler;
import org.apache.smscserver.SmscServerConfigurationException;
import org.apache.smscserver.SmscServerContext;
import org.apache.smscserver.impl.DefaultSmscHandler;
import org.apache.smscserver.impl.DefaultSmscIoSession;
import org.apache.smscserver.ipfilter.MinaSessionFilter;
import org.apache.smscserver.ipfilter.SessionFilter;
import org.apache.smscserver.listener.Listener;
import org.apache.smscserver.listener.ListenerFactory;
import org.apache.smscserver.smsclet.SmscIoSession;
import org.apache.smscserver.ssl.ClientAuth;
import org.apache.smscserver.ssl.SslConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * The default {@link Listener} implementation.
 * 
 * @author hceylan
 */
public class NioListener extends AbstractListener {

    private final Logger LOG = LoggerFactory.getLogger(NioListener.class);

    private SocketAcceptor acceptor;

    private InetSocketAddress address;

    boolean suspended = false;

    private final SmscHandler handler = new DefaultSmscHandler();

    private SmscServerContext context;

    /**
     * @deprecated Use the constructor with IpFilter instead. Constructor for internal use, do not use directly. Instead
     *             use {@link ListenerFactory}
     */
    @Deprecated
    public NioListener(String serverAddress, int port, boolean implicitSsl, SslConfiguration sslConfiguration,
            int idleTimeout, List<InetAddress> blockedAddresses, List<Subnet> blockedSubnets) {
        super(serverAddress, port, implicitSsl, sslConfiguration, idleTimeout, blockedAddresses, blockedSubnets);
    }

    /**
     * Constructor for internal use, do not use directly. Instead use {@link ListenerFactory}
     */
    public NioListener(String serverAddress, int port, boolean implicitSsl, SslConfiguration sslConfiguration,
            int idleTimeout, SessionFilter sessionFilter) {
        super(serverAddress, port, implicitSsl, sslConfiguration, idleTimeout, sessionFilter);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public synchronized Set<SmscIoSession> getActiveSessions() {
        Map<Long, IoSession> sessions = this.acceptor.getManagedSessions();

        Set<SmscIoSession> smscSessions = new HashSet<SmscIoSession>();
        for (IoSession session : sessions.values()) {
            smscSessions.add(new DefaultSmscIoSession(session, this.context));
        }

        return smscSessions;
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public int getPort() {
        return TcpLink.DEFAULT_PORT;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean isStopped() {
        return this.acceptor == null;
    }

    /**
     * @see Listener#isSuspended()
     */
    public boolean isSuspended() {
        return this.suspended;

    }

    /**
     * @see Listener#resume()
     */
    public synchronized void resume() {
        if ((this.acceptor != null) && this.suspended) {
            try {
                this.LOG.debug("Resuming listener");
                this.acceptor.bind(this.address);
                this.LOG.debug("Listener resumed");

                this.updatePort();

                this.suspended = false;
            } catch (IOException e) {
                this.LOG.error("Failed to resume listener", e);
            }
        }
    }

    /**
     * @see Listener#start(SmscServerContext)
     */
    public synchronized void start(SmscServerContext context) {
        if (!this.isStopped()) {
            // listener already started, don't allow
            throw new IllegalStateException("Listener already started");
        }

        try {

            this.context = context;

            this.acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors());

            if (this.getServerAddress() != null) {
                this.address = new InetSocketAddress(this.getServerAddress(), this.getPort());
            } else {
                this.address = new InetSocketAddress(this.getPort());
            }

            this.acceptor.setReuseAddress(true);
            this.acceptor.getSessionConfig().setReadBufferSize(2048);
            this.acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, this.getIdleTimeout());
            // Decrease the default receiver buffer size
            this.acceptor.getSessionConfig().setReceiveBufferSize(512);

            MdcInjectionFilter mdcFilter = new MdcInjectionFilter();

            this.acceptor.getFilterChain().addLast("mdcFilter", mdcFilter);

            SessionFilter sessionFilter = this.getSessionFilter();
            if (sessionFilter != null) {
                // add and IP filter to the filter chain.
                this.acceptor.getFilterChain().addLast("sessionFilter", new MinaSessionFilter(sessionFilter));
            }

            this.acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(context.getThreadPoolExecutor()));
            this.acceptor.getFilterChain().addLast("codec",
                    new ProtocolCodecFilter(new SmscServerProtocolCodecFactory()));
            this.acceptor.getFilterChain().addLast("mdcFilter2", mdcFilter);
            this.acceptor.getFilterChain().addLast("logger", new SmscLoggingFilter());

            if (this.isImplicitSsl()) {
                SslConfiguration ssl = this.getSslConfiguration();
                SslFilter sslFilter;
                try {
                    sslFilter = new SslFilter(ssl.getSSLContext());
                } catch (GeneralSecurityException e) {
                    throw new SmscServerConfigurationException("SSL could not be initialized, check configuration");
                }

                if (ssl.getClientAuth() == ClientAuth.NEED) {
                    sslFilter.setNeedClientAuth(true);
                } else if (ssl.getClientAuth() == ClientAuth.WANT) {
                    sslFilter.setWantClientAuth(true);
                }

                if (ssl.getEnabledCipherSuites() != null) {
                    sslFilter.setEnabledCipherSuites(ssl.getEnabledCipherSuites());
                }

                this.acceptor.getFilterChain().addFirst("sslFilter", sslFilter);
            }

            this.handler.init(context, this);
            this.acceptor.setHandler(new SmscHandlerAdapter(context, this.handler));

            try {
                this.acceptor.bind(this.address);
            } catch (IOException e) {
                throw new SmscServerConfigurationException("Failed to bind to address " + this.address
                        + ", check configuration", e);
            }

            this.updatePort();

        } catch (RuntimeException e) {
            // clean up if we fail to start
            this.stop();

            throw e;
        }
    }

    /**
     * @see Listener#stop()
     */
    public synchronized void stop() {
        // close server socket
        if (this.acceptor != null) {
            this.acceptor.unbind();
            this.acceptor.dispose();
            this.acceptor = null;
        }
        this.context = null;
    }

    /**
     * @see Listener#suspend()
     */
    public synchronized void suspend() {
        if ((this.acceptor != null) && !this.suspended) {
            this.LOG.debug("Suspending listener");
            this.acceptor.unbind();

            this.suspended = true;
            this.LOG.debug("Listener suspended");
        }
    }

    private void updatePort() {
        // update the port to the real port bound by the listener
        this.setPort(this.acceptor.getLocalAddress().getPort());
    }
}
