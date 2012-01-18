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

package org.apache.smscserver.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.smscserver.SmscServerConfigurationException;
import org.apache.smscserver.ssl.impl.DefaultSslConfiguration;
import org.apache.smscserver.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to configure the SSL settings for the control channel or the data channel.
 * 
 * @author hceylan
 */
public class SslConfigurationFactory {

    private final Logger LOG = LoggerFactory.getLogger(SslConfigurationFactory.class);

    private File keystoreFile = new File("./res/.keystore");

    private String keystorePass;

    private String keystoreType = KeyStore.getDefaultType();

    private String keystoreAlgorithm = KeyManagerFactory.getDefaultAlgorithm();

    private File trustStoreFile;

    private String trustStorePass;

    private String trustStoreType = KeyStore.getDefaultType();

    private String trustStoreAlgorithm = TrustManagerFactory.getDefaultAlgorithm();

    private String sslProtocol = "TLS";

    private ClientAuth clientAuth = ClientAuth.NONE;

    private String keyPass;

    private String keyAlias;

    private String[] enabledCipherSuites;

    /**
     * Create an instance of {@link SslConfiguration} based on the configuration of this factory.
     * 
     * @return The {@link SslConfiguration} instance
     */
    public SslConfiguration createSslConfiguration() {

        try {
            // initialize keystore
            this.LOG.debug("Loading key store from \"{}\", using the key store type \"{}\"",
                    this.keystoreFile.getAbsolutePath(), this.keystoreType);
            KeyStore keyStore = this.loadStore(this.keystoreFile, this.keystoreType, this.keystorePass);

            KeyStore trustStore;
            if (this.trustStoreFile != null) {
                this.LOG.debug("Loading trust store from \"{}\", using the key store type \"{}\"",
                        this.trustStoreFile.getAbsolutePath(), this.trustStoreType);
                trustStore = this.loadStore(this.trustStoreFile, this.trustStoreType, this.trustStorePass);
            } else {
                trustStore = keyStore;
            }

            String keyPassToUse;
            if (this.keyPass == null) {
                keyPassToUse = this.keystorePass;
            } else {
                keyPassToUse = this.keyPass;
            }
            // initialize key manager factory
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(this.keystoreAlgorithm);
            keyManagerFactory.init(keyStore, keyPassToUse.toCharArray());

            // initialize trust manager factory
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(this.trustStoreAlgorithm);
            trustManagerFactory.init(trustStore);

            return new DefaultSslConfiguration(keyManagerFactory, trustManagerFactory, this.clientAuth,
                    this.sslProtocol, this.enabledCipherSuites, this.keyAlias);
        } catch (Exception ex) {
            this.LOG.error("DefaultSsl.configure()", ex);
            throw new SmscServerConfigurationException("DefaultSsl.configure()", ex);
        }
    }

    /**
     * Return the required client authentication setting
     * 
     * @return {@link ClientAuth#NEED} if client authentication is required, {@link ClientAuth#WANT} is client
     *         authentication is wanted or {@link ClientAuth#NONE} if no client authentication is the be performed
     */
    public ClientAuth getClientAuth() {
        return this.clientAuth;
    }

    /**
     * Returns the cipher suites that should be enabled for this connection. Must return null if the default (as decided
     * by the JVM) cipher suites should be used.
     * 
     * @return An array of cipher suites, or null.
     */
    public String[] getEnabledCipherSuites() {
        if (this.enabledCipherSuites != null) {
            return this.enabledCipherSuites.clone();
        } else {
            return null;
        }
    }

    /**
     * Get the server key alias to be used for SSL communication
     * 
     * @return The alias, or null if none is set
     */
    public String getKeyAlias() {
        return this.keyAlias;
    }

    /**
     * The password used to load the key
     * 
     * @return The password
     */
    public String getKeyPassword() {
        return this.keyPass;
    }

    /**
     * The algorithm used to open the key store. Defaults to "SunX509"
     * 
     * @return The key store algorithm
     */
    public String getKeystoreAlgorithm() {
        return this.keystoreAlgorithm;
    }

    /**
     * The key store file used by this configuration
     * 
     * @return The key store file
     */
    public File getKeystoreFile() {
        return this.keystoreFile;
    }

    /**
     * The password used to load the key store
     * 
     * @return The password
     */
    public String getKeystorePassword() {
        return this.keystorePass;
    }

    /**
     * The key store type, defaults to @see {@link KeyStore#getDefaultType()}
     * 
     * @return The key store type
     */
    public String getKeystoreType() {
        return this.keystoreType;
    }

    /**
     * The SSL protocol used for this channel. Supported values are "SSL" and "TLS". Defaults to "TLS".
     * 
     * @return The SSL protocol
     */
    public String getSslProtocol() {
        return this.sslProtocol;
    }

    /**
     * The algorithm used to open the trust store. Defaults to "SunX509"
     * 
     * @return The trust store algorithm
     */
    public String getTruststoreAlgorithm() {
        return this.trustStoreAlgorithm;
    }

    /**
     * Get the file used to load the truststore
     * 
     * @return The {@link File} containing the truststore
     */
    public File getTruststoreFile() {
        return this.trustStoreFile;
    }

    /**
     * The password used to load the trust store
     * 
     * @return The password
     */
    public String getTruststorePassword() {
        return this.trustStorePass;
    }

    /**
     * The trust store type, defaults to @see {@link KeyStore#getDefaultType()}
     * 
     * @return The trust store type
     */
    public String getTruststoreType() {
        return this.trustStoreType;
    }

    private KeyStore loadStore(File storeFile, String storeType, String storePass) throws IOException,
            GeneralSecurityException {
        InputStream fin = null;
        try {
            if (storeFile.exists()) {
                this.LOG.debug("Trying to load store from file");
                fin = new FileInputStream(storeFile);
            } else {
                this.LOG.debug("Trying to load store from classpath");
                fin = this.getClass().getClassLoader().getResourceAsStream(storeFile.getPath());

                if (fin == null) {
                    throw new SmscServerConfigurationException("Key store could not be loaded from "
                            + storeFile.getPath());
                }
            }

            KeyStore store = KeyStore.getInstance(storeType);
            store.load(fin, storePass.toCharArray());

            return store;
        } finally {
            IoUtils.close(fin);
        }
    }

    /**
     * Set what client authentication level to use, supported values are "yes" or "true" for required authentication,
     * "want" for wanted authentication and "false" or "none" for no authentication. Defaults to "none".
     * 
     * @param clientAuthReqd
     *            The desired authentication level
     */
    public void setClientAuthentication(String clientAuthReqd) {
        if ("true".equalsIgnoreCase(clientAuthReqd) || "yes".equalsIgnoreCase(clientAuthReqd)
                || "need".equalsIgnoreCase(clientAuthReqd)) {
            this.clientAuth = ClientAuth.NEED;
        } else if ("want".equalsIgnoreCase(clientAuthReqd)) {
            this.clientAuth = ClientAuth.WANT;
        } else {
            this.clientAuth = ClientAuth.NONE;
        }
    }

    /**
     * Set the allowed cipher suites, note that the exact list of supported cipher suites differs between JRE
     * implementations.
     * 
     * @param enabledCipherSuites
     */
    public void setEnabledCipherSuites(String[] enabledCipherSuites) {
        if (enabledCipherSuites != null) {
            this.enabledCipherSuites = enabledCipherSuites.clone();
        } else {
            this.enabledCipherSuites = null;
        }
    }

    /**
     * Set the alias for the key to be used for SSL communication. If the specified key store contains multiple keys,
     * this alias can be set to select a specific key.
     * 
     * @param keyAlias
     *            The alias to use, or null if JSSE should be allowed to choose the key.
     */
    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    /**
     * Set the password used to load the key
     * 
     * @param keyPass
     *            The password
     */
    public void setKeyPassword(String keyPass) {
        this.keyPass = keyPass;
    }

    /**
     * Override the key store algorithm used to open the key store
     * 
     * @param keystoreAlgorithm
     *            The key store algorithm
     */
    public void setKeystoreAlgorithm(String keystoreAlgorithm) {
        this.keystoreAlgorithm = keystoreAlgorithm;

    }

    /**
     * Set the key store file to be used by this configuration
     * 
     * @param keyStoreFile
     *            A path to an existing key store file
     */
    public void setKeystoreFile(File keyStoreFile) {
        this.keystoreFile = keyStoreFile;
    }

    /**
     * Set the password used to load the key store
     * 
     * @param keystorePass
     *            The password
     */
    public void setKeystorePassword(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    /**
     * Set the key store type
     * 
     * @param keystoreType
     *            The key store type
     */
    public void setKeystoreType(String keystoreType) {
        this.keystoreType = keystoreType;
    }

    /**
     * Set the SSL protocol used for this channel. Supported values are "SSL" and "TLS". Defaults to "TLS".
     * 
     * @param sslProtocol
     *            The SSL protocol
     */
    public void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    /**
     * Override the trust store algorithm used to open the trust store
     * 
     * @param trustStoreAlgorithm
     *            The trust store algorithm
     */
    public void setTruststoreAlgorithm(String trustStoreAlgorithm) {
        this.trustStoreAlgorithm = trustStoreAlgorithm;

    }

    /**
     * Set the password used to load the trust store
     * 
     * @param trustStoreFile
     *            The password
     */
    public void setTruststoreFile(File trustStoreFile) {
        this.trustStoreFile = trustStoreFile;
    }

    /**
     * Set the password used to load the trust store
     * 
     * @param trustStorePass
     *            The password
     */
    public void setTruststorePassword(String trustStorePass) {
        this.trustStorePass = trustStorePass;
    }

    /**
     * Set the trust store type
     * 
     * @param trustStoreType
     *            The trust store type
     */
    public void setTruststoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }
}
