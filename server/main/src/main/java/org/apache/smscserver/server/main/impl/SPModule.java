package org.apache.smscserver.server.main.impl;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.smscserver.server.main.SPLogger;

/**
 * Modules deployment descriptor
 * 
 * @author hceylan
 * 
 */
public class SPModule {

    private static final SPLogger LOG = SPLogger.getLogger(SPModule.class);

    private static final String BIN_PATH = File.separator + "bin";

    private final String modulePath;
    private final String module;
    private String[] jars;

    private ClassLoader classLoader;

    public SPModule(ClassLoader parentClassloader, String module, String modulePath) {
        super();

        this.module = module;
        this.modulePath = modulePath;

        this.initModule(parentClassloader);
    }

    public void deploy() {
        // TODO Implement
    }

    /**
     * Returns the module class loader.
     * 
     * @return the module class loader
     */
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /**
     * Returns the property of the module.
     * 
     * @param property
     *            name of the property
     * @return the property of the module
     */
    public Object getProperty(String property) {
        // TODO Implement
        return null;
    }

    private void initModule(ClassLoader parentClassloader) {
        String binPath = this.modulePath + SPModule.BIN_PATH;

        this.jars = new File(binPath).list();

        URL[] urls = new URL[this.jars.length];
        int i = 0;
        for (String jar : this.jars) {
            String url = "file://" + binPath + File.separator + jar;
            try {
                urls[i++] = new URL(url);
            } catch (MalformedURLException e) {
                SPModule.LOG.error(e, "ignoring jar file {0}", url);
            }

            SPModule.LOG.info("Module [{0}], jar [{1}] added to the module classpath", this.module, jar);
        }

        try {
            Class<?> clazz = parentClassloader.loadClass("org.apache.smscserver.server.bootstrap.impl.ChildClassLoader");
            Constructor<?> constructor = clazz.getConstructor(URL[].class, ClassLoader.class);
            this.classLoader = (ClassLoader) constructor.newInstance(urls, parentClassloader);
        } catch (Exception e) {
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this) //
                .append("module", this.module) //
                .toString();
    }
}
