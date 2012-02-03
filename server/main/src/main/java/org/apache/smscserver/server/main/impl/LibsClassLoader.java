/**
 * 
 */
package org.apache.smscserver.server.main.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A class loader to load classes from lib jars
 * 
 * @author hceylan
 * 
 */
public class LibsClassLoader extends ChildClassLoader {

	private static final String PATH_SEPARATOR = System.getProperty("file.separator", "/");
	private static final String LIB_PATH = LibsClassLoader.PATH_SEPARATOR + "lib";

	private static URL[] getLibJars(String spHome) {
		List<URL> urls = new ArrayList<URL>();

		String libPath = spHome + LibsClassLoader.LIB_PATH;
		File libFolder = new File(libPath);
		String[] files = libFolder.list();

		for (String file : files) {
			if (file.endsWith(".jar")) {
				try {
					urls.add(new URL("file://" + libPath + LibsClassLoader.PATH_SEPARATOR + file));
				} catch (MalformedURLException e) {
					System.err.println("Ignoring library " + file + ": " + e.getMessage());
				}
			}
		}

		return urls.toArray(new URL[urls.size()]);
	}

	public LibsClassLoader(String spHome, DualClassLoader parent) {
		super(LibsClassLoader.getLibJars(spHome), parent);
	}

}
