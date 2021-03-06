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
package org.apache.smscserver.smsclet;

import java.net.SocketAddress;

import org.apache.mina.core.session.IoSession;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public interface SmscIoSession extends IoSession {

    final String ATTRIBUTE_PREFIX = "org.apache.smscserver.";

    /**
     * Returns the next sequence number the server should use for sending requests to client.
     * 
     * @return the next sequence number
     */
    int getNextSequnce();

    /**
     * Returns the socket address of remote peer.
     * 
     * @return the remote socket address
     */
    SocketAddress getRemoteAddress();

    /**
     * Returns the user associates with the session.
     * 
     * @return the user associates with the session
     */
    User getUser();

    /**
     * Locks the session for a conversation
     * 
     * @return true if lock is successful
     * 
     */
    boolean lock();

    /**
     * Called upon sucessful bind.
     * <p>
     * Starts the delivery for the client.
     */
    void startDelivery();

    /**
     * Called upon sucessful bind.
     * <p>
     * Starts the delivery for the client.
     */
    void stopDelivery();

    /**
     * unlocks the session
     */
    void unlock();
}
