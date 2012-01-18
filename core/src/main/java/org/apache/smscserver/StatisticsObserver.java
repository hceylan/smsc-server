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

import java.net.InetAddress;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * SMSC statistics observer interface.
 * 
 * @author hceylan
 */
public interface StatisticsObserver {

    /**
     * New user bind notification.
     */
    void notifyBind();

    /**
     * Failed user bind notification.
     * 
     * @param address
     *            Remote address that the failure came from
     */
    void notifyBindFail(InetAddress address);

    /**
     * Connection close notification
     */
    void notifyCloseConnection();

    /**
     * User message received notification.
     */
    void notifyMessageReceived();

    /**
     * User message sent notification.
     */
    void notifyMessageSent();

    /**
     * Connection open notification
     */
    void notifyOpenConnection();

    /**
     * User unbind notification.
     */
    void notifyUnbind();

}
