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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.smscserver.smsclet.SmscException;
import org.apache.smscserver.smsclet.User;
import org.apache.smscserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.smscserver.usermanager.PropertiesUserManagerFactory;
import org.apache.smscserver.usermanager.UserManagerFactory;
import org.apache.smscserver.usermanager.impl.PropertiesUserManager;
import org.apache.smscserver.util.IoUtils;

/**
 * 
 * @author hceylan
 * 
 */
public class PropertiesUserManagerTest extends UserManagerTestTemplate {

    private static final File TEST_DIR = new File("test-tmp");

    private static final File USERS_FILE = new File(PropertiesUserManagerTest.TEST_DIR, "users.props");

    private void createUserFile() throws IOException {
        Properties users = new Properties();
        users.setProperty("smscserver.user.user1.userpassword", "pw1");

        users.setProperty("smscserver.user.user2.userpassword", "pw2");
        users.setProperty("smscserver.user.user2.enableflag", "false");
        users.setProperty("smscserver.user.user2.idletime", "2");
        users.setProperty("smscserver.user.user2.maxloginnumber", "3");
        users.setProperty("smscserver.user.user2.maxloginperip", "4");

        users.setProperty("smscserver.user.user3.userpassword", "");

        FileOutputStream fos = new FileOutputStream(PropertiesUserManagerTest.USERS_FILE);
        users.store(fos, null);

        fos.close();
    }

    @Override
    protected UserManagerFactory createUserManagerFactory() throws SmscException {
        PropertiesUserManagerFactory um = new PropertiesUserManagerFactory();
        um.setFile(PropertiesUserManagerTest.USERS_FILE);
        um.setPasswordEncryptor(new ClearTextPasswordEncryptor());

        return um;
    }

    @Override
    protected void setUp() throws Exception {

        PropertiesUserManagerTest.TEST_DIR.mkdirs();

        this.createUserFile();

        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        IoUtils.delete(PropertiesUserManagerTest.TEST_DIR);
    }

    public void testRefresh() throws FileNotFoundException, IOException {
        Properties users = new Properties();
        users.load(new FileInputStream(PropertiesUserManagerTest.USERS_FILE));
        boolean originalSetting = Boolean.valueOf(users.getProperty("smscserver.user.user2.enableflag"));
        users.setProperty("smscserver.user.user2.enableflag", "true");
        users.store(new FileOutputStream(PropertiesUserManagerTest.USERS_FILE), null);

        PropertiesUserManager pum = (PropertiesUserManager) this.userManager;
        pum.refresh();
        User modifiedUser = pum.getUserByName("user2");
        Assert.assertEquals("Enable flag should have been \"true\" after call to refresh().", true,
                modifiedUser.getEnabled());

        // set everything back again
        users.load(new FileInputStream(PropertiesUserManagerTest.USERS_FILE));
        users.setProperty("smscserver.user.user2.enableflag", Boolean.toString(originalSetting));
        users.store(new FileOutputStream(PropertiesUserManagerTest.USERS_FILE), null);

        pum.refresh();
        // check everything is back again
        modifiedUser = pum.getUserByName("user2");
        Assert.assertEquals("Enable flag should have reset back to \"" + originalSetting
                + "\" after second call to refresh().", originalSetting, modifiedUser.getEnabled());
    }
}
