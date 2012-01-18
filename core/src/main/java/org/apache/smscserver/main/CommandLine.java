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
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * This class is the starting point for the SmscServer when it is started using the command line mode.
 * 
 * @author hceylan
 */
public class CommandLine {

    /**
     * This method is the SmscServer starting point when running by using the command line mode.
     * 
     * @param args
     *            The first element of this array must specify the kind of configuration to be used to start the server.
     */
    public static void main(String args[]) {

        CommandLine cli = new CommandLine();
        try {

            // get configuration
            SmscServer server = cli.getConfiguration(args);
            if (server == null) {
                return;
            }

            // start the server
            server.start();
            System.out.println("SmscServer started");

            // add shutdown hook if possible
            cli.addShutdownHook(server);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * The purpose of this class is to allow the final user to start the SmscServer application. Because of that it has
     * only <code>static</code> methods and cannot be instanced.
     */
    protected CommandLine() {
    }

    /**
     * Add shutdown hook.
     */
    private void addShutdownHook(final SmscServer engine) {

        // create shutdown hook
        Runnable shutdownHook = new Runnable() {
            public void run() {
                System.out.println("Stopping server...");
                engine.stop();
            }
        };

        // add shutdown hook
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(shutdownHook));
    }

    /**
     * Get the configuration object.
     */
    protected SmscServer getConfiguration(String[] args) throws Exception {

        SmscServer server = null;
        if (args.length == 0) {
            System.out.println("Using default configuration");
            server = new SmscServerFactory().createServer();
        } else if ((args.length == 1) && args[0].equals("-default")) {
            // supported for backwards compatibility, but not documented
            System.out.println("The -default switch is deprecated, please use --default instead");
            System.out.println("Using default configuration");
            server = new SmscServerFactory().createServer();
        } else if ((args.length == 1) && args[0].equals("--default")) {
            System.out.println("Using default configuration");
            server = new SmscServerFactory().createServer();
        } else if ((args.length == 1) && args[0].equals("--help")) {
            this.usage();
        } else if ((args.length == 1) && args[0].equals("-?")) {
            this.usage();
        } else if (args.length == 1) {
            System.out.println("Using XML configuration file " + args[0] + "...");
            FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(args[0]);

            if (ctx.containsBean("server")) {
                server = (SmscServer) ctx.getBean("server");
            } else {
                String[] beanNames = ctx.getBeanNamesForType(SmscServer.class);
                if (beanNames.length == 1) {
                    server = (SmscServer) ctx.getBean(beanNames[0]);
                } else if (beanNames.length > 1) {
                    System.err.println("Using the first server defined in the configuration, named " + beanNames[0]);
                    server = (SmscServer) ctx.getBean(beanNames[0]);
                } else {
                    System.err.println("XML configuration does not contain a server configuration");
                }
            }
        } else {
            this.usage();
        }

        return server;
    }

    /**
     * Print the usage message.
     */
    protected void usage() {
        System.err.println("Usage: java org.apache.smscserver.test.main.CommandLine [OPTION] [CONFIGFILE]");
        System.err.println("Starts SmscServer using the default configuration of the ");
        System.err.println("configuration file if provided.");
        System.err.println("");
        System.err.println("      --default              use the default configuration, ");
        System.err.println("                             also used if no command line argument is given ");
        System.err.println("  -?, --help                 print this message");
    }
}