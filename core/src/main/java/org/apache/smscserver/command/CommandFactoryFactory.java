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

import ie.omk.smpp.message.SMPPPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.smscserver.command.impl.BindCommand;
import org.apache.smscserver.command.impl.DefaultCommandFactory;
import org.apache.smscserver.command.impl.EnquireLinkCommand;
import org.apache.smscserver.command.impl.SubmitSMCommand;
import org.apache.smscserver.command.impl.UnbindCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for {@link CommandFactory} instances
 * 
 * @author hceylan
 */
public class CommandFactoryFactory {
    private static final Logger LOG = LoggerFactory.getLogger(CommandFactoryFactory.class);

    private static final Map<Integer, Command> DEFAULT_COMMAND_MAP = new HashMap<Integer, Command>();

    static {
        // first populate the default command list
        CommandFactoryFactory.DEFAULT_COMMAND_MAP.put(SMPPPacket.BIND_RECEIVER, BindCommand.SINGLETON);
        CommandFactoryFactory.DEFAULT_COMMAND_MAP.put(SMPPPacket.BIND_TRANSCEIVER, BindCommand.SINGLETON);
        CommandFactoryFactory.DEFAULT_COMMAND_MAP.put(SMPPPacket.BIND_TRANSMITTER, BindCommand.SINGLETON);
        CommandFactoryFactory.DEFAULT_COMMAND_MAP.put(SMPPPacket.UNBIND, UnbindCommand.SINGLETON);
        CommandFactoryFactory.DEFAULT_COMMAND_MAP.put(SMPPPacket.ENQUIRE_LINK, EnquireLinkCommand.SINGLETON);
        CommandFactoryFactory.DEFAULT_COMMAND_MAP.put(SMPPPacket.SUBMIT_SM, SubmitSMCommand.SINGLETON);
    }

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
        Map<Integer, Command> mergedCommands = new HashMap<Integer, Command>();
        if (this.useDefaultCommands) {
            mergedCommands.putAll(CommandFactoryFactory.DEFAULT_COMMAND_MAP);

            this.logCommands(CommandFactoryFactory.DEFAULT_COMMAND_MAP, "default");
        } else {
            CommandFactoryFactory.LOG.info("Default commands are disabled!");
        }

        this.logCommands(this.commandMap, "configured");

        mergedCommands.putAll(this.commandMap);

        return new DefaultCommandFactory(mergedCommands);
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

    private void logCommands(Map<Integer, Command> map, String type) {
        if (map.isEmpty()) {
            CommandFactoryFactory.LOG.info("No {} command found.", type);
        } else {
            List<Integer> ids = new ArrayList<Integer>(map.keySet());

            Collections.sort(ids);

            for (Integer id : ids) {
                CommandFactoryFactory.LOG.info("Adding " + type + " command {} - {}", id, map.get(id).getClass()
                        .getCanonicalName());
            }
        }
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
