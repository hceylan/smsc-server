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

package org.apache.smscserver.util;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.smscserver.smsclet.SmscException;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * This class encapsulates <code>java.util.Properties</code> to add java
 * primitives and some other java classes.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class BaseProperties extends Properties {

    private static final long serialVersionUID = 5572645129592131953L;

    /**
     * Default constructor.
     */
    public BaseProperties() {
    }

    /**
     * Load existing property.
     */
    public BaseProperties(final Properties prop) {
        super(prop);
    }

    // ////////////////////////////////////////
    // ////// Properties Get Methods ////////
    // ////////////////////////////////////////
    /**
     * Get boolean value.
     */
    public boolean getBoolean(final String str) throws SmscException {
        String prop = getProperty(str);
        if (prop == null) {
            throw new SmscException(str + " not found");
        }

        return prop.toLowerCase().equals("true");
    }

    public boolean getBoolean(final String str, final boolean bol) {
        try {
            return getBoolean(str);
        } catch (SmscException ex) {
            return bol;
        }
    }

    /**
     * Get integer value.
     */
    public int getInteger(final String str) throws SmscException {
        String value = getProperty(str);
        if (value == null) {
            throw new SmscException(str + " not found");
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new SmscException("BaseProperties.getInteger()", ex);
        }
    }

    public int getInteger(final String str, final int intVal) {
        try {
            return getInteger(str);
        } catch (SmscException ex) {
            return intVal;
        }
    }

    /**
     * Get long value.
     */
    public long getLong(final String str) throws SmscException {
        String value = getProperty(str);
        if (value == null) {
            throw new SmscException(str + " not found");
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new SmscException("BaseProperties.getLong()", ex);
        }
    }

    public long getLong(final String str, final long val) {
        try {
            return getLong(str);
        } catch (SmscException ex) {
            return val;
        }
    }

    /**
     * Get double value.
     */
    public double getDouble(final String str) throws SmscException {
        String value = getProperty(str);
        if (value == null) {
            throw new SmscException(str + " not found");
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new SmscException("BaseProperties.getDouble()", ex);
        }
    }

    public double getDouble(final String str, final double doubleVal) {
        try {
            return getDouble(str);
        } catch (SmscException ex) {
            return doubleVal;
        }
    }

    /**
     * Get <code>InetAddress</code>.
     */
    public InetAddress getInetAddress(final String str) throws SmscException {
        String value = getProperty(str);
        if (value == null) {
            throw new SmscException(str + " not found");
        }

        try {
            return InetAddress.getByName(value);
        } catch (UnknownHostException ex) {
            throw new SmscException("Host " + value + " not found");
        }
    }

    public InetAddress getInetAddress(final String str, final InetAddress addr) {
        try {
            return getInetAddress(str);
        } catch (SmscException ex) {
            return addr;
        }
    }

    /**
     * Get <code>String</code>.
     */
    public String getString(final String str) throws SmscException {
        String value = getProperty(str);
        if (value == null) {
            throw new SmscException(str + " not found");
        }

        return value;
    }

    public String getString(final String str, final String s) {
        try {
            return getString(str);
        } catch (SmscException ex) {
            return s;
        }
    }

    /**
     * Get <code>File</code> object.
     */
    public File getFile(final String str) throws SmscException {
        String value = getProperty(str);
        if (value == null) {
            throw new SmscException(str + " not found");
        }
        return new File(value);
    }

    public File getFile(final String str, final File fl) {
        try {
            return getFile(str);
        } catch (SmscException ex) {
            return fl;
        }
    }

    /**
     * Get <code>Class</code> object
     */
    public Class<?> getClass(final String str) throws SmscException {
        String value = getProperty(str);
        if (value == null) {
            throw new SmscException(str + " not found");
        }

        try {
            return Class.forName(value);
        } catch (ClassNotFoundException ex) {
            throw new SmscException("BaseProperties.getClass()", ex);
        }
    }

    public Class<?> getClass(final String str, final Class<?> cls) {
        try {
            return getClass(str);
        } catch (SmscException ex) {
            return cls;
        }
    }

    /**
     * Get <code>TimeZone</code>
     */
    public TimeZone getTimeZone(final String str) throws SmscException {
        String value = getProperty(str);
        if (value == null) {
            throw new SmscException(str + " not found");
        }
        return TimeZone.getTimeZone(value);
    }

    public TimeZone getTimeZone(final String str, final TimeZone tz) {
        try {
            return getTimeZone(str);
        } catch (SmscException ex) {
            return tz;
        }
    }

    /**
     * Get <code>DateFormat</code> object.
     */
    public SimpleDateFormat getDateFormat(final String str) throws SmscException {
        String value = getProperty(str);
        if (value == null) {
            throw new SmscException(str + " not found");
        }
        try {
            return new SimpleDateFormat(value);
        } catch (IllegalArgumentException e) {
            throw new SmscException("Date format was incorrect: " + value, e);
        }
    }

    public SimpleDateFormat getDateFormat(final String str,
            final SimpleDateFormat fmt) {
        try {
            return getDateFormat(str);
        } catch (SmscException ex) {
            return fmt;
        }
    }

    /**
     * Get <code>Date</code> object.
     */
    public Date getDate(final String str, final DateFormat fmt)
            throws SmscException {
        String value = getProperty(str);
        if (value == null) {
            throw new SmscException(str + " not found");
        }

        try {
            return fmt.parse(value);
        } catch (ParseException ex) {
            throw new SmscException("BaseProperties.getdate()", ex);
        }
    }

    public Date getDate(final String str, final DateFormat fmt, final Date dt) {
        try {
            return getDate(str, fmt);
        } catch (SmscException ex) {
            return dt;
        }
    }

    // ////////////////////////////////////////
    // ////// Properties Set Methods ////////
    // ////////////////////////////////////////
    /**
     * Set boolean value.
     */
    public void setProperty(final String key, final boolean val) {
        setProperty(key, String.valueOf(val));
    }

    /**
     * Set integer value.
     */
    public void setProperty(final String key, final int val) {
        setProperty(key, String.valueOf(val));
    }

    /**
     * Set double value.
     */
    public void setProperty(final String key, final double val) {
        setProperty(key, String.valueOf(val));
    }

    /**
     * Set float value.
     */
    public void setProperty(final String key, final float val) {
        setProperty(key, String.valueOf(val));
    }

    /**
     * Set long value.
     */
    public void setProperty(final String key, final long val) {
        setProperty(key, String.valueOf(val));
    }

    /**
     * Set <code>InetAddress</code>.
     */
    public void setInetAddress(final String key, final InetAddress val) {
        setProperty(key, val.getHostAddress());
    }

    /**
     * Set <code>File</code> object.
     */
    public void setProperty(final String key, final File val) {
        setProperty(key, val.getAbsolutePath());
    }

    /**
     * Set <code>DateFormat</code> object.
     */
    public void setProperty(final String key, final SimpleDateFormat val) {
        setProperty(key, val.toPattern());
    }

    /**
     * Set <code>TimeZone</code> object.
     */
    public void setProperty(final String key, final TimeZone val) {
        setProperty(key, val.getID());
    }

    /**
     * Set <code>Date</code> object.
     */
    public void setProperty(final String key, final Date val,
            final DateFormat fmt) {
        setProperty(key, fmt.format(val));
    }

    /**
     * Set <code>Class</code> object.
     */
    public void setProperty(final String key, final Class<?> val) {
        setProperty(key, val.getName());
    }

}
