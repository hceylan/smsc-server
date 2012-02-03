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

/**
 * Interface for providing the configuration for the control socket connections.
 * 
 * 
 * @author hceylan
 * 
 */
public interface DeliveryManagerConfig {

    /**
     * Returns the delivery periods.
     * <p>
     * Delivery periods ued to reschedule next delivery trial based on an error.
     * 
     * @return the delivery periods
     */
    long[] getDeliveryPeriods();

    /**
     * Returns the time in seconds, how long message poller should wait for next message poll.
     * 
     * @return the time in seconds, how long message poller should wait for next message poll.
     */
    int getDeliveryPollTime();

    /**
     * Returns the number of threads the server to create for mnaging delivery.
     * 
     * @return the number of threads the server to create for mnaging delivery
     */
    int getManagerThreads();

    /**
     * Returns the maximum number of threads the server is allowed to create for processing client requests.
     * 
     * @return the maximum number of threads the server is allowed to create for processing client requests
     */
    int getMaxThreads();

    /**
     * Returns the minimum number of threads the server is required tkeep for processing client requests.
     * 
     * @return the minimum number of threads the server is required tkeep for processing client requests
     */
    int getMinThreads();

}
