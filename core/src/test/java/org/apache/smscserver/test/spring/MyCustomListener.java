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

package org.apache.smscserver.test.spring;

import java.net.InetAddress;
import java.util.List;
import java.util.Set;

import org.apache.mina.filter.firewall.Subnet;
import org.apache.smscserver.SmscServerContext;
import org.apache.smscserver.ipfilter.SessionFilter;
import org.apache.smscserver.listener.Listener;
import org.apache.smscserver.smsclet.SmscIoSession;
import org.apache.smscserver.ssl.SslConfiguration;

/**
 * Used for testing creation of custom listeners from Spring config
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * 
 */
public class MyCustomListener implements Listener {

    private int port;

    public Set<SmscIoSession> getActiveSessions() {
        return null;
    }

    public List<InetAddress> getBlockedAddresses() {
        return null;
    }

    public List<Subnet> getBlockedSubnets() {
        return null;
    }

    public int getIdleTimeout() {
        return 0;
    }

    public int getPort() {
        return this.port;
    }

    public String getServerAddress() {
        return null;
    }

    public SessionFilter getSessionFilter() {
        return null;
    }

    public SslConfiguration getSslConfiguration() {
        return null;
    }

    public boolean isImplicitSsl() {
        return false;
    }

    public boolean isStopped() {
        return false;
    }

    public boolean isSuspended() {
        return false;
    }

    public void resume() {

    }

    public void setPort(int port) {
        this.port = port;
    }

    public void start(SmscServerContext serverContext) {

    }

    public void stop() {

    }

    public void suspend() {

    }

}
