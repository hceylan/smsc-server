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

import java.net.UnknownHostException;

import org.apache.smscserver.SmscServerConfigurationException;
import org.apache.smscserver.ipfilter.IpFilterType;
import org.apache.smscserver.ipfilter.RemoteIpFilter;
import org.apache.smscserver.listener.ListenerFactory;
import org.apache.smscserver.ssl.SslConfiguration;
import org.apache.smscserver.ssl.SslConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Parses the SmscServer "nio-listener" element into a Spring bean graph
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class ListenerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    private final Logger LOG = LoggerFactory.getLogger(ListenerBeanDefinitionParser.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {

        BeanDefinitionBuilder factoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(ListenerFactory.class);

        if (StringUtils.hasText(element.getAttribute("port"))) {
            factoryBuilder.addPropertyValue("port", Integer.valueOf(element.getAttribute("port")));
        }

        SslConfiguration ssl = this.parseSsl(element);
        if (ssl != null) {
            factoryBuilder.addPropertyValue("sslConfiguration", ssl);
        }

        if (StringUtils.hasText(element.getAttribute("idle-timeout"))) {
            factoryBuilder.addPropertyValue("idleTimeout", SpringUtil.parseInt(element, "idle-timeout", 300));
        }

        String localAddress = SpringUtil.parseStringFromInetAddress(element, "local-address");
        if (localAddress != null) {
            factoryBuilder.addPropertyValue("serverAddress", localAddress);
        }
        factoryBuilder.addPropertyValue("implicitSsl", SpringUtil.parseBoolean(element, "implicit-ssl", false));

        Element blacklistElm = SpringUtil.getChildElement(element, SmscServerNamespaceHandler.SMSCSERVER_NS,
                "blacklist");
        if (blacklistElm != null) {
            this.LOG.warn("Element 'blacklist' is deprecated, and may be removed in a future release. Please use 'remote-ip-filter' instead. ");
            try {
                RemoteIpFilter remoteIpFilter = new RemoteIpFilter(IpFilterType.DENY, blacklistElm.getTextContent());
                factoryBuilder.addPropertyValue("sessionFilter", remoteIpFilter);
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException("Invalid IP address or subnet in the 'blacklist' element", e);
            }
        }

        Element remoteIpFilterElement = SpringUtil.getChildElement(element, SmscServerNamespaceHandler.SMSCSERVER_NS,
                "remote-ip-filter");
        if (remoteIpFilterElement != null) {
            if (blacklistElm != null) {
                throw new SmscServerConfigurationException(
                        "Element 'remote-ip-filter' may not be used when 'blacklist' element is specified. ");
            }
            String filterType = remoteIpFilterElement.getAttribute("type");
            try {
                RemoteIpFilter remoteIpFilter = new RemoteIpFilter(IpFilterType.parse(filterType),
                        remoteIpFilterElement.getTextContent());
                factoryBuilder.addPropertyValue("sessionFilter", remoteIpFilter);
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException("Invalid IP address or subnet in the 'remote-ip-filter' element");
            }
        }

        BeanDefinition factoryDefinition = factoryBuilder.getBeanDefinition();

        String listenerFactoryName = parserContext.getReaderContext().generateBeanName(factoryDefinition);

        BeanDefinitionHolder factoryHolder = new BeanDefinitionHolder(factoryDefinition, listenerFactoryName);
        this.registerBeanDefinition(factoryHolder, parserContext.getRegistry());

        // set the factory on the listener bean
        builder.getRawBeanDefinition().setFactoryBeanName(listenerFactoryName);
        builder.getRawBeanDefinition().setFactoryMethodName("createListener");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getBeanClass(final Element element) {
        return null;
    }

    private SslConfiguration parseSsl(final Element parent) {
        Element sslElm = SpringUtil.getChildElement(parent, SmscServerNamespaceHandler.SMSCSERVER_NS, "ssl");

        if (sslElm != null) {
            SslConfigurationFactory ssl = new SslConfigurationFactory();

            Element keyStoreElm = SpringUtil.getChildElement(sslElm, SmscServerNamespaceHandler.SMSCSERVER_NS,
                    "keystore");
            if (keyStoreElm != null) {
                ssl.setKeystoreFile(SpringUtil.parseFile(keyStoreElm, "file"));
                ssl.setKeystorePassword(SpringUtil.parseString(keyStoreElm, "password"));

                String type = SpringUtil.parseString(keyStoreElm, "type");
                if (type != null) {
                    ssl.setKeystoreType(type);
                }

                String keyAlias = SpringUtil.parseString(keyStoreElm, "key-alias");
                if (keyAlias != null) {
                    ssl.setKeyAlias(keyAlias);
                }

                String keyPassword = SpringUtil.parseString(keyStoreElm, "key-password");
                if (keyPassword != null) {
                    ssl.setKeyPassword(keyPassword);
                }

                String algorithm = SpringUtil.parseString(keyStoreElm, "algorithm");
                if (algorithm != null) {
                    ssl.setKeystoreAlgorithm(algorithm);
                }
            }

            Element trustStoreElm = SpringUtil.getChildElement(sslElm, SmscServerNamespaceHandler.SMSCSERVER_NS,
                    "truststore");
            if (trustStoreElm != null) {
                ssl.setTruststoreFile(SpringUtil.parseFile(trustStoreElm, "file"));
                ssl.setTruststorePassword(SpringUtil.parseString(trustStoreElm, "password"));

                String type = SpringUtil.parseString(trustStoreElm, "type");
                if (type != null) {
                    ssl.setTruststoreType(type);
                }

                String algorithm = SpringUtil.parseString(trustStoreElm, "algorithm");
                if (algorithm != null) {
                    ssl.setTruststoreAlgorithm(algorithm);
                }
            }

            String clientAuthStr = SpringUtil.parseString(sslElm, "client-authentication");
            if (clientAuthStr != null) {
                ssl.setClientAuthentication(clientAuthStr);
            }

            String enabledCiphersuites = SpringUtil.parseString(sslElm, "enabled-ciphersuites");
            if (enabledCiphersuites != null) {
                ssl.setEnabledCipherSuites(enabledCiphersuites.split(" "));
            }

            String protocol = SpringUtil.parseString(sslElm, "protocol");
            if (protocol != null) {
                ssl.setSslProtocol(protocol);
            }

            return ssl.createSslConfiguration();
        } else {
            return null;
        }

    }

}
