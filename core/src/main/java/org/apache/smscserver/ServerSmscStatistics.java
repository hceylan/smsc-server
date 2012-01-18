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

import org.apache.smscserver.smsclet.SmscIoSession;
import org.apache.smscserver.smsclet.SmscReply;
import org.apache.smscserver.smsclet.SmscRequest;
import org.apache.smscserver.smsclet.SmscStatistics;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * This is same as <code>org.apache.smscserver.smsclet.SmscStatistics</code> with added observer and setting values
 * functionalities.
 * 
 * @author hceylan
 */
public interface ServerSmscStatistics extends SmscStatistics {

    /**
     * Reset all cumulative total counters. Do not reset current counters, like current logins, otherwise these will
     * become negative when someone disconnects.
     */
    void resetStatisticsCounters();

    /**
     * Increment current bind count.
     */
    void setBind(SmscIoSession smscIoSession);

    /**
     * Increment failed Bind count.
     */
    void setBindFail(SmscIoSession smscIoSession);

    /**
     * Decrement close connection count.
     */
    void setCloseConnection(SmscIoSession session);

    /**
     * Set message observer.
     */
    void setMessageObserver(MessageObserver observer);

    /**
     * Increment message received count.
     */
    void setMessageReceived(SmscIoSession session, SmscRequest request);

    /**
     * Increment message sent download count.
     */
    void setMessageSent(SmscIoSession session, SmscReply reply);

    /**
     * Set statistics observer.
     */
    void setObserver(StatisticsObserver observer);

    /**
     * Increment current connection count.
     */
    void setOpenConnection(SmscIoSession session);

    /**
     * Decrement current bind count.
     */
    void setUnbind(SmscIoSession session);
}
