/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.smscserver.listener.nio;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.smscserver.impl.SmscHandler;
import org.apache.smscserver.impl.SmscIoSession;
import org.apache.smscserver.impl.SmscServerContext;
import org.apache.smscserver.packet.impl.NotImplementedReply;
import org.apache.smscserver.smsclet.SmscReply;
import org.apache.smscserver.smsclet.SmscRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * Adapter between MINA handler and the {@link SmscHandler} interface
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * 
 */
public class SmscHandlerAdapter implements IoHandler {

    private final SmscServerContext context;

    private SmscHandler smscHandler;

    public SmscHandlerAdapter(SmscServerContext context, SmscHandler smscHandler) {
        this.context = context;
        this.smscHandler = smscHandler;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        this.smscHandler.exceptionCaught(new SmscIoSession(session, this.context), cause);
    }

    public SmscHandler getSmscHandler() {
        return this.smscHandler;
    }

    public void messageReceived(IoSession session, Object message) throws Exception {
        SmscIoSession smscSession = new SmscIoSession(session, this.context);

        SmscRequest request = (SmscRequest) message;

        if (request == null) {
            // TODO: Hasan handle InvMsgId
        }

        smscSession.setRequest(request);

        SmscReply reply = this.smscHandler.messageReceived(smscSession, request);
        if (reply == null) {
            reply = new NotImplementedReply(request);
        }

        session.write(reply);
    }

    public void messageSent(IoSession session, Object message) throws Exception {
        this.smscHandler.messageSent(new SmscIoSession(session, this.context), (SmscReply) message);
    }

    public void sessionClosed(IoSession session) throws Exception {
        this.smscHandler.sessionClosed(new SmscIoSession(session, this.context));
    }

    public void sessionCreated(IoSession session) throws Exception {
        SmscIoSession smscSession = new SmscIoSession(session, this.context);
        MdcInjectionFilter.setProperty(session, "session", smscSession.getSessionId().toString());

        this.smscHandler.sessionCreated(smscSession);
    }

    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        this.smscHandler.sessionIdle(new SmscIoSession(session, this.context), status);
    }

    public void sessionOpened(IoSession session) throws Exception {
        this.smscHandler.sessionOpened(new SmscIoSession(session, this.context));
    }

    public void setSmscHandler(SmscHandler handler) {
        this.smscHandler = handler;

    }

}
