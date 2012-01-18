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
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.smscserver.smsclet.Authentication;
import org.apache.smscserver.smsclet.AuthenticationFailedException;
import org.apache.smscserver.smsclet.Authority;
import org.apache.smscserver.smsclet.SmscIoSession;
import org.apache.smscserver.smsclet.User;
import org.apache.smscserver.smsclet.UserManager;
import org.apache.smscserver.usermanager.UserManagerFactory;
import org.apache.smscserver.usermanager.UsernamePasswordAuthentication;

/**
 * 
 * @author hceylan
 * 
 */
public abstract class UserManagerTestTemplate extends TestCase {

    public static class FooAuthentication implements Authentication {

        public SmscIoSession getSession() {
            return null;
        }
    }

    protected UserManager userManager;

    protected abstract UserManagerFactory createUserManagerFactory() throws Exception;

    private int getMaxLoginNumber(User user) {
        ConcurrentBindRequest concurrentLoginRequest = new ConcurrentBindRequest(0, 0);
        concurrentLoginRequest = (ConcurrentBindRequest) user.authorize(concurrentLoginRequest);

        if (concurrentLoginRequest != null) {
            return concurrentLoginRequest.getMaxConcurrentBinds();
        } else {
            return 0;
        }
    }

    private int getMaxLoginPerIP(User user) {
        ConcurrentBindRequest concurrentLoginRequest = new ConcurrentBindRequest(0, 0);
        concurrentLoginRequest = (ConcurrentBindRequest) user.authorize(concurrentLoginRequest);

        if (concurrentLoginRequest != null) {
            return concurrentLoginRequest.getMaxConcurrentBindsPerIP();
        } else {
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        this.userManager = this.createUserManagerFactory().createUserManager();
    }

    public void testAuthenticate() throws Exception {
        Assert.assertNotNull(this.userManager.authenticate(new UsernamePasswordAuthentication(null, "user1", "pw1")));
    }

    public void testAuthenticateEmptyPassword() throws Exception {
        Assert.assertNotNull(this.userManager.authenticate(new UsernamePasswordAuthentication(null, "user3", "")));
    }

    public void testAuthenticateNullPassword() throws Exception {
        Assert.assertNotNull(this.userManager.authenticate(new UsernamePasswordAuthentication(null, "user3", null)));
    }

    public void testAuthenticateNullUser() throws Exception {
        try {
            this.userManager.authenticate(new UsernamePasswordAuthentication(null, null, "foo"));
            Assert.fail("Must throw AuthenticationFailedException");
        } catch (AuthenticationFailedException e) {
            // ok
        }
    }

    public void testAuthenticateUnknownAuthentication() throws Exception {
        try {
            this.userManager.authenticate(new FooAuthentication());
            Assert.fail("Must throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    public void testAuthenticateUnknownUser() throws Exception {
        try {
            this.userManager.authenticate(new UsernamePasswordAuthentication(null, "foo", "foo"));
            Assert.fail("Must throw AuthenticationFailedException");
        } catch (AuthenticationFailedException e) {
            // ok
        }
    }

    public void testAuthenticateWrongPassword() throws Exception {
        try {
            this.userManager.authenticate(new UsernamePasswordAuthentication(null, "user1", "foo"));
            Assert.fail("Must throw AuthenticationFailedException");
        } catch (AuthenticationFailedException e) {
            // ok
        }
    }

    public void testDelete() throws Exception {
        Assert.assertTrue(this.userManager.doesExist("user1"));
        Assert.assertTrue(this.userManager.doesExist("user2"));
        this.userManager.delete("user1");
        Assert.assertFalse(this.userManager.doesExist("user1"));
        Assert.assertTrue(this.userManager.doesExist("user2"));
        this.userManager.delete("user2");
        Assert.assertFalse(this.userManager.doesExist("user1"));
        Assert.assertFalse(this.userManager.doesExist("user2"));
    }

    public void testDeleteNonExistingUser() throws Exception {
        // silent failure
        this.userManager.delete("foo");
    }

    public void testDoesExist() throws Exception {
        Assert.assertTrue(this.userManager.doesExist("user1"));
        Assert.assertTrue(this.userManager.doesExist("user2"));
        Assert.assertFalse(this.userManager.doesExist("foo"));
    }

    public void testGetAdminName() throws Exception {
        Assert.assertEquals("admin", this.userManager.getAdminName());
    }

    public void testGetUserByName() throws Exception {
        User user = this.userManager.getUserByName("user2");

        Assert.assertEquals("user2", user.getName());
        Assert.assertNull("Password must not be set", user.getPassword());
        Assert.assertEquals(2, user.getMaxIdleTime());
        Assert.assertEquals(3, this.getMaxLoginNumber(user));
        Assert.assertEquals(4, this.getMaxLoginPerIP(user));
        Assert.assertFalse(user.getEnabled());
    }

    public void testGetUserByNameWithDefaultValues() throws Exception {
        User user = this.userManager.getUserByName("user1");

        Assert.assertEquals("user1", user.getName());
        Assert.assertNull("Password must not be set", user.getPassword());
        Assert.assertEquals(0, user.getMaxIdleTime());
        Assert.assertEquals(0, this.getMaxLoginNumber(user));
        Assert.assertEquals(0, this.getMaxLoginPerIP(user));
        Assert.assertTrue(user.getEnabled());
    }

    public void testGetUserByNameWithUnknownUser() throws Exception {
        Assert.assertNull(this.userManager.getUserByName("foo"));
    }

    public void testIsAdmin() throws Exception {
        Assert.assertTrue(this.userManager.isAdmin("admin"));
        Assert.assertFalse(this.userManager.isAdmin("user1"));
        Assert.assertFalse(this.userManager.isAdmin("foo"));
    }

    public void testSave() throws Exception {
        BaseUser user = new BaseUser();
        user.setName("newuser");
        user.setPassword("newpw");
        user.setEnabled(false);
        user.setMaxIdleTime(2);

        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(new ConcurrentBindPermission(3, 4));
        user.setAuthorities(authorities);

        this.userManager.save(user);

        User actualUser = this.userManager.getUserByName("newuser");

        Assert.assertEquals(user.getName(), actualUser.getName());
        Assert.assertNull(actualUser.getPassword());
        Assert.assertEquals(user.getEnabled(), actualUser.getEnabled());
        Assert.assertEquals(user.getMaxIdleTime(), actualUser.getMaxIdleTime());
        Assert.assertEquals(this.getMaxLoginNumber(user), this.getMaxLoginNumber(actualUser));
        Assert.assertEquals(this.getMaxLoginPerIP(user), this.getMaxLoginPerIP(actualUser));

        // verify the password
        Assert.assertNotNull(this.userManager
                .authenticate(new UsernamePasswordAuthentication(null, "newuser", "newpw")));

        try {
            this.userManager.authenticate(new UsernamePasswordAuthentication(null, "newuser", "dummy"));
            Assert.fail("Must throw AuthenticationFailedException");
        } catch (AuthenticationFailedException e) {
            // ok
        }

        // save without updating the users password (password==null)
        this.userManager.save(user);

        Assert.assertNotNull(this.userManager
                .authenticate(new UsernamePasswordAuthentication(null, "newuser", "newpw")));
        try {
            this.userManager.authenticate(new UsernamePasswordAuthentication(null, "newuser", "dummy"));
            Assert.fail("Must throw AuthenticationFailedException");
        } catch (AuthenticationFailedException e) {
            // ok
        }

        // save and update the users password
        user.setPassword("newerpw");
        this.userManager.save(user);

        Assert.assertNotNull(this.userManager.authenticate(new UsernamePasswordAuthentication(null, "newuser",
                "newerpw")));

        try {
            this.userManager.authenticate(new UsernamePasswordAuthentication(null, "newuser", "newpw"));
            Assert.fail("Must throw AuthenticationFailedException");
        } catch (AuthenticationFailedException e) {
            // ok
        }

    }

    public void testSavePersistent() throws Exception {
        BaseUser user = new BaseUser();
        user.setName("newuser");
        user.setPassword("newpw");
        user.setEnabled(false);
        user.setMaxIdleTime(2);

        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(new ConcurrentBindPermission(3, 4));
        user.setAuthorities(authorities);

        this.userManager.save(user);

        UserManager newUserManager = this.createUserManagerFactory().createUserManager();

        User actualUser = newUserManager.getUserByName("newuser");

        Assert.assertEquals(user.getName(), actualUser.getName());
        Assert.assertNull(actualUser.getPassword());
        Assert.assertEquals(user.getEnabled(), actualUser.getEnabled());
        Assert.assertEquals(user.getMaxIdleTime(), actualUser.getMaxIdleTime());
        Assert.assertEquals(this.getMaxLoginNumber(user), this.getMaxLoginNumber(actualUser));
        Assert.assertEquals(this.getMaxLoginPerIP(user), this.getMaxLoginPerIP(actualUser));

        // verify the password
        Assert.assertNotNull(newUserManager.authenticate(new UsernamePasswordAuthentication(null, "newuser", "newpw")));

        try {
            newUserManager.authenticate(new UsernamePasswordAuthentication(null, "newuser", "dummy"));
            Assert.fail("Must throw AuthenticationFailedException");
        } catch (AuthenticationFailedException e) {
            // ok
        }

        // save without updating the users password (password==null)
        this.userManager.save(user);

        newUserManager = this.createUserManagerFactory().createUserManager();
        Assert.assertNotNull(newUserManager.authenticate(new UsernamePasswordAuthentication(null, "newuser", "newpw")));
        try {
            newUserManager.authenticate(new UsernamePasswordAuthentication(null, "newuser", "dummy"));
            Assert.fail("Must throw AuthenticationFailedException");
        } catch (AuthenticationFailedException e) {
            // ok
        }

        // save and update the users password
        user.setPassword("newerpw");
        this.userManager.save(user);

        newUserManager = this.createUserManagerFactory().createUserManager();
        Assert.assertNotNull(newUserManager
                .authenticate(new UsernamePasswordAuthentication(null, "newuser", "newerpw")));

        try {
            newUserManager.authenticate(new UsernamePasswordAuthentication(null, "newuser", "newpw"));
            Assert.fail("Must throw AuthenticationFailedException");
        } catch (AuthenticationFailedException e) {
            // ok
        }

    }

    public void testSaveWithDefaultValues() throws Exception {
        BaseUser user = new BaseUser();
        user.setName("newuser");
        user.setPassword("newpw");
        this.userManager.save(user);

        User actualUser = this.userManager.getUserByName("newuser");

        Assert.assertEquals(user.getName(), actualUser.getName());
        Assert.assertNull(actualUser.getPassword());
        Assert.assertEquals(true, actualUser.getEnabled());
        Assert.assertEquals(0, actualUser.getMaxIdleTime());
        Assert.assertEquals(0, this.getMaxLoginNumber(actualUser));
        Assert.assertEquals(0, this.getMaxLoginPerIP(actualUser));
    }

    public void testSaveWithExistingUser() throws Exception {
        BaseUser user = new BaseUser();
        user.setName("user2");
        this.userManager.save(user);

        User actualUser = this.userManager.getUserByName("user2");

        Assert.assertEquals("user2", actualUser.getName());
        Assert.assertNull(actualUser.getPassword());
        Assert.assertEquals(0, actualUser.getMaxIdleTime());
        Assert.assertEquals(0, this.getMaxLoginNumber(actualUser));
        Assert.assertEquals(0, this.getMaxLoginPerIP(actualUser));
        Assert.assertTrue(actualUser.getEnabled());
    }
}
