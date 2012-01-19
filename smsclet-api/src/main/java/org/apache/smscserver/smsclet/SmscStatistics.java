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

package org.apache.smscserver.smsclet;

import java.net.InetAddress;
import java.util.Date;

/**
 * This interface holds all the smsc server statistical information.
 * 
 * @author hceylan
 */
public interface SmscStatistics {

    /**
     * Get current bind number
     * 
     * @return The current number of binds
     */
    int getCurrentBindNumber();

    /**
     * Get current number of connections.
     * 
     * @return The current number of connections
     */
    int getCurrentConnectionNumber();

    /**
     * Get the bind number for the specific user
     * 
     * @param user
     *            The {@link User} for which to retrieve the number of binds
     * @return The total number of binds for the provided user
     */
    int getCurrentUserBindNumber(User user);

    /**
     * Get the bind number for the specific user from the ipAddress
     * 
     * @param user
     *            bind user account
     * @param ipAddress
     *            the ip address of the remote user
     * @return The total number of bind for the provided user and IP address
     */
    int getCurrentUserBindNumber(User user, InetAddress ipAddress);

    /**
     * Get the server start time.
     * 
     * @return The {@link Date} when the server started
     */
    Date getStartTime();

    /**
     * Get total bind number.
     * 
     * @return The total number of binds
     */
    int getTotalBindNumber();

    /**
     * Get total number of connections
     * 
     * @return The total number of connections
     */
    int getTotalConnectionNumber();

    /**
     * Get total failed bind number.
     * 
     * @return The total number of failed binds
     */
    int getTotalFailedBindNumber();

    /**
     * Get number of message received.
     * 
     * @return The total number of messages received
     */
    int getTotalMessageReceivedNumber();

    /**
     * Get number of message sent.
     * 
     * @return The total number of messages sent
     */
    int getTotalMessageSentNumber();
}
