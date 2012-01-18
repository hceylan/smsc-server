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

    private final int concurrentLogins;

    private final int concurrentLoginsFromThisIP;

    private int maxConcurrentLogins = 0;

    private int maxConcurrentLoginsPerIP = 0;

    /**
     * @param concurrentLogins
     * @param concurrentLoginsFromThisIP
     */
    public ConcurrentBindRequest(int concurrentLogins, int concurrentLoginsFromThisIP) {
        super();
        this.concurrentLogins = concurrentLogins;
        this.concurrentLoginsFromThisIP = concurrentLoginsFromThisIP;
    }

    /**
     * The number of concurrent logins requested
     * 
     * @return the concurrentLogins The number of current concurrent logins
     */
    public int getConcurrentLogins() {
        return this.concurrentLogins;
    }

    /**
     * The number of concurrent logins from this IP requested
     * 
     * @return the concurrentLoginsFromThisIP The number of current concurrent logins from this IP
     */
    public int getConcurrentLoginsFromThisIP() {
        return this.concurrentLoginsFromThisIP;
    }

    /**
     * The maximum allowed concurrent bin for this user, or 0 if no limit is set. This is normally populated by
     * {@link ConcurrentBindPermission}
     * 
     * @return The maximum allowed concurrent logins
     */
    public int getMaxConcurrentBinds() {
        return this.maxConcurrentLogins;
    }

    /**
     * The maximum allowed concurrent bin per IP for this user, or 0 if no limit is set. This is normally populated by
     * {@link ConcurrentBindPermission}
     * 
     * @return The maximum allowed concurrent logins per IP
     */
    public int getMaxConcurrentBindsPerIP() {
        return this.maxConcurrentLoginsPerIP;
    }

    /**
     * Set the maximum allowed concurrent binds for this user
     * 
     * @param maxConcurrentLogins
     *            Set max allowed concurrent connections
     */
    void setMaxConcurrentBinds(int maxConcurrentLogins) {
        this.maxConcurrentLogins = maxConcurrentLogins;
    }

    /**
     * Set the maximum allowed concurrent binds per IP for this user
     * 
     * @param maxConcurrentLoginsPerIP
     *            Set max allowed concurrent connections per IP
     */
    void setMaxConcurrentBindsPerIP(int maxConcurrentLoginsPerIP) {
        this.maxConcurrentLoginsPerIP = maxConcurrentLoginsPerIP;
    }
}
