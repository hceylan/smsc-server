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

import java.util.List;

import org.apache.smscserver.command.CommandFactory;
import org.apache.smscserver.command.CommandFactoryFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Parses the SmscServer "commands" element into a Spring bean graph
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class CommandFactoryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {

        BeanDefinitionBuilder factoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(CommandFactoryFactory.class);

        ManagedMap commands = new ManagedMap();

        List<Element> childs = SpringUtil.getChildElements(element);

        for (Element commandElm : childs) {
            String name = commandElm.getAttribute("name");
            Object bean = SpringUtil.parseSpringChildElement(commandElm, parserContext, builder);
            commands.put(name, bean);
        }

        factoryBuilder.addPropertyValue("commandMap", commands);

        if (StringUtils.hasText(element.getAttribute("use-default"))) {
            factoryBuilder.addPropertyValue("useDefaultCommands", Boolean.valueOf(element.getAttribute("use-default")));
        }

        BeanDefinition factoryDefinition = factoryBuilder.getBeanDefinition();
        String factoryId = parserContext.getReaderContext().generateBeanName(factoryDefinition);

        BeanDefinitionHolder factoryHolder = new BeanDefinitionHolder(factoryDefinition, factoryId);
        this.registerBeanDefinition(factoryHolder, parserContext.getRegistry());

        // set the factory on the listener bean
        builder.getRawBeanDefinition().setFactoryBeanName(factoryId);
        builder.getRawBeanDefinition().setFactoryMethodName("createCommandFactory");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<? extends CommandFactory> getBeanClass(final Element element) {
        return null;
    }
}
