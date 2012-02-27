/**
 * 
 */
package org.apache.smscserver.server.bootstrap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * A Specialized class loader for Service Portal
 * 
 * @author hceylan
 * 
 */
public class BootClassLoader extends URLClassLoader {

	private static final String PATH_SEPARATOR = System.getProperty("file.separator", "/");
	private static final String LIB_PATH = BootClassLoader.PATH_SEPARATOR + "lib";

	private static URL[] getLibJars(String spHome) {
		List<URL> urls = new ArrayList<URL>();

		String libPath = spHome + BootClassLoader.LIB_PATH;
		File libFolder = new File(libPath);
		String[] files = libFolder.list();

		for (String file : files) {
			if (file.endsWith(".jar")) {
				try {
					urls.add(new URL("file://" + libPath + BootClassLoader.PATH_SEPARATOR + file));
				} catch (MalformedURLException e) {
					System.err.println("Ignoring library " + file + ": " + e.getMessage());
				}
			}
		}

		return urls.toArray(new URL[urls.size()]);
	}

	public BootClassLoader(ClassLoader parent, String spHome) {
		super(BootClassLoader.getLibJars(spHome), parent);
	}

}
