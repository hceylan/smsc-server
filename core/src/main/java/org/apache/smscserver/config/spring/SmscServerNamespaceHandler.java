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

package org.apache.smscserver.config.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Registration point for SmscServer bean defintion parsers
 * 
 * @author hceylan
 */
public class SmscServerNamespaceHandler extends NamespaceHandlerSupport {

    /**
     * The SmscServer Spring config namespace
     */
    public static final String SMSCSERVER_NS = "http://mina.apache.org/smscserver/spring/v1";

    /**
     * Register the necessary element names with the appropriate bean definition parser
     */
    public SmscServerNamespaceHandler() {
        this.registerBeanDefinitionParser("server", new ServerBeanDefinitionParser());
        this.registerBeanDefinitionParser("nio-listener", new ListenerBeanDefinitionParser());
        this.registerBeanDefinitionParser("file-user-manager", new UserManagerBeanDefinitionParser());
        this.registerBeanDefinitionParser("message-manager", new MessageManagerBeanDefinitionParser());
        this.registerBeanDefinitionParser("db-user-manager", new UserManagerBeanDefinitionParser());
        this.registerBeanDefinitionParser("commands", new CommandFactoryBeanDefinitionParser());
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        // do nothing
    }

}
