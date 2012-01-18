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

import javax.sql.DataSource;

import org.apache.smscserver.SmscServerConfigurationException;
import org.apache.smscserver.messagemanager.impl.DBMessageManager;
import org.apache.smscserver.smsclet.MessageManager;
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

    private void check(Object field, boolean required, String errorMessage) {
        if (field == null) {
            if (required) {
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
        this.check(this.datasource, true, "datasource not provided");
        this.check(this.sqlCreateTable, false,
                "create table exist SQL statement not provided. Table should be manually created if doesn't exist");
        this.check(this.sqlInsertMessage, true, "insert message SQL statement not provided");
        this.check(this.sqlSelectMessage, true, "select message SQL statement not provided");
        this.check(this.sqlUpdateMessage, true, " update message SQL statement not provided");

        return new DBMessageManager(this.datasource, this.sqlCreateTable, this.sqlInsertMessage, this.sqlSelectMessage,
                this.sqlUpdateMessage, this.sqlSelectLatestReplacableMessage);
    }

    /**
     * @param datasource
     *            the datasource to set
     */
    public void setDatasource(DataSource datasource) {
        this.datasource = datasource;
    }

    /**
     * @param datasource
     *            the datasource to set
     */
    public void setDataSource(DataSource datasource) {
        this.datasource = datasource;
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

}