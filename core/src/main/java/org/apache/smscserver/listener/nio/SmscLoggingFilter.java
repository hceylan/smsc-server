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

import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.smscserver.smsclet.BindRequest;
import org.apache.smscserver.smsclet.SmscRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * Specialized @see {@link LoggingFilter} that optionally masks SMSC passwords.
 * 
 * @author hceylan
 */
public class SmscLoggingFilter extends LoggingFilter {

    private boolean maskPassword = true;

    private final Logger LOG;

    /**
     * @see LoggingFilter#LoggingFilter()
     */
    public SmscLoggingFilter() {
        this(SmscLoggingFilter.class.getName());
    }

    /**
     * @see LoggingFilter#LoggingFilter(Class)
     */
    public SmscLoggingFilter(Class<?> clazz) {
        this(clazz.getName());
    }

    /**
     * @see LoggingFilter#LoggingFilter(String)
     */
    public SmscLoggingFilter(String name) {
        super(name);

        this.LOG = LoggerFactory.getLogger(name);
    }

    /**
     * Are password masked?
     * 
     * @return true if passwords are masked
     */
    public boolean isMaskPassword() {
        return this.maskPassword;
    }

    /**
     * @see LoggingFilter#messageReceived(org.apache.mina.core.filterchain.IoFilter.NextFilter, IoSession, Object)
     */
    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        try {
            SmscRequest request = (SmscRequest) message;

            if (request instanceof BindRequest) {
                BindRequest bindRequest = (BindRequest) request;
                if (this.maskPassword) {
                    this.LOG.trace("Bind Request - {} -systemId {}, systemType {}", new Object[] { bindRequest.getId(),
                            bindRequest.getSystemId(), bindRequest.getSystemType() });
                } else {
                    this.LOG.trace("Bind Request - {} -systemId {}, password {}, systemType {}",
                            new Object[] { bindRequest.getId(), bindRequest.getSystemId(), bindRequest.getPassword(),
                                    bindRequest.getSystemType() });
                }
            } else {
                this.LOG.trace("Other Request - {}", request.getId());
            }
        } catch (Exception e) {
            // ignore
        }

        nextFilter.messageReceived(session, message);
    }

    @Override
    public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        super.messageSent(nextFilter, session, writeRequest);
    }

    /**
     * Mask password in log messages
     * 
     * @param maskPassword
     *            true if passwords should be masked
     */
    public void setMaskPassword(boolean maskPassword) {
        this.maskPassword = maskPassword;
    }

}
