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

package org.apache.smscserver.command.impl;

import java.util.Map;

import org.apache.smscserver.command.Command;
import org.apache.smscserver.command.CommandFactory;
import org.apache.smscserver.command.CommandFactoryFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * Command factory to return appropriate command implementation depending on the SMSC Command id.
 * 
 * <strong><strong>Internal class, do not use directly.</strong></strong>
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class DefaultCommandFactory implements CommandFactory {

    private final Map<Integer, Command> commandMap;

    /**
     * Internal constructor, use {@link CommandFactoryFactory} instead
     */
    public DefaultCommandFactory(Map<Integer, Command> commandMap) {
        this.commandMap = commandMap;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Command getCommand(int commandID) {
        return this.commandMap.get(commandID);
    }

}
