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

package org.apache.smscserver.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Enumeration;

import junit.framework.Assert;

import org.apache.commons.codec.binary.Hex;
import org.apache.smscserver.util.IoUtils;

/**
 * 
 * @author hceylan
 * 
 */
public class TestUtil {

    private static final int DEFAULT_PORT = 12321;

    public static void assertArraysEqual(byte[] expected, byte[] actual) {
        Assert.assertEquals(new String(Hex.encodeHex(expected)), new String(Hex.encodeHex(actual)));
    }

    public static void assertFileEqual(byte[] expected, File file) throws Exception {
        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;

        try {
            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(file);

            IoUtils.copy(fis, baos, 1024);

            byte[] actual = baos.toByteArray();

            TestUtil.assertArraysEqual(expected, actual);
        } finally {
            IoUtils.close(fis);
            IoUtils.close(baos);
        }
    }

    public static void assertInArrays(Object expected, Object[] actual) {
        boolean found = false;
        for (Object object : actual) {
            if (object.equals(expected)) {
                found = true;
                break;
            }
        }

        if (!found) {
            Assert.fail("Expected value not in array");
        }
    }

    /**
     * Attempts to find a free port
     * 
     * @throws IOException
     * 
     * @throws IOException
     */
    public static int findFreePort() throws IOException {
        return TestUtil.findFreePort(TestUtil.DEFAULT_PORT);
    }

    /**
     * Attempts to find a free port
     * 
     * @param initPort
     *            The first port to try, before resolving to brute force searching
     * @throws IOException
     * 
     * @throws IOException
     */
    public static int findFreePort(int initPort) throws IOException {
        int port = -1;
        ServerSocket tmpSocket = null;
        // first try the default port
        try {
            tmpSocket = new ServerSocket(initPort);

            port = initPort;

            System.out.println("Using default port: " + port);
        } catch (IOException e) {
            System.out.println("Failed to use specified port");
            // didn't work, try to find one dynamically
            try {
                int attempts = 0;

                while ((port < 1024) && (attempts < 2000)) {
                    attempts++;

                    tmpSocket = new ServerSocket(0);

                    port = tmpSocket.getLocalPort();
                }

            } catch (IOException e1) {
                throw new IOException("Failed to find a port to use for testing: " + e1.getMessage());
            }
        } finally {
            if (tmpSocket != null) {
                try {
                    tmpSocket.close();
                } catch (IOException e) {
                    // ignore
                }
                tmpSocket = null;
            }
        }

        return port;
    }

    public static InetAddress findNonLocalhostIp() throws Exception {
        Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();

        while (nifs.hasMoreElements()) {
            NetworkInterface nif = nifs.nextElement();
            Enumeration<InetAddress> ips = nif.getInetAddresses();

            while (ips.hasMoreElements()) {
                InetAddress ip = ips.nextElement();
                if ((ip instanceof java.net.Inet4Address) && !ip.isLoopbackAddress()) {
                    return ip;
                } else {
                    // IPv6 not tested
                }
            }
        }

        return null;
    }

    public static File getBaseDir() {
        // check Maven system prop first and use if set
        String basedir = System.getProperty("basedir");
        if (basedir != null) {
            return new File(basedir);
        } else {
            // Are we using Eclipse based on parent directory?
            File core = new File("core");
            File check = new File(core, "src");
            if (check.isDirectory()) {
                return core;
            }
            return new File(".");
        }
    }

}
