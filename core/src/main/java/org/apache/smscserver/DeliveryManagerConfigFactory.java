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

import org.apache.smscserver.impl.DefaultDeliveryManagerConfig;

/**
 * Factory for creating connection configurations
 * 
 * @author hceylan
 * 
 */
public class DeliveryManagerConfigFactory {

    private int managerThreads = 2;
    private int maxThreads = 8;
    private int minThreads = 2;
    private long[] periods;
    private int deliveryPollTime;

    /**
     * Create a connection configuration instances based on the configuration on this factory
     * 
     * @return The {@link DeliveryManagerConfig} instance
     */
    public DeliveryManagerConfig createDeliveryManagerConfig() {
        return new DefaultDeliveryManagerConfig(this.managerThreads, this.minThreads, this.maxThreads, this.periods,
                this.deliveryPollTime);
    }

    /**
     * Sets the time in seconds, how long message poller should wait for next message poll.
     * 
     * @param deliveryPollTime
     *            the time in seconds
     */
    public void setDeliveryPollTime(int deliveryPollTime) {
        this.deliveryPollTime = deliveryPollTime;
    }

    /**
     * Sets the delivery retry periods.
     * <p>
     * Delivery retry periods must be in the form of array of seconds i.e.: <code>60, 3600, 86400, 604800</code>
     * 
     * @param attribute
     */
    public void setDeliveryRetryPeriods(String attribute) {
        String[] periodsStrs = attribute.split(",");

        this.periods = new long[periodsStrs.length];
        int i = 0;
        for (String periodStr : periodsStrs) {
            try {
                this.periods[i++] = Long.parseLong(periodStr.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Delivery periods setting is invalid! Must be in the form of i.e.: 60, 3600, 86400, 604800");
            }
        }
    }

    /**
     * Sets the number of threads the server to create for managing the delivery.
     * 
     * @param maxThreads
     *            the maximum number of threads the server to create for managing delivery
     */
    public void setManagerThreads(int managerThreads) {
        this.managerThreads = managerThreads;
    }

    /**
     * Sets the maximum number of threads the server is allowed to create for processing delivery.
     * 
     * @param maxThreads
     *            the maximum number of threads the server is allowed to create for processing delivery
     */
    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    /**
     * Sets the minimum number of threads the server is allowed to create for processing delivery.
     * 
     * @param minThreads
     *            the minimum number of threads the server is allowed to create for processing delivery
     */
    public void setMinThreads(int minThreads) {
        this.minThreads = minThreads;
    }

}
