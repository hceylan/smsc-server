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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.smscserver.smsclet.UserManager;
import org.apache.smscserver.usermanager.impl.PropertiesUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for the properties file based <code>UserManager</code> implementation.
 * 
 * @author hceylan
 */
public class PropertiesUserManagerFactory implements UserManagerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesUserManagerFactory.class);

    private String adminName = "admin";

    private File userDataFile;

    private URL userDataURL;

    private PasswordEncryptor passwordEncryptor = new ClearTextPasswordEncryptor();

    private String smscHome;

    public PropertiesUserManagerFactory() {
        super();
    }

    public PropertiesUserManagerFactory(String smscHome) {
        super();

        this.smscHome = smscHome;
    }

    /**
     * Creates a {@link PropertiesUserManager} instance based on the provided configuration
     */
    public UserManager createUserManager() {
        if (this.userDataFile != null) {
            return new PropertiesUserManager(this.passwordEncryptor, this.userDataFile, this.adminName);
        }

        if (this.smscHome == null) {
            try {
                this.smscHome = new File("./").getCanonicalPath().toString();
            } catch (IOException e1) {
            }
        }

        if (this.userDataURL == null) {
            try {
                this.userDataURL = new URL("file://" + this.smscHome + "/conf/users.properties");

                PropertiesUserManagerFactory.LOG.info("User Properties file is " + this.userDataURL);
            } catch (MalformedURLException e) {
                PropertiesUserManagerFactory.LOG.error("Error with user properties file!", e);
            }
        }

        return new PropertiesUserManager(this.passwordEncryptor, this.userDataURL, this.adminName);
    }

    /**
     * Get the admin name.
     * 
     * @return The admin user name
     */
    public String getAdminName() {
        return this.adminName;
    }

    /**
     * Retrieve the file used to load and store users
     * 
     * @return The file
     */
    public File getFile() {
        return this.userDataFile;
    }

    /**
     * Retrieve the password encryptor used by user managers created by this factory
     * 
     * @return The password encryptor. Default to {@link Md5PasswordEncryptor} if no other has been provided
     */
    public PasswordEncryptor getPasswordEncryptor() {
        return this.passwordEncryptor;
    }

    /**
     * Retrieve the URL used to load and store users
     * 
     * @return The {@link URL}
     */
    public URL getUrl() {
        return this.userDataURL;
    }

    /**
     * Set the name to use as the administrator of the server. The default value is "admin".
     * 
     * @param adminName
     *            The administrator user name
     */
    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    /**
     * Set the file used to store and read users.
     * 
     * @param propFile
     *            A file containing users
     */
    public void setFile(File propFile) {
        this.userDataFile = propFile;
    }

    /**
     * Set the password encryptor to use by user managers created by this factory
     * 
     * @param passwordEncryptor
     *            The password encryptor
     */
    public void setPasswordEncryptor(PasswordEncryptor passwordEncryptor) {
        this.passwordEncryptor = passwordEncryptor;
    }

    /**
     * Set the URL used to store and read users.
     * 
     * @param userDataURL
     *            A {@link URL} containing users
     */
    public void setUrl(URL userDataURL) {
        this.userDataURL = userDataURL;
    }
}
