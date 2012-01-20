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

package org.apache.smscserver.impl;

import org.apache.smscserver.ConnectionConfig;
import org.apache.smscserver.ConnectionConfigFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 * 
 */
public class DefaultConnectionConfig implements ConnectionConfig {

    private final int maxBinds;

    private final int maxBindFailures;

    private final int bindFailureDelay;

    private final int maxThreads;

    private final int minThreads;

    private final int maxDeliveryThreads;

    private final int minDeliveryThreads;

    public DefaultConnectionConfig() {
        this(500, 10, 3, 2, 8, 1, 4);
    }

    /**
     * Internal constructor, do not use directly. Use {@link ConnectionConfigFactory} instead
     */
    public DefaultConnectionConfig(int nindFailureDelay, int maxBinds, int maxBindFailures, int minThreads,
            int maxThreads, int maxDeliveryThreads, int minDeliveryThreads) {
        this.bindFailureDelay = nindFailureDelay;
        this.maxBinds = maxBinds;
        this.maxBindFailures = maxBindFailures;
        this.minThreads = minThreads;
        this.maxThreads = maxThreads;
        this.maxDeliveryThreads = maxDeliveryThreads;
        this.minDeliveryThreads = minDeliveryThreads;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getBindFailureDelay() {
        return this.bindFailureDelay;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getMaxBindFailures() {
        return this.maxBindFailures;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getMaxBinds() {
        return this.maxBinds;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getMaxDeliveryThreads() {
        return this.maxDeliveryThreads;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getMaxThreads() {
        return this.maxThreads;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getMinDeliveryThreads() {
        return this.minDeliveryThreads;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getMinThreads() {
        return this.minThreads;
    }

}
