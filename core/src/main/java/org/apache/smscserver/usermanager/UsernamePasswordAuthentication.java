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

package org.apache.smscserver.usermanager;

import org.apache.smscserver.smsclet.Authentication;
import org.apache.smscserver.smsclet.SmscIoSession;
import org.apache.smscserver.usermanager.impl.UserMetadata;

/**
 * Class representing a normal authentication attempt using username and password
 * 
 * @author hceylan
 */
public class UsernamePasswordAuthentication implements Authentication {

    private final String username;

    private final String password;

    private UserMetadata userMetadata;

    private final SmscIoSession session;

    /**
     * Constructor with the minimal data for an authentication
     * 
     * @param username
     *            The user name
     * @param password
     *            The password, can be null
     */
    public UsernamePasswordAuthentication(SmscIoSession session, final String username, final String password) {
        this.session = session;
        this.username = username;
        this.password = password;
    }

    /**
     * Constructor with an additonal parameter for user metadata
     * 
     * @param username
     *            The user name
     * @param password
     *            The password, can be null
     * @param userMetadata
     *            The user metadata
     */
    public UsernamePasswordAuthentication(SmscIoSession session, final String username, final String password,
            final UserMetadata userMetadata) {
        this(session, username, password);
        this.userMetadata = userMetadata;
    }

    /**
     * Retrive the password
     * 
     * @return The password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public SmscIoSession getSession() {
        return this.session;
    }

    /**
     * Retrive the user metadata
     * 
     * @return The user metadata
     */
    public UserMetadata getUserMetadata() {
        return this.userMetadata;
    }

    /**
     * Retrive the user name
     * 
     * @return The user name
     */
    public String getUsername() {
        return this.username;
    }
}
