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

import java.util.ArrayList;
import java.util.List;

import org.apache.smscserver.smsclet.Authority;
import org.apache.smscserver.smsclet.User;
import org.apache.smscserver.usermanager.impl.BaseUser;

/**
 * Factory for {@link User} instances.
 * 
 * @author hceylan
 */
public class UserFactory {

    private String name = null;

    private String password = null;

    private int maxIdleTimeSec = 0; // no limit

    private boolean isEnabled = true;

    private List<Authority> authorities = new ArrayList<Authority>();

    /**
     * Creates a user based on the configuration set on the factory
     * 
     * @return The created user
     */
    public User createUser() {
        BaseUser user = new BaseUser();
        user.setName(this.name);
        user.setPassword(this.password);
        user.setEnabled(this.isEnabled);
        user.setAuthorities(this.authorities);
        user.setMaxIdleTime(this.maxIdleTimeSec);

        return user;
    }

    /**
     * Get the authorities for users created by this factory
     * 
     * @return The authorities
     */
    public List<? extends Authority> getAuthorities() {
        return this.authorities;
    }

    /**
     * Get the max idle time for users created by this factory
     * 
     * @return The max idle time in seconds
     */
    public int getMaxIdleTime() {
        return this.maxIdleTimeSec;
    }

    /**
     * Get the user name for users created by this factory
     * 
     * @return The user name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the password for users created by this factory
     * 
     * @return The password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Get the enabled status for users created by this factory
     * 
     * @return true if the user is enabled (allowed to log in)
     */
    public boolean isEnabled() {
        return this.isEnabled;
    }

    /**
     * Set the authorities for users created by this factory
     * 
     * @param authorities
     *            The authorities
     */
    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    /**
     * Get the enabled status for users created by this factory
     * 
     * @param isEnabled
     *            true if the user should be enabled (allowed to log in)
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * Set the user name for users created by this factory
     * 
     * @param maxIdleTimeSec
     *            The max idle time in seconds
     */
    public void setMaxIdleTime(int maxIdleTimeSec) {
        this.maxIdleTimeSec = maxIdleTimeSec;
    }

    /**
     * Set the user name for users created by this factory
     * 
     * @param name
     *            The user name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the user name for users created by this factory
     * 
     * @param password
     *            The password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}