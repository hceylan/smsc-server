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

import org.apache.smscserver.impl.DefaultConnectionConfig;

/**
 * Factory for creating connection configurations
 * 
 * @author hceylan
 * 
 */
public class ConnectionConfigFactory {

    private int maxBinds = 10;
    private int maxBindFailures = 3;
    private int bindFailureDelay = 500;
    private int maxThreads = 8;
    private int minThreads = 2;
    private int maxDeliveryThreads = 1;
    private int minDeliveryThreads = 4;

    /**
     * Create a connection configuration instances based on the configuration on this factory
     * 
     * @return The {@link ConnectionConfig} instance
     */
    public ConnectionConfig createConnectionConfig() {
        return new DefaultConnectionConfig(this.bindFailureDelay, this.maxBinds, this.maxBindFailures, this.minThreads,
                this.maxThreads, this.maxDeliveryThreads, this.minDeliveryThreads);
    }

    /**
     * The delay in number of milliseconds between bind failures. Important to make brute force attacks harder.
     * 
     * @return The delay time in milliseconds
     */
    public int getBindFailureDelay() {
        return this.bindFailureDelay;
    }

    /**
     * The maximum number of time an user can fail to bind before getting disconnected
     * 
     * @return The maximum number of failure bind attempts
     */
    public int getMaxBindFailures() {
        return this.maxBindFailures;
    }

    /**
     * The maximum number of concurrently bound users
     * 
     * @return The maximum number of users
     */
    public int getMaxBinds() {
        return this.maxBinds;
    }

    /**
     * @return the maxDeliveryThreads
     */
    public int getMaxDeliveryThreads() {
        return this.maxDeliveryThreads;
    }

    /**
     * Returns the maximum number of threads the server is allowed to create for processing client requests.
     * 
     * @return the maximum number of threads the server is allowed to create for processing client requests.
     */
    public int getMaxThreads() {
        return this.maxThreads;
    }

    /**
     * @return the minDeliveryThreads
     */
    public int getMinDeliveryThreads() {
        return this.minDeliveryThreads;
    }

    /**
     * @return the minThreads
     */
    public int getMinThreads() {
        return this.minThreads;
    }

    /**
     * Set the delay in number of milliseconds between bind failures. Important to make brute force attacks harder.
     * 
     * @param bindFailureDelay
     *            The delay time in milliseconds
     */
    public void setBindFailureDelay(final int bindFailureDelay) {
        this.bindFailureDelay = bindFailureDelay;
    }

    /**
     * Set the maximum number of time an user can fail to bind before getting disconnected
     * 
     * @param maxBindFailures
     *            The maximum number of failure bind attempts
     */
    public void setMaxBindFailures(final int maxBindFailures) {
        this.maxBindFailures = maxBindFailures;
    }

    /**
     * Set she maximum number of concurrently logged in users
     * 
     * @param maxBinds
     *            The maximum number of users
     */

    public void setMaxBinds(final int maxBinds) {
        this.maxBinds = maxBinds;
    }

    /**
     * @param maxDeliveryThreads
     *            the maxDeliveryThreads to set
     */
    public void setMaxDeliveryThreads(int maxDeliveryThreads) {
        this.maxDeliveryThreads = maxDeliveryThreads;
    }

    /**
     * Sets the maximum number of threads the server is allowed to create for processing client requests.
     * 
     * @param maxThreads
     *            the maximum number of threads the server is allowed to create for processing client requests
     */
    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    /**
     * @param minDeliveryThreads
     *            the minDeliveryThreads to set
     */
    public void setMinDeliveryThreads(int minDeliveryThreads) {
        this.minDeliveryThreads = minDeliveryThreads;
    }

    /**
     * Sets the minimum number of threads the server is required to keep for processing client requests.
     * 
     * @param maxThreads
     *            the minimum number of threads the server is required to keep for processing client requests.
     */
    public void setMinThreads(int minThreads) {
        this.minThreads = minThreads;
    }
}
