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

package org.apache.smscserver.test.usermanager.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.smscserver.smsclet.Authority;
import org.apache.smscserver.smsclet.SmscException;
import org.apache.smscserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.smscserver.usermanager.PropertiesUserManagerFactory;
import org.apache.smscserver.usermanager.UserManagerFactory;
import org.apache.smscserver.usermanager.impl.BaseUser;
import org.apache.smscserver.usermanager.impl.ConcurrentBindPermission;

/**
 * 
 * @author hceylan
 * 
 */
public class VolatilePropertiesUserManagerTest extends UserManagerTestTemplate {

    @Override
    protected UserManagerFactory createUserManagerFactory() throws SmscException {
        PropertiesUserManagerFactory um = new PropertiesUserManagerFactory();

        // set to null should make the user manager volatile, e.g. not use a file
        um.setFile(null);
        um.setPasswordEncryptor(new ClearTextPasswordEncryptor());

        return um;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        BaseUser user1 = new BaseUser();
        user1.setName("user1");
        user1.setPassword("pw1");

        this.userManager.save(user1);

        BaseUser user2 = new BaseUser();
        user2.setName("user2");
        user2.setPassword("pw2");
        user2.setEnabled(false);
        user2.setMaxIdleTime(2);

        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(new ConcurrentBindPermission(3, 4));

        user2.setAuthorities(authorities);

        this.userManager.save(user2);

        BaseUser user3 = new BaseUser();
        user3.setName("user3");
        user3.setPassword("");

        this.userManager.save(user3);

    }

    // we do not save persistent in this case so this test is disabled
    @Override
    public void testSavePersistent() {

    }
}
