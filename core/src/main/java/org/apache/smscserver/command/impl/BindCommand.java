/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.smscserver.command.impl;

import org.apache.smscserver.SmscServerContext;
import org.apache.smscserver.command.Command;
import org.apache.smscserver.impl.DefaultSmscIoSession;
import org.apache.smscserver.packet.impl.SmscBindReplyImpl;
import org.apache.smscserver.packet.impl.SmscStatusReplyImpl;
import org.apache.smscserver.smsclet.Authentication;
import org.apache.smscserver.smsclet.AuthenticationFailedException;
import org.apache.smscserver.smsclet.BindRequest;
import org.apache.smscserver.smsclet.SmscReply;
import org.apache.smscserver.smsclet.SmscRequest;
import org.apache.smscserver.smsclet.User;
import org.apache.smscserver.usermanager.UsernamePasswordAuthentication;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * Bind Command
 * 
 * <strong><strong>Internal class, do not use directly.</strong></strong>
 * 
 * @author hceylan
 */
public class BindCommand implements Command {

    public static final BindCommand SINGLETON = new BindCommand();

    /**
     * @param type
     * 
     */
    private BindCommand() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public SmscReply execute(DefaultSmscIoSession session, SmscServerContext context, SmscRequest request) {
        if (session.getUser() != null) {
            return new SmscStatusReplyImpl(request, SmscReply.ErrorCode.ESME_RALYBND);
        }

        BindRequest bindRequest = (BindRequest) request;

        Authentication authentication = new UsernamePasswordAuthentication(session, bindRequest.getSystemId(),
                bindRequest.getPassword());
        User user;
        try {
            user = context.getUserManager().authenticate(authentication);
            session.setUser(user);

        } catch (AuthenticationFailedException e) {
            return new SmscStatusReplyImpl(request, SmscReply.ErrorCode.ESME_RBINDFAIL);
        }

        return new SmscBindReplyImpl(request, bindRequest.getSystemId());
    }
}
