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

package org.apache.smscserver.command;

import java.io.IOException;

import org.apache.smscserver.impl.SmscIoSession;
import org.apache.smscserver.impl.SmscServerContext;
import org.apache.smscserver.smsclet.SmscException;
import org.apache.smscserver.smsclet.SmscReply;
import org.apache.smscserver.smsclet.SmscRequest;

/**
 * This interface encapsulates all the SMSC commands.
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public interface Command {

    /**
     * Execute command.
     * 
     * @param session
     *            The current {@link SmscIoSession}
     * @param context
     *            The current {@link SmscServerContext}
     * @param request
     *            The current {@link SmscRequest}
     * @return the reply to be sent to client
     * @throws IOException
     * @throws SmscException
     */
    SmscReply execute(SmscIoSession session, SmscServerContext context, SmscRequest request) throws IOException,
            SmscException;

}
