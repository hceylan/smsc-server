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

import org.apache.smscserver.smsclet.Authority;
import org.apache.smscserver.smsclet.AuthorizationRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * The max upload rate permission
 * 
 * @author hceylan
 */
public class ConcurrentBindPermission implements Authority {

    private final int maxConcurrentBinds;

    private final int maxConcurrentBindsPerIP;

    public ConcurrentBindPermission(int maxConcurrentBinds, int maxConcurrentBindsPerIP) {
        this.maxConcurrentBinds = maxConcurrentBinds;
        this.maxConcurrentBindsPerIP = maxConcurrentBindsPerIP;
    }

    /**
     * @see Authority#authorize(AuthorizationRequest)
     */
    public AuthorizationRequest authorize(AuthorizationRequest request) {
        if (request instanceof ConcurrentBindRequest) {
            ConcurrentBindRequest concurrentBindRequest = (ConcurrentBindRequest) request;

            if ((this.maxConcurrentBinds != 0)
                    && (this.maxConcurrentBinds < concurrentBindRequest.getConcurrentBinds())) {
                return null;
            } else if ((this.maxConcurrentBindsPerIP != 0)
                    && (this.maxConcurrentBindsPerIP < concurrentBindRequest.getConcurrentBindsFromThisIP())) {
                return null;
            } else {
                concurrentBindRequest.setMaxConcurrentBinds(this.maxConcurrentBinds);
                concurrentBindRequest.setMaxConcurrentBindsPerIP(this.maxConcurrentBindsPerIP);

                return concurrentBindRequest;
            }
        } else {
            return null;
        }
    }

    /**
     * @see Authority#canAuthorize(AuthorizationRequest)
     */
    public boolean canAuthorize(AuthorizationRequest request) {
        return request instanceof ConcurrentBindRequest;
    }
}
