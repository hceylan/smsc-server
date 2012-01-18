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

package org.apache.smscserver;

import java.io.IOException;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.smscserver.impl.DefaultSmscIoSession;
import org.apache.smscserver.listener.Listener;
import org.apache.smscserver.smsclet.SmscReply;
import org.apache.smscserver.smsclet.SmscRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 * 
 */
public interface SmscHandler {

    /**
     * Invoked when any exception is thrown by user {@link IoHandler} implementation or by MINA. If <code>cause</code>
     * is instanceof {@link IOException}, MINA will close the connection automatically.
     */
    void exceptionCaught(DefaultSmscIoSession session, Throwable cause) throws Exception;

    void init(SmscServerContext context, Listener listener);

    /**
     * Invoked when a message is received.
     * 
     * @return the reply that should be sent back to the client
     */
    SmscReply messageReceived(DefaultSmscIoSession session, SmscRequest request) throws Exception;

    /**
     * Invoked when a message written by {@link IoSession#write(Object)} is sent out.
     */
    void messageSent(DefaultSmscIoSession session, SmscReply reply) throws Exception;

    /**
     * Invoked when a connection is closed.
     */
    void sessionClosed(DefaultSmscIoSession session) throws Exception;

    /**
     * Invoked from an I/O processor thread when a new connection has been created. Because this method is supposed to
     * be called from the same thread that handles I/O of multiple sessions, please implement this method to perform
     * tasks that consumes minimal amount of time such as socket parameter and user-defined session attribute
     * initialization.
     */
    void sessionCreated(DefaultSmscIoSession session) throws Exception;

    /**
     * Invoked with the related {@link IdleStatus} when a connection becomes idle.
     */
    void sessionIdle(DefaultSmscIoSession session, IdleStatus status) throws Exception;

    /**
     * Invoked when a connection has been opened. This method is invoked after {@link #sessionCreated(DefaultSmscIoSession)}.
     * The biggest difference from {@link #sessionCreated(DefaultSmscIoSession)} is that it's invoked from other thread than an
     * I/O processor thread once thread modesl is configured properly.
     */
    void sessionOpened(DefaultSmscIoSession session) throws Exception;
}
