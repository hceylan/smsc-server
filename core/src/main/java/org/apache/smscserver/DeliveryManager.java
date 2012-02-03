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
package org.apache.smscserver;

import org.apache.smscserver.smsclet.SmscIoSession;

/**
 * Interface for Delivery Manager implementations
 * 
 * @version $Rev$ $Date$
 */
public interface DeliveryManager {

    /**
     * Signals the session has been unbound
     * 
     * @param ioSession
     */
    void closeBoundSession(SmscIoSession ioSession);

    /**
     * Destroys the delivery manager
     */
    void destroy();

    /**
     * @return
     */
    long[] getDeliveryPeriods();

    /**
     * Signals a session has been bound
     * 
     * @param ioSession
     */
    void newBoundSession(SmscIoSession ioSession);

    /**
     * Resumes the delivery manager
     */
    void resume();

    /**
     * Starts the delivery manager operation.
     */
    void start();

    /**
     * Suspends the delivery manager.
     */
    void suspend();
}
