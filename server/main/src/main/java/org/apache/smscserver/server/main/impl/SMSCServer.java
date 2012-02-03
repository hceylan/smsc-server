/**
 * 
 */
package org.apache.smscserver.server.main.impl;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.smscserver.server.main.SPLogger;

import com.ericsson.service.server.Server;

/**
 * The main point for Service Portal
 * 
 * @author hceylan
 * 
 */
public class SMSCServer implements Server {

    private static final String LOGJ_CONFIG = File.separator + "conf/log4j.xml";

    private static final SPLogger LOG = SPLogger.getLogger(SMSCServer.class);

    private static final String MODULES_PATH = File.separator + "modules";

    private String serverHome;
    private String modulesPath;
    private final Map<String, SPModule> modules;

    private ClassLoader classLoader;

    /**
     * No External instantiation
     * 
     * @param spHome
     */
    public SMSCServer() {
        super();

        this.modules = new HashMap<String, SPModule>();
    }

    /**
     * Returns the module with the name moduleName.
     * 
     * @param moduleName
     *            name of the module
     * @return
     * @return the module with the name moduleName
     */
    public SPModule getModule(String moduleName) {
        return this.modules.get(moduleName);
    }

    /**
     * Returns the modules in the server.
     * 
     * @return the modules in the server
     */
    public Collection<SPModule> getModules() {
        return this.modules.values();
    }

    /**
     * Returns the Service Delivery Platform home path.
     * 
     * @return the Service Delivery Platform home path
     */
    public String getSPHome() {
        return this.serverHome;
    }

    private void initializeModules() throws Exception {
        SMSCServer.LOG.info("Initializing modules...");

        this.modulesPath = this.serverHome + SMSCServer.MODULES_PATH;
        String[] modules = new File(this.modulesPath).list();

        for (String module : modules) {
            SMSCServer.LOG.info("Found module {0}", module);

            SPModule spModule = new SPModule(this.classLoader, module, this.modulesPath + File.separator + module);

            this.modules.put(module, spModule);

            Method method = this.classLoader.getClass().getMethod("addModuleClassLoader", ClassLoader.class);
            method.invoke(this.classLoader, spModule.getClassLoader());

            spModule.deploy();
        }

        SMSCServer.LOG.info("Modules have been initialized successfully");
    }

    /**
     * Runs the server
     * 
     * @param classLoader
     *            the parent class loader
     * @param serverHome
     *            the home directory of SMSC Server
     */
    public void run(ClassLoader classLoader, String serverHome) {
        this.classLoader = classLoader;
        this.serverHome = serverHome;

        DOMConfigurator.configureAndWatch(serverHome + SMSCServer.LOGJ_CONFIG);

        long start = System.currentTimeMillis();

        SMSCServer.LOG.info("Starting Service Portal Server...");

        try {
            this.initializeModules();

            long time = (System.currentTimeMillis() - start);

            SMSCServer.LOG.info("Service Portal has started successfully in {0} miliseconds", time);
        } catch (Exception e) {
            SMSCServer.LOG.fatal(e, "Service Portal cannot be started due to a fatal error");

            System.exit(1);
        }
    }
}
