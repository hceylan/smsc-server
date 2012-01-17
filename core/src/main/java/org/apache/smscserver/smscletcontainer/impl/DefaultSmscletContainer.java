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

package org.apache.smscserver.smscletcontainer.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.smscserver.smsclet.SmscException;
import org.apache.smscserver.smsclet.SmscReply;
import org.apache.smscserver.smsclet.SmscRequest;
import org.apache.smscserver.smsclet.SmscSession;
import org.apache.smscserver.smsclet.Smsclet;
import org.apache.smscserver.smsclet.SmscletContext;
import org.apache.smscserver.smscletcontainer.SmscletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * This smsclet calls other smsclet methods and returns appropriate smsc reply.
 * 
 * <strong><strong>Internal class, do not use directly.</strong></strong>
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class DefaultSmscletContainer implements SmscletContainer {

    private final Logger LOG = LoggerFactory.getLogger(DefaultSmscletContainer.class);

    private final Map<String, Smsclet> smsclets;

    public DefaultSmscletContainer() {
        this(new ConcurrentHashMap<String, Smsclet>());
    }

    public DefaultSmscletContainer(Map<String, Smsclet> smsclets) {
        this.smsclets = smsclets;
    }

    /**
     * Destroy all smsclets.
     */
    public void destroy() {
        for (Entry<String, Smsclet> entry : this.smsclets.entrySet()) {
            try {
                entry.getValue().destroy();
            } catch (Exception ex) {
                this.LOG.error(entry.getKey() + " :: SmscletHandler.destroy()", ex);
            }
        }
    }

    /**
     * Get Smsclet for the given name.
     */
    public synchronized Smsclet getSmsclet(String name) {
        if (name == null) {
            return null;
        }

        return this.smsclets.get(name);
    }

    /**
     * @see SmscletContainer#getSmsclets()
     */
    public synchronized Map<String, Smsclet> getSmsclets() {
        return this.smsclets;
    }

    public synchronized void init(SmscletContext smscletContext) throws SmscException {
        for (Entry<String, Smsclet> entry : this.smsclets.entrySet()) {
            entry.getValue().init(smscletContext);
        }
    }

    /**
     * Call smsclet onConnect.
     */
    public boolean onConnect(SmscSession session) throws SmscException, IOException {
        for (Entry<String, Smsclet> entry : this.smsclets.entrySet()) {
            if (!entry.getValue().onConnect(session)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Call smsclet onDisconnect.
     */
    public void onDisconnect(SmscSession session) throws SmscException, IOException {
        for (Entry<String, Smsclet> entry : this.smsclets.entrySet()) {
            entry.getValue().onDisconnect(session);
        }
    }

    /**
     * Called by the smsclet container after a request has been received by the server. The implementation should return
     * based on the desired action to be taken by the server:
     * 
     * @param session
     *            The current session
     * @param request
     *            The current request
     * @return the reply that will be sent for this command.
     * @throws SmscException
     * @throws IOException
     */
    public SmscReply onRequest(SmscSession session, SmscRequest request) throws SmscException, IOException {

        SmscReply reply = null;
        for (Entry<String, Smsclet> entry : this.smsclets.entrySet()) {

            reply = entry.getValue().onRequest(session, request);
            if (reply != null) {
                return reply;
            }
        }

        return reply;
    }

}
