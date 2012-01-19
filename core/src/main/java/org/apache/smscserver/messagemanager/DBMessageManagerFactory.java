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

package org.apache.smscserver.messagemanager;

import java.io.InputStream;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.smscserver.SmscServerConfigurationException;
import org.apache.smscserver.messagemanager.impl.DBMessageManager;
import org.apache.smscserver.smsclet.MessageManager;
import org.apache.smscserver.util.IoUtils;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for database based <code>MessageManager</code>.
 * 
 * @author hceylan
 */
public class DBMessageManagerFactory implements MessageManagerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DBMessageManagerFactory.class);

    private DataSource datasource;
    private String sqlCreateTable;
    private String sqlInsertMessage;
    private String sqlSelectMessage;
    private String sqlUpdateMessage;
    private String sqlSelectLatestReplacableMessage;

    private String embeddedProfile;
    private String url;

    public DBMessageManagerFactory(String embeddedProfile, String url) {
        super();

        this.embeddedProfile = embeddedProfile;
        this.url = url;
    }

    private void check(Object field, boolean required, String errorMessage) {
        if (field == null) {
            if (required) {
                // FIXME: Hasan for now
                throw new SmscServerConfigurationException("Required " + errorMessage);
            } else {
                DBMessageManagerFactory.LOG.warn("Optional parameter {}", errorMessage);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    public MessageManager createMessageManager() {
        if (this.embeddedProfile != null) {
            DBMessageManagerFactory.LOG.info("Using embedded profile {}", this.embeddedProfile);

            this.startEmbeddedMode();
        }

        return this.createMessageManagerImpl();
    }

    private MessageManager createMessageManagerImpl() {
        this.check(this.datasource, true, "datasource not provided");
        this.check(this.sqlCreateTable, false,
                "create table exist SQL statement not provided. Table should be manually created if doesn't exist");
        this.check(this.sqlInsertMessage, true, "insert message SQL statement not provided");
        this.check(this.sqlSelectMessage, true, "select message SQL statement not provided");
        this.check(this.sqlUpdateMessage, true, " update message SQL statement not provided");

        return new DBMessageManager(this.datasource, this.sqlCreateTable, this.sqlInsertMessage, this.sqlSelectMessage,
                this.sqlUpdateMessage, this.sqlSelectLatestReplacableMessage);
    }

    private String getProfileSQL(String qualifier) {
        try {
            String sqlFile = "/org/apache/smscserver/config/db/messages-" + qualifier + "-" + this.embeddedProfile
                    + ".sql";
            DBMessageManagerFactory.LOG.debug("SQL File to read: {}", sqlFile);

            InputStream is = this.getClass().getResourceAsStream(sqlFile);

            return IoUtils.readFully(is);
        } catch (Exception e) {
            throw new SmscServerConfigurationException("Error reading SQL resource", e);
        }
    }

    /**
     * @param datasource
     *            the datasource to set
     */
    public void setDataSource(DataSource datasource) {
        this.datasource = datasource;
    }

    /**
     * @param embeddedProfile
     *            the embeddedProfile to set
     */
    public void setEmbedded(String embeddedProfile) {
        this.embeddedProfile = embeddedProfile;
    }

    /**
     * @param sqlCreateTable
     *            the sqlCreateTable to set
     */
    public void setSqlCreateTable(String sqlCreateTable) {
        this.sqlCreateTable = sqlCreateTable;
    }

    /**
     * @param sqlInsertMessage
     *            the sqlInsertMessage to set
     */
    public void setSqlInsertMessage(String sqlInsertMessage) {
        this.sqlInsertMessage = sqlInsertMessage;
    }

    /**
     * @param sqlSelectLatestReplacableMessage
     *            the sqlSelectLatestReplacableMessage to set
     */
    public void setSqlSelectLatestReplacableMessage(String sqlSelectLatestReplacableMessage) {
        this.sqlSelectLatestReplacableMessage = sqlSelectLatestReplacableMessage;
    }

    /**
     * @param sqlSelectMessage
     *            the sqlSelectMessage to set
     */
    public void setSqlSelectMessage(String sqlSelectMessage) {
        this.sqlSelectMessage = sqlSelectMessage;
    }

    /**
     * @param sqlUpdateMessage
     *            the sqlUpdateMessage to set
     */
    public void setSqlUpdateMessage(String sqlUpdateMessage) {
        this.sqlUpdateMessage = sqlUpdateMessage;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setURL(String url) {
        this.url = url;
    }

    private void startEmbeddedMode() {
        if (this.datasource == null) {
            if (StringUtils.isEmpty(this.url)) {
                throw new SmscServerConfigurationException(
                        "When using embedded mode and no datasource provided, URL paramater is required!");
            }

            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL(this.url);
            ds.setUser("sa");
            ds.setPassword("");

            this.datasource = ds;
        }

        if (this.sqlCreateTable == null) {
            this.sqlCreateTable = this.getProfileSQL("createtable");
        }

        if (this.sqlInsertMessage == null) {
            this.sqlInsertMessage = this.getProfileSQL("insert");
        }

        if (this.sqlUpdateMessage == null) {
            this.sqlUpdateMessage = this.getProfileSQL("update");
        }

        if (this.sqlSelectMessage == null) {
            this.sqlSelectMessage = this.getProfileSQL("select");
        }

        if (this.sqlSelectLatestReplacableMessage == null) {
            this.sqlSelectLatestReplacableMessage = this.getProfileSQL("selectlatestreplacable");
        }
    }
}