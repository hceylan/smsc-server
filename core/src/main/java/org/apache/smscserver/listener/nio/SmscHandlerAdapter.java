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
import org.apache.smscserver.SmscHandler;
import org.apache.smscserver.SmscServerContext;
import org.apache.smscserver.impl.DefaultSmscIoSession;
import org.apache.smscserver.packet.impl.SmscStatusReplyImpl;
import org.apache.smscserver.smsclet.SmscPacket;
import org.apache.smscserver.smsclet.SmscReply;
import org.apache.smscserver.smsclet.SmscRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * Adapter between MINA handler and the {@link SmscHandler} interface
 * 
 * @author hceylan
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
        this.smscHandler.exceptionCaught(new DefaultSmscIoSession(session, this.context), cause);
    }

    public SmscHandler getSmscHandler() {
        return this.smscHandler;
    }

    public void messageReceived(IoSession session, Object message) throws Exception {
        DefaultSmscIoSession smscSession = new DefaultSmscIoSession(session, this.context);

        if (message instanceof SmscRequest) {

            SmscRequest request = (SmscRequest) message;

            smscSession.setRequest(request);

            SmscReply reply = this.smscHandler.messageReceived(smscSession, request);
            if (reply == null) {
                reply = new SmscStatusReplyImpl(request, SmscReply.ErrorCode.ESME_RINVCMDID);
            }

            session.write(reply);
        }
    }

    public void messageSent(IoSession session, Object message) throws Exception {
        this.smscHandler.messageSent(new DefaultSmscIoSession(session, this.context), (SmscPacket) message);
    }

    public void sessionClosed(IoSession session) throws Exception {
        this.smscHandler.sessionClosed(new DefaultSmscIoSession(session, this.context));
    }

    public void sessionCreated(IoSession session) throws Exception {
        DefaultSmscIoSession smscSession = new DefaultSmscIoSession(session, this.context);
        MdcInjectionFilter.setProperty(session, "session", smscSession.getSessionId().toString());

        this.smscHandler.sessionCreated(smscSession);
    }

    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        this.smscHandler.sessionIdle(new DefaultSmscIoSession(session, this.context), status);
    }

    public void sessionOpened(IoSession session) throws Exception {
        this.smscHandler.sessionOpened(new DefaultSmscIoSession(session, this.context));
    }

    public void setSmscHandler(SmscHandler handler) {
        this.smscHandler = handler;

    }

}
