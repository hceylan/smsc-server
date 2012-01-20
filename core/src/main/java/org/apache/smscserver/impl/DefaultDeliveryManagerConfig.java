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

import org.apache.smscserver.ConnectionConfigFactory;
import org.apache.smscserver.DeliveryManagerConfig;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 * 
 */
public class DefaultDeliveryManagerConfig implements DeliveryManagerConfig {

    private final int managerThreads;
    private final int maxThreads;
    private final int minThreads;

    public DefaultDeliveryManagerConfig() {
        this(2, 8, 2);
    }

    /**
     * Internal constructor, do not use directly. Use {@link ConnectionConfigFactory} instead
     */
    public DefaultDeliveryManagerConfig(int managerThreads, int minThreads, int maxThreads) {
        this.managerThreads = managerThreads;
        this.minThreads = minThreads;
        this.maxThreads = maxThreads;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getManagerThreads() {
        return this.managerThreads;
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
    public int getMinThreads() {
        return this.minThreads;
    }

}
