package org.apache.smscserver.server.bootstrap;

/**
 * Interface for the server
 * 
 * @author hceylan
 * 
 */
public interface Server {

    void run(ClassLoader classLoader, String serverHome) throws Exception;

}
