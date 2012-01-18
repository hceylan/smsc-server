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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.smscserver.smsclet.Authority;
import org.apache.smscserver.smsclet.AuthorizationRequest;
import org.apache.smscserver.smsclet.User;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * Generic user class. The user attributes are:
 * <ul>
 * <li>userid</li>
 * <li>userpassword</li>
 * <li>enableflag</li>
 * <li>homedirectory</li>
 * <li>writepermission</li>
 * <li>idletime</li>
 * <li>uploadrate</li>
 * <li>downloadrate</li>
 * </ul>
 * 
 * @author hceylan
 */

public class BaseUser implements User {

    private String name = null;

    private String password = null;

    private int maxIdleTimeSec = 0; // no limit

    private boolean isEnabled = true;

    private List<? extends Authority> authorities = new ArrayList<Authority>();

    /**
     * Default constructor.
     */
    public BaseUser() {
    }

    /**
     * Copy constructor.
     */
    public BaseUser(User user) {
        this.name = user.getName();
        this.password = user.getPassword();
        this.authorities = user.getAuthorities();
        this.maxIdleTimeSec = user.getMaxIdleTime();
        this.isEnabled = user.getEnabled();
    }

    /**
     * {@inheritDoc}
     */
    public AuthorizationRequest authorize(AuthorizationRequest request) {
        // check for no authorities at all
        if (this.authorities == null) {
            return null;
        }

        boolean someoneCouldAuthorize = false;
        for (Authority authority : this.authorities) {
            if (authority.canAuthorize(request)) {
                someoneCouldAuthorize = true;

                request = authority.authorize(request);

                // authorization failed, return null
                if (request == null) {
                    return null;
                }
            }

        }

        if (someoneCouldAuthorize) {
            return request;
        } else {
            return null;
        }
    }

    public List<Authority> getAuthorities() {
        if (this.authorities != null) {
            return Collections.unmodifiableList(this.authorities);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Authority> getAuthorities(Class<? extends Authority> clazz) {
        List<Authority> selected = new ArrayList<Authority>();

        for (Authority authority : this.authorities) {
            if (authority.getClass().equals(clazz)) {
                selected.add(authority);
            }
        }

        return selected;
    }

    /**
     * Get the user enable status.
     */
    public boolean getEnabled() {
        return this.isEnabled;
    }

    /**
     * Get the maximum idle time in second.
     */
    public int getMaxIdleTime() {
        return this.maxIdleTimeSec;
    }

    /**
     * Get the user name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the user password.
     */
    public String getPassword() {
        return this.password;
    }

    public void setAuthorities(List<Authority> authorities) {
        if (authorities != null) {
            this.authorities = Collections.unmodifiableList(authorities);
        } else {
            this.authorities = null;
        }
    }

    /**
     * Set the user enable status.
     */
    public void setEnabled(boolean enb) {
        this.isEnabled = enb;
    }

    /**
     * Set the maximum idle time in second.
     */
    public void setMaxIdleTime(int idleSec) {
        this.maxIdleTimeSec = idleSec;
        if (this.maxIdleTimeSec < 0) {
            this.maxIdleTimeSec = 0;
        }
    }

    /**
     * Set user name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set user password.
     */
    public void setPassword(String pass) {
        this.password = pass;
    }

    /**
     * String representation.
     */
    @Override
    public String toString() {
        return this.name;
    }
}
