/**
 * 
 */
package com.ericsson.service.server;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Main entry point for the deployment
 * 
 * @author hceylan
 * 
 */
public class Main {

    private static BootClassLoader createClassLoader(String homeDirectory) {
        Thread thread = Thread.currentThread();
        BootClassLoader bootClassLoader = new BootClassLoader(thread.getContextClassLoader(), homeDirectory);
        thread.setContextClassLoader(bootClassLoader);

        return bootClassLoader;
    }

    private static String getHomeDirectory() throws IOException {
        String spHome = System.getProperty("SP_HOME");
        if (spHome == null) {
            System.err.println("SP_HOME is not set, using current working directory...!");
        } else {
            return new File(spHome).getCanonicalPath();
        }

        return new File(".").getCanonicalPath();
    }

    public static void main(String[] args) {
        String spHome;
        try {
            spHome = Main.getHomeDirectory();

            BootClassLoader spClassLoader = Main.createClassLoader(spHome);

            Class<?> clazz = spClassLoader.loadClass("org.apache.smscserver.server.main.ServerDelegate");
            Method method = clazz.getMethod("run", String.class);
            method.setAccessible(true);

            Object server = clazz.newInstance();
            method.invoke(server, spHome);

        } catch (Exception e) {
            System.err.println("Unable to boot Service Platform!");

            e.printStackTrace();
        }
    }

    private Main() {
        // no intantiation
    }
}
