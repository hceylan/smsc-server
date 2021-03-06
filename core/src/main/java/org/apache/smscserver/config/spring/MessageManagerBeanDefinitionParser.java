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

import org.apache.smscserver.message.DBMessageManagerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Parses the SmscServer "message-manager"
 * 
 * @author hceylan
 */
public class MessageManagerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {

        Class<?> factoryClass = DBMessageManagerFactory.class;

        BeanDefinitionBuilder factoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(factoryClass);

        Element dsElm = SpringUtil.getChildElement(element, SmscServerNamespaceHandler.SMSCSERVER_NS, "data-source");

        if (dsElm != null) {
            // schema ensure we get the right type of element
            Element springElm = SpringUtil.getChildElement(dsElm, null, null);
            Object o;
            if ("bean".equals(springElm.getLocalName())) {
                o = parserContext.getDelegate().parseBeanDefinitionElement(springElm, builder.getBeanDefinition());
            } else {
                // ref
                o = parserContext.getDelegate().parsePropertySubElement(springElm, builder.getBeanDefinition());
            }

            factoryBuilder.addPropertyValue("dataSource", o);
        }

        factoryBuilder.addPropertyValue("embeddedProfile", this.getChildElement(element, "embedded-profile"));
        factoryBuilder.addPropertyValue("URL", this.getChildElement(element, "url"));

        factoryBuilder.addPropertyValue("sqlCreateTable", this.getChildElement(element, "crate-table"));
        factoryBuilder.addPropertyValue("sqlInsertMessage", this.getChildElement(element, "insert-message"));
        factoryBuilder.addPropertyValue("sqlSelectMessage", this.getChildElement(element, "select-message"));
        factoryBuilder.addPropertyValue("sqlSelectUserMessage", this.getChildElement(element, "select-user-message"));
        factoryBuilder.addPropertyValue("sqlSelectLatestReplacableMessage",
                this.getChildElement(element, "select-replace"));
        factoryBuilder.addPropertyValue("sqlUpdateMessage", this.getChildElement(element, "update-message"));

        BeanDefinition factoryDefinition = factoryBuilder.getBeanDefinition();
        String factoryId = parserContext.getReaderContext().generateBeanName(factoryDefinition);

        BeanDefinitionHolder factoryHolder = new BeanDefinitionHolder(factoryDefinition, factoryId);
        this.registerBeanDefinition(factoryHolder, parserContext.getRegistry());

        // set the factory on the listener bean
        builder.getRawBeanDefinition().setFactoryBeanName(factoryId);
        builder.getRawBeanDefinition().setFactoryMethodName("createMessageManager");

    }

    @Override
    protected Class<?> getBeanClass(final Element element) {
        return null;
    }

    private String getChildElement(final Element element, final String elmName) {
        return SpringUtil.getChildElementText(element, SmscServerNamespaceHandler.SMSCSERVER_NS, elmName);
    }
}
