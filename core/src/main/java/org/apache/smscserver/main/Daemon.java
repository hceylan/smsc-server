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

package org.apache.smscserver.main;

import org.apache.smscserver.SmscServer;
import org.apache.smscserver.SmscServerFactory;
import org.apache.smscserver.smsclet.SmscException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Invokes SmscServer as a daemon, running in the background. Used for example for the Windows service.
 * 
 * @author hceylan
 */
public class Daemon {

    private static final Logger LOG = LoggerFactory.getLogger(Daemon.class);

    private static SmscServer server;

    private static Object lock = new Object();

    /**
     * Get the configuration object.
     */
    private static SmscServer getConfiguration(String[] args) throws Exception {

        SmscServer server = null;
        if ((args == null) || (args.length < 2)) {
            Daemon.LOG.info("Using default configuration....");
            server = new SmscServerFactory().createServer();
        } else if ((args.length == 2) && args[1].equals("-default")) {
            // supported for backwards compatibility, but not documented
            System.out.println("The -default switch is deprecated, please use --default instead");
            Daemon.LOG.info("Using default configuration....");
            server = new SmscServerFactory().createServer();
        } else if ((args.length == 2) && args[1].equals("--default")) {
            Daemon.LOG.info("Using default configuration....");
            server = new SmscServerFactory().createServer();
        } else if (args.length == 2) {
            Daemon.LOG.info("Using xml configuration file " + args[1] + "...");
            FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(args[1]);

            if (ctx.containsBean("server")) {
                server = (SmscServer) ctx.getBean("server");
            } else {
                String[] beanNames = ctx.getBeanNamesForType(SmscServer.class);
                if (beanNames.length == 1) {
                    server = (SmscServer) ctx.getBean(beanNames[0]);
                } else if (beanNames.length > 1) {
                    System.out.println("Using the first server defined in the configuration, named " + beanNames[0]);
                    server = (SmscServer) ctx.getBean(beanNames[0]);
                } else {
                    System.err.println("XML configuration does not contain a server configuration");
                }
            }
        } else {
            throw new SmscException("Invalid configuration option");
        }

        return server;
    }

    /**
     * Main entry point for the daemon
     * 
     * @param args
     *            The arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        try {
            if (Daemon.server == null) {
                // get configuration
                Daemon.server = Daemon.getConfiguration(args);
                if (Daemon.server == null) {
                    Daemon.LOG.error("No configuration provided");
                    throw new SmscException("No configuration provided");
                }
            }

            String command = "start";

            if ((args != null) && (args.length > 0)) {
                command = args[0];
            }

            if (command.equals("start")) {
                Daemon.LOG.info("Starting SMSC server daemon");
                Daemon.server.start();

                synchronized (Daemon.lock) {
                    Daemon.lock.wait();
                }
            } else if (command.equals("stop")) {
                synchronized (Daemon.lock) {
                    Daemon.lock.notify();
                }
                Daemon.LOG.info("Stopping SMSC server daemon");
                Daemon.server.stop();
            }
        } catch (Throwable t) {
            Daemon.LOG.error("Daemon error", t);
        }
    }
}
