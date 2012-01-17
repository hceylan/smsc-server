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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.smscserver.SmscServerConfigurationException;
import org.apache.smscserver.smsclet.Authentication;
import org.apache.smscserver.smsclet.AuthenticationFailedException;
import org.apache.smscserver.smsclet.Authority;
import org.apache.smscserver.smsclet.SmscException;
import org.apache.smscserver.smsclet.User;
import org.apache.smscserver.usermanager.AnonymousAuthentication;
import org.apache.smscserver.usermanager.PasswordEncryptor;
import org.apache.smscserver.usermanager.PropertiesUserManagerFactory;
import org.apache.smscserver.usermanager.UsernamePasswordAuthentication;
import org.apache.smscserver.util.BaseProperties;
import org.apache.smscserver.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * <p>
 * Properties file based <code>UserManager</code> implementation. We use <code>user.properties</code> file to store user
 * data.
 * </p>
 * 
 * </p>The file will use the following properties for storing users:</p>
 * <table>
 * <tr>
 * <th>Property</th>
 * <th>Documentation</th>
 * <tr>
 * <td>smscserver.user.{username}.userpassword</td>
 * <td>The password for the user. Can be in clear text, MD5 hash or salted SHA hash based on the configuration on the
 * user manager</td>
 * </tr>
 * <tr>
 * <td>smscserver.user.{username}.enableflag</td>
 * <td>true if the user is enabled, false otherwise</td>
 * </tr>
 * <tr>
 * <td>smscserver.user.{username}.idletime</td>
 * <td>The number of seconds the user is allowed to be idle before disconnected. 0 disables the idle timeout</td>
 * </tr>
 * <tr>
 * <td>smscserver.user.{username}.maxloginnumber</td>
 * <td>The maximum number of concurrent logins by the user. 0 disables the check.</td>
 * </tr>
 * <tr>
 * <td>smscserver.user.{username}.maxloginperip</td>
 * <td>The maximum number of concurrent logins from the same IP address by the user. 0 disables the check.</td>
 * </tr>
 * </table>
 * 
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * smscserver.user.admin.userpassword=admin
 * smscserver.user.admin.enableflag=true
 * smscserver.user.admin.idletime=0
 * smscserver.user.admin.maxloginnumber=0
 * smscserver.user.admin.maxloginperip=0
 * </pre>
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class PropertiesUserManager extends AbstractUserManager {

    private final Logger LOG = LoggerFactory.getLogger(PropertiesUserManager.class);

    private final static String PREFIX = "smscserver.user.";

    private BaseProperties userDataProp;

    private File userDataFile;

    private URL userUrl;

    /**
     * Internal constructor, do not use directly. Use {@link PropertiesUserManagerFactory} instead.
     */
    public PropertiesUserManager(PasswordEncryptor passwordEncryptor, File userDataFile, String adminName) {
        super(adminName, passwordEncryptor);

        this.loadFromFile(userDataFile);
    }

    /**
     * Internal constructor, do not use directly. Use {@link PropertiesUserManagerFactory} instead.
     */
    public PropertiesUserManager(PasswordEncryptor passwordEncryptor, URL userDataPath, String adminName) {
        super(adminName, passwordEncryptor);

        this.loadFromUrl(userDataPath);
    }

    /**
     * User authenticate method
     */
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        if (authentication instanceof UsernamePasswordAuthentication) {
            UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;

            String user = upauth.getUsername();
            String password = upauth.getPassword();

            if (user == null) {
                throw new AuthenticationFailedException("Authentication failed");
            }

            if (password == null) {
                password = "";
            }

            String storedPassword = this.userDataProp.getProperty(PropertiesUserManager.PREFIX + user + '.'
                    + AbstractUserManager.ATTR_PASSWORD);

            if (storedPassword == null) {
                // user does not exist
                throw new AuthenticationFailedException("Authentication failed");
            }

            if (this.getPasswordEncryptor().matches(password, storedPassword)) {
                return this.getUserByName(user);
            } else {
                throw new AuthenticationFailedException("Authentication failed");
            }

        } else if (authentication instanceof AnonymousAuthentication) {
            if (this.doesExist("anonymous")) {
                return this.getUserByName("anonymous");
            } else {
                throw new AuthenticationFailedException("Authentication failed");
            }
        } else {
            throw new IllegalArgumentException("Authentication not supported by this user manager");
        }
    }

    /**
     * Delete an user. Removes all this user entries from the properties. After removing the corresponding from the
     * properties, save the data.
     */
    public void delete(String usrName) throws SmscException {
        // remove entries from properties
        String thisPrefix = PropertiesUserManager.PREFIX + usrName + '.';
        Enumeration<?> propNames = this.userDataProp.propertyNames();
        ArrayList<String> remKeys = new ArrayList<String>();
        while (propNames.hasMoreElements()) {
            String thisKey = propNames.nextElement().toString();
            if (thisKey.startsWith(thisPrefix)) {
                remKeys.add(thisKey);
            }
        }
        Iterator<String> remKeysIt = remKeys.iterator();
        while (remKeysIt.hasNext()) {
            this.userDataProp.remove(remKeysIt.next());
        }

        this.saveUserData();
    }

    /**
     * Close the user manager - remove existing entries.
     */
    public synchronized void dispose() {
        if (this.userDataProp != null) {
            this.userDataProp.clear();
            this.userDataProp = null;
        }
    }

    /**
     * User existance check
     */
    public boolean doesExist(String name) {
        String key = PropertiesUserManager.PREFIX + name + '.' + AbstractUserManager.ATTR_HOME;
        return this.userDataProp.containsKey(key);
    }

    /**
     * Get all user names.
     */
    public String[] getAllUserNames() {
        // get all user names
        String suffix = '.' + AbstractUserManager.ATTR_HOME;
        ArrayList<String> ulst = new ArrayList<String>();
        Enumeration<?> allKeys = this.userDataProp.propertyNames();
        int prefixlen = PropertiesUserManager.PREFIX.length();
        int suffixlen = suffix.length();
        while (allKeys.hasMoreElements()) {
            String key = (String) allKeys.nextElement();
            if (key.endsWith(suffix)) {
                String name = key.substring(prefixlen);
                int endIndex = name.length() - suffixlen;
                name = name.substring(0, endIndex);
                ulst.add(name);
            }
        }

        Collections.sort(ulst);
        return ulst.toArray(new String[0]);
    }

    /**
     * Retrive the file backing this user manager
     * 
     * @return The file
     */
    public File getFile() {
        return this.userDataFile;
    }

    /**
     * Get user password. Returns the encrypted value.
     * 
     * <pre>
     * If the password value is not null
     *    password = new password 
     * else 
     *   if user does exist
     *     password = old password
     *   else 
     *     password = &quot;&quot;
     * </pre>
     */
    private String getPassword(User usr) {
        String name = usr.getName();
        String password = usr.getPassword();

        if (password != null) {
            password = this.getPasswordEncryptor().encrypt(password);
        } else {
            String blankPassword = this.getPasswordEncryptor().encrypt("");

            if (this.doesExist(name)) {
                String key = PropertiesUserManager.PREFIX + name + '.' + AbstractUserManager.ATTR_PASSWORD;
                password = this.userDataProp.getProperty(key, blankPassword);
            } else {
                password = blankPassword;
            }
        }
        return password;
    }

    /**
     * Load user data.
     */
    public User getUserByName(String userName) {
        if (!this.doesExist(userName)) {
            return null;
        }

        String baseKey = PropertiesUserManager.PREFIX + userName + '.';
        BaseUser user = new BaseUser();
        user.setName(userName);
        user.setEnabled(this.userDataProp.getBoolean(baseKey + AbstractUserManager.ATTR_ENABLE, true));
        user.setHomeDirectory(this.userDataProp.getProperty(baseKey + AbstractUserManager.ATTR_HOME, "/"));

        List<Authority> authorities = new ArrayList<Authority>();

        if (this.userDataProp.getBoolean(baseKey + AbstractUserManager.ATTR_WRITE_PERM, false)) {
            authorities.add(new WritePermission());
        }

        int maxLogin = this.userDataProp.getInteger(baseKey + AbstractUserManager.ATTR_MAX_LOGIN_NUMBER, 0);
        int maxLoginPerIP = this.userDataProp.getInteger(baseKey + AbstractUserManager.ATTR_MAX_LOGIN_PER_IP, 0);

        authorities.add(new ConcurrentLoginPermission(maxLogin, maxLoginPerIP));

        int uploadRate = this.userDataProp.getInteger(baseKey + AbstractUserManager.ATTR_MAX_UPLOAD_RATE, 0);
        int downloadRate = this.userDataProp.getInteger(baseKey + AbstractUserManager.ATTR_MAX_DOWNLOAD_RATE, 0);

        authorities.add(new TransferRatePermission(downloadRate, uploadRate));

        user.setAuthorities(authorities);

        user.setMaxIdleTime(this.userDataProp.getInteger(baseKey + AbstractUserManager.ATTR_MAX_IDLE_TIME, 0));

        return user;
    }

    private void loadFromFile(File userDataFile) {
        try {
            this.userDataProp = new BaseProperties();

            if (userDataFile != null) {
                this.LOG.debug("File configured, will try loading");

                if (userDataFile.exists()) {
                    this.userDataFile = userDataFile;

                    this.LOG.debug("File found on file system");
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(userDataFile);
                        this.userDataProp.load(fis);
                    } finally {
                        IoUtils.close(fis);
                    }
                } else {
                    // try loading it from the classpath
                    this.LOG.debug("File not found on file system, try loading from classpath");

                    InputStream is = this.getClass().getClassLoader().getResourceAsStream(userDataFile.getPath());

                    if (is != null) {
                        try {
                            this.userDataProp.load(is);
                        } finally {
                            IoUtils.close(is);
                        }
                    } else {
                        throw new SmscServerConfigurationException(
                                "User data file specified but could not be located, "
                                        + "neither on the file system or in the classpath: " + userDataFile.getPath());
                    }
                }
            }
        } catch (IOException e) {
            throw new SmscServerConfigurationException("Error loading user data file : " + userDataFile, e);
        }
    }

    private void loadFromUrl(URL userDataPath) {
        try {
            this.userDataProp = new BaseProperties();

            if (userDataPath != null) {
                this.LOG.debug("URL configured, will try loading");

                this.userUrl = userDataPath;
                InputStream is = null;

                is = userDataPath.openStream();

                try {
                    this.userDataProp.load(is);
                } finally {
                    IoUtils.close(is);
                }
            }
        } catch (IOException e) {
            throw new SmscServerConfigurationException("Error loading user data resource : " + userDataPath, e);
        }
    }

    /**
     * Reloads the contents of the user.properties file. This allows any manual modifications to the file to be
     * recognised by the running server.
     */
    public void refresh() {
        synchronized (this.userDataProp) {
            if (this.userDataFile != null) {
                this.LOG.debug("Refreshing user manager using file: " + this.userDataFile.getAbsolutePath());
                this.loadFromFile(this.userDataFile);

            } else {
                // file is null, must have been created using URL
                this.LOG.debug("Refreshing user manager using URL: " + this.userUrl.toString());
                this.loadFromUrl(this.userUrl);
            }
        }
    }

    /**
     * Save user data. Store the properties.
     */
    public synchronized void save(User usr) throws SmscException {
        // null value check
        if (usr.getName() == null) {
            throw new NullPointerException("User name is null.");
        }
        String thisPrefix = PropertiesUserManager.PREFIX + usr.getName() + '.';

        // set other properties
        this.userDataProp.setProperty(thisPrefix + AbstractUserManager.ATTR_PASSWORD, this.getPassword(usr));

        String home = usr.getHomeDirectory();
        if (home == null) {
            home = "/";
        }
        this.userDataProp.setProperty(thisPrefix + AbstractUserManager.ATTR_HOME, home);
        this.userDataProp.setProperty(thisPrefix + AbstractUserManager.ATTR_ENABLE, usr.getEnabled());
        this.userDataProp.setProperty(thisPrefix + AbstractUserManager.ATTR_WRITE_PERM,
                usr.authorize(new WriteRequest()) != null);
        this.userDataProp.setProperty(thisPrefix + AbstractUserManager.ATTR_MAX_IDLE_TIME, usr.getMaxIdleTime());

        TransferRateRequest transferRateRequest = new TransferRateRequest();
        transferRateRequest = (TransferRateRequest) usr.authorize(transferRateRequest);

        if (transferRateRequest != null) {
            this.userDataProp.setProperty(thisPrefix + AbstractUserManager.ATTR_MAX_UPLOAD_RATE,
                    transferRateRequest.getMaxUploadRate());
            this.userDataProp.setProperty(thisPrefix + AbstractUserManager.ATTR_MAX_DOWNLOAD_RATE,
                    transferRateRequest.getMaxDownloadRate());
        } else {
            this.userDataProp.remove(thisPrefix + AbstractUserManager.ATTR_MAX_UPLOAD_RATE);
            this.userDataProp.remove(thisPrefix + AbstractUserManager.ATTR_MAX_DOWNLOAD_RATE);
        }

        // request that always will succeed
        ConcurrentLoginRequest concurrentLoginRequest = new ConcurrentLoginRequest(0, 0);
        concurrentLoginRequest = (ConcurrentLoginRequest) usr.authorize(concurrentLoginRequest);

        if (concurrentLoginRequest != null) {
            this.userDataProp.setProperty(thisPrefix + AbstractUserManager.ATTR_MAX_LOGIN_NUMBER,
                    concurrentLoginRequest.getMaxConcurrentLogins());
            this.userDataProp.setProperty(thisPrefix + AbstractUserManager.ATTR_MAX_LOGIN_PER_IP,
                    concurrentLoginRequest.getMaxConcurrentLoginsPerIP());
        } else {
            this.userDataProp.remove(thisPrefix + AbstractUserManager.ATTR_MAX_LOGIN_NUMBER);
            this.userDataProp.remove(thisPrefix + AbstractUserManager.ATTR_MAX_LOGIN_PER_IP);
        }

        this.saveUserData();
    }

    /**
     * @throws SmscException
     */
    private void saveUserData() throws SmscException {
        if (this.userDataFile == null) {
            return;
        }

        File dir = this.userDataFile.getAbsoluteFile().getParentFile();
        if ((dir != null) && !dir.exists() && !dir.mkdirs()) {
            String dirName = dir.getAbsolutePath();
            throw new SmscServerConfigurationException("Cannot create directory for user data file : " + dirName);
        }

        // save user data
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(this.userDataFile);
            this.userDataProp.store(fos, "Generated file - don't edit (please)");
        } catch (IOException ex) {
            this.LOG.error("Failed saving user data", ex);
            throw new SmscException("Failed saving user data", ex);
        } finally {
            IoUtils.close(fos);
        }
    }

}
