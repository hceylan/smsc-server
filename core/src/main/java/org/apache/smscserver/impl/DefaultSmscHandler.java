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

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.write.WriteToClosedSessionException;
import org.apache.smscserver.command.Command;
import org.apache.smscserver.command.CommandFactory;
import org.apache.smscserver.listener.Listener;
import org.apache.smscserver.smsclet.SmscReply;
import org.apache.smscserver.smsclet.SmscRequest;
import org.apache.smscserver.smscletcontainer.SmscletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * 
 */
public class DefaultSmscHandler implements SmscHandler {

    private final Logger LOG = LoggerFactory.getLogger(DefaultSmscHandler.class);

    private SmscServerContext context;

    private Listener listener;

    public void exceptionCaught(final SmscIoSession session, final Throwable cause) throws Exception {
        if (cause instanceof WriteToClosedSessionException) {
            WriteToClosedSessionException writeToClosedSessionException = (WriteToClosedSessionException) cause;
            this.LOG.warn("Client closed connection before all replies could be sent, last reply was {}",
                    writeToClosedSessionException.getRequest());
            session.close(false).awaitUninterruptibly(10000);
        } else {
            this.LOG.error("Exception caught, closing session", cause);
            session.close(false).awaitUninterruptibly(10000);
        }

    }

    public void init(final SmscServerContext context, final Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    public SmscReply messageReceived(final SmscIoSession session, final SmscRequest request) throws Exception {
        session.updateLastAccessTime();

        SmscReply reply = this.context.getSmscletContainer().onRequest(session.getSmscletSession(), request);
        if (reply != null) {
            return reply;
        }

        int commandID = request.getCommandId();
        CommandFactory commandFactory = this.context.getCommandFactory();
        Command command = commandFactory.getCommand(commandID);

        return command.execute(session, this.context, request);
    }

    public void messageSent(final SmscIoSession session, final SmscReply reply) throws Exception {
        // do nothing
    }

    public void sessionClosed(final SmscIoSession session) throws Exception {
        this.LOG.debug("Closing session");
        try {
            this.context.getSmscletContainer().onDisconnect(session.getSmscletSession());
        } catch (Exception e) {
            // swallow the exception, we're closing down the session anyways
            this.LOG.warn("Smsclet threw an exception on disconnect", e);
        }

        ServerSmscStatistics stats = ((ServerSmscStatistics) this.context.getSmscStatistics());

        if (stats != null) {
            stats.setUnbind(session);
            stats.setCloseConnection(session);
            this.LOG.debug("Statistics login and connection count decreased due to session close");
        } else {
            this.LOG.warn("Statistics not available in session, can not decrease login and connection count");
        }
        this.LOG.debug("Session closed");
    }

    public void sessionCreated(final SmscIoSession session) throws Exception {
        session.setListener(this.listener);

        ServerSmscStatistics stats = ((ServerSmscStatistics) this.context.getSmscStatistics());

        if (stats != null) {
            stats.setOpenConnection(session);
        }
    }

    public void sessionIdle(final SmscIoSession session, final IdleStatus status) throws Exception {
        this.LOG.info("Session idle, closing");
        session.close(false).awaitUninterruptibly(10000);
    }

    public void sessionOpened(final SmscIoSession session) throws Exception {
        SmscletContainer smsclets = this.context.getSmscletContainer();

        try {
            if (!smsclets.onConnect(session.getSmscletSession())) {
                this.LOG.debug("Smsclet returned DISCONNECT, session will be closed");
                session.close(false).awaitUninterruptibly(10000);

            } else {
                session.updateLastAccessTime();
            }
        } catch (Exception e) {
            this.LOG.debug("Smsclet threw exception", e);
        }
    }

}
