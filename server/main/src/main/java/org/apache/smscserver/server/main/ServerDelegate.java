package org.apache.smscserver.server.main;

import org.apache.smscserver.server.bootstrap.Server;
import org.apache.smscserver.server.main.impl.DualClassLoader;


/**
 * @author hceylan
 * 
 */
public class ServerDelegate {

    /**
     * {@inheritDoc}
     * 
     */
    public void run(String serverHome) throws Exception {
        DualClassLoader classLoader = new DualClassLoader(this.getClass().getClassLoader().getParent(), serverHome);

        Thread.currentThread().setContextClassLoader(classLoader);
        Class<?> serverClass = classLoader.loadClass("org.apache.smscserver.server.main.impl.SMSCServer");
        Server server = (Server) serverClass.newInstance();

        server.run(classLoader, serverHome);
    }
}
