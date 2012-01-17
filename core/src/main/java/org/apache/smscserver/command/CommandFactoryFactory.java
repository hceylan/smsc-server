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

package org.apache.smscserver.command;

import java.util.HashMap;
import java.util.Map;

import org.apache.smscserver.command.impl.DefaultCommandFactory;

/**
 * Factory for {@link CommandFactory} instances
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class CommandFactoryFactory {

    private final Map<Integer, Command> commandMap = new HashMap<Integer, Command>();

    private boolean useDefaultCommands = true;

    /**
     * Add or override a command.
     * 
     * @param commandID
     *            The Id of the command
     * @param command
     *            The command
     */
    public void addCommand(Integer commandID, Command command) {
        if (commandID == null) {
            throw new NullPointerException("commandName can not be null");
        }
        if (command == null) {
            throw new NullPointerException("command can not be null");
        }

        this.commandMap.put(commandID, command);
    }

    /**
     * Create an {@link CommandFactory} based on the configuration on the factory.
     * 
     * @return The {@link CommandFactory}
     */
    public CommandFactory createCommandFactory() {
        return new DefaultCommandFactory(this.commandMap);
    }

    /**
     * Get the installed commands
     * 
     * @return The installed commands
     */
    public Map<Integer, Command> getCommandMap() {
        return this.commandMap;
    }

    /**
     * Are default commands used?
     * 
     * @return true if default commands are used
     */
    public boolean isUseDefaultCommands() {
        return this.useDefaultCommands;
    }

    /**
     * Set commands to add or override to the default commands
     * 
     * @param commandMap
     *            The map of commands, the key will be used to map to requests.
     */
    public void setCommandMap(final Map<Integer, Command> commandMap) {
        if (commandMap == null) {
            throw new NullPointerException("commandMap can not be null");
        }

        this.commandMap.clear();
        this.commandMap.putAll(commandMap);
    }

    /**
     * Sets whether the default commands will be used.
     * 
     * @param useDefaultCommands
     *            true if default commands should be used
     */
    public void setUseDefaultCommands(final boolean useDefaultCommands) {
        this.useDefaultCommands = useDefaultCommands;
    }
}
