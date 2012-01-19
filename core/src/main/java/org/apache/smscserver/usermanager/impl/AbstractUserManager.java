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

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.smscserver.ServerSmscStatistics;
import org.apache.smscserver.smsclet.Authentication;
import org.apache.smscserver.smsclet.AuthenticationFailedException;
import org.apache.smscserver.smsclet.SmscException;
import org.apache.smscserver.smsclet.SmscIoSession;
import org.apache.smscserver.smsclet.SmscStatistics;
import org.apache.smscserver.smsclet.SmscletContext;
import org.apache.smscserver.smsclet.User;
import org.apache.smscserver.smsclet.UserManager;
import org.apache.smscserver.usermanager.Md5PasswordEncryptor;
import org.apache.smscserver.usermanager.PasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * Abstract common base type for {@link UserManager} implementations
 * 
 * @author hceylan
 */
public abstract class AbstractUserManager implements UserManager {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesUserManager.class);

    public static final String ATTR_SYSTEM_ID = "systemid";

    public static final String ATTR_PASSWORD = "userpassword";

    public static final String ATTR_ENABLE = "enableflag";

    public static final String ATTR_MAX_IDLE_TIME = "idletime";

    public static final String ATTR_MAX_BIND_NUMBER = "maxbindnumber";

    public static final String ATTR_MAX_BIND_PER_IP = "maxbindperip";

    private final String adminName;

    private final PasswordEncryptor passwordEncryptor;

    protected SmscletContext context;

    public AbstractUserManager() {
        this(null, new Md5PasswordEncryptor());
    }

    /**
     * Internal constructor, do not use directly
     */
    public AbstractUserManager(String adminName, PasswordEncryptor passwordEncryptor) {
        this.adminName = adminName;
        this.passwordEncryptor = passwordEncryptor;
    }

    /**
     * User authenticate method
     * 
     * @throws SmscException
     */
    public final User authenticate(Authentication authentication) throws SmscException {
        try {
            User internalAuthenticate = this.internalAuthenticate(authentication);
            return internalAuthenticate;
        } catch (AuthenticationFailedException e) {
            if (this.context == null) {
                AbstractUserManager.LOG.warn("Context is null bind statistics will not be updated!");
            } else {
                ServerSmscStatistics stat = (ServerSmscStatistics) this.context.getSmscStatistics();
                stat.setBindFail(authentication.getSession());
            }

            throw e;
        }
    }

    protected void authorizeConcurency(Authentication authentication, User user) throws AuthenticationFailedException {
        if (!user.getEnabled()) {
            throw new AuthenticationFailedException("User account is disabled by administrator");
        }

        // user bind limit check
        InetAddress address = null;
        SmscIoSession session = authentication.getSession();
        if ((session != null) && (this.context != null)) {
            if (session.getRemoteAddress() instanceof InetSocketAddress) {
                address = ((InetSocketAddress) session.getRemoteAddress()).getAddress();
            }

            SmscStatistics stats = this.context.getSmscStatistics();
            ConcurrentBindRequest request = new ConcurrentBindRequest(stats.getCurrentUserBindNumber(user) + 1,
                    stats.getCurrentUserBindNumber(user, address) + 1);

            if (user.authorize(request) == null) {
                AbstractUserManager.LOG.info("User logged in too many sessions, user will be disconnected");
                throw new AuthenticationFailedException("Too many sessions");
            }
        } else {
            AbstractUserManager.LOG.warn("Session or context is null. Concurrent bind status will not authorized!");
        }
    }

    /**
     * Get the admin name.
     */
    public String getAdminName() {
        return this.adminName;
    }

    /**
     * Retrieve the password encryptor used for this user manager
     * 
     * @return The password encryptor. Default to {@link Md5PasswordEncryptor} if no other has been provided
     */
    public PasswordEncryptor getPasswordEncryptor() {
        return this.passwordEncryptor;
    }

    protected abstract User internalAuthenticate(Authentication authentication) throws AuthenticationFailedException,
            SmscException;

    /**
     * @return true if user with this bind is administrator
     */
    public boolean isAdmin(String bind) throws SmscException {
        return this.adminName.equals(bind);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void setContext(SmscletContext context) {
        if (context == null) {
            throw new NullPointerException("Context is null");
        }

        if (this.context != null) {
            throw new IllegalStateException("Context is already assigned");
        }

        this.context = context;
    }
}
