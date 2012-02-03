/**
 * 
 */
package org.apache.smscserver.server.main.impl;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A Classloader that traverses both libs and modules to load classes.
 * 
 * @author hceylan
 * 
 * 
 */
public class DualClassLoader extends ClassLoader {

	private final LibsClassLoader libsClassLoader;

	private final List<ClassLoader> moduleClassLoaders;

	public DualClassLoader(ClassLoader parent, String spHome) {
		super(parent);

		this.libsClassLoader = new LibsClassLoader(spHome, this);
		this.moduleClassLoaders = new ArrayList<ClassLoader>();
	}

	/**
	 * Adds a module to the classloader.
	 * 
	 * @param classLoader
	 *            the classloader of the module
	 */
	public void addModuleClassLoader(ClassLoader classLoader) {
		this.moduleClassLoaders.add(classLoader);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> c = null;

		try {
			c = this.libsClassLoader.findClass(name);

			if (c != null) {
				return c;
			}
		} catch (ClassNotFoundException e) {
		}

		for (ClassLoader classLoader : this.moduleClassLoaders) {
			try {
				Class<? extends ClassLoader> clazz = classLoader.getClass();
				Method method = clazz.getMethod("findClass", String.class);
				method.setAccessible(true);
				c = (Class<?>) method.invoke(classLoader, name);

				if (c != null) {
					return c;
				}
			} catch (Exception e) {
			}
		}

		throw new ClassNotFoundException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected URL findResource(String name) {
		URL url = null;

		url = this.libsClassLoader.findResource(name);

		if (url != null) {
			return url;
		}

		for (ClassLoader classLoader : this.moduleClassLoaders) {
			try {

				Method method = classLoader.getClass().getMethod("findResource", String.class);
				method.setAccessible(true);
				url = (URL) method.invoke(classLoader, name);

				if (url != null) {
					return url;
				}
			} catch (Exception e) {
			}
		}

		return null;
	}

	public LibsClassLoader getLibsClassLoader() {
		return this.libsClassLoader;
	}
}
