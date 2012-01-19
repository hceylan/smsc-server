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

package org.apache.smscserver.usermanager.impl;

import org.apache.smscserver.smsclet.AuthorizationRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * Class representing a request to log in a number of concurrent times
 * 
 * @author hceylan
 */
public class ConcurrentBindRequest implements AuthorizationRequest {

    private final int concurrentBinds;

    private final int concurrentBindsFromThisIP;

    private int maxConcurrentBinds = 0;

    private int maxConcurrentBindsPerIP = 0;

    /**
     * @param concurrentBinds
     * @param concurrentBindsFromThisIP
     */
    public ConcurrentBindRequest(int concurrentBinds, int concurrentBindsFromThisIP) {
        super();
        this.concurrentBinds = concurrentBinds;
        this.concurrentBindsFromThisIP = concurrentBindsFromThisIP;
    }

    /**
     * The number of concurrent binds requested
     * 
     * @return the concurrentBinds The number of current concurrent binds
     */
    public int getConcurrentBinds() {
        return this.concurrentBinds;
    }

    /**
     * The number of concurrent binds from this IP requested
     * 
     * @return the concurrentBindsFromThisIP The number of current concurrent binds from this IP
     */
    public int getConcurrentBindsFromThisIP() {
        return this.concurrentBindsFromThisIP;
    }

    /**
     * The maximum allowed concurrent bin for this user, or 0 if no limit is set. This is normally populated by
     * {@link ConcurrentBindPermission}
     * 
     * @return The maximum allowed concurrent binds
     */
    public int getMaxConcurrentBinds() {
        return this.maxConcurrentBinds;
    }

    /**
     * The maximum allowed concurrent bin per IP for this user, or 0 if no limit is set. This is normally populated by
     * {@link ConcurrentBindPermission}
     * 
     * @return The maximum allowed concurrent binds per IP
     */
    public int getMaxConcurrentBindsPerIP() {
        return this.maxConcurrentBindsPerIP;
    }

    /**
     * Set the maximum allowed concurrent binds for this user
     * 
     * @param maxConcurrentBinds
     *            Set max allowed concurrent connections
     */
    void setMaxConcurrentBinds(int maxConcurrentBinds) {
        this.maxConcurrentBinds = maxConcurrentBinds;
    }

    /**
     * Set the maximum allowed concurrent binds per IP for this user
     * 
     * @param maxConcurrentBindsPerIP
     *            Set max allowed concurrent connections per IP
     */
    void setMaxConcurrentBindsPerIP(int maxConcurrentBindsPerIP) {
        this.maxConcurrentBindsPerIP = maxConcurrentBindsPerIP;
    }
}
