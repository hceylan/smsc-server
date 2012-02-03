package org.apache.smscserver.server.main.impl;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * @author hceylan
 * 
 */
public class ChildClassLoader extends URLClassLoader {

	public ChildClassLoader(URL[] urls) {
		super(urls);
	}

	public ChildClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public ChildClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> c = this.findLoadedClass(name);

		if (c != null) {
			return c;
		}

		return super.findClass(name);
	}
}