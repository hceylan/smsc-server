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
 * This class encapsulates <code>java.util.Properties</code> to add java primitives and some other java classes.
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
        String prop = this.getProperty(str);
        if (prop == null) {
            throw new SmscException(str + " not found");
        }

        return prop.toLowerCase().equals("true");
    }

    public boolean getBoolean(final String str, final boolean bol) {
        try {
            return this.getBoolean(str);
        } catch (SmscException ex) {
            return bol;
        }
    }

    /**
     * Get <code>Class</code> object
     */
    public Class<?> getClass(final String str) throws SmscException {
        String value = this.getProperty(str);
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
            return this.getClass(str);
        } catch (SmscException ex) {
            return cls;
        }
    }

    /**
     * Get <code>Date</code> object.
     */
    public Date getDate(final String str, final DateFormat fmt) throws SmscException {
        String value = this.getProperty(str);
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
            return this.getDate(str, fmt);
        } catch (SmscException ex) {
            return dt;
        }
    }

    /**
     * Get <code>DateFormat</code> object.
     */
    public SimpleDateFormat getDateFormat(final String str) throws SmscException {
        String value = this.getProperty(str);
        if (value == null) {
            throw new SmscException(str + " not found");
        }
        try {
            return new SimpleDateFormat(value);
        } catch (IllegalArgumentException e) {
            throw new SmscException("Date format was incorrect: " + value, e);
        }
    }

    public SimpleDateFormat getDateFormat(final String str, final SimpleDateFormat fmt) {
        try {
            return this.getDateFormat(str);
        } catch (SmscException ex) {
            return fmt;
        }
    }

    /**
     * Get double value.
     */
    public double getDouble(final String str) throws SmscException {
        String value = this.getProperty(str);
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
            return this.getDouble(str);
        } catch (SmscException ex) {
            return doubleVal;
        }
    }

    /**
     * Get <code>File</code> object.
     */
    public File getFile(final String str) throws SmscException {
        String value = this.getProperty(str);
        if (value == null) {
            throw new SmscException(str + " not found");
        }
        return new File(value);
    }

    public File getFile(final String str, final File fl) {
        try {
            return this.getFile(str);
        } catch (SmscException ex) {
            return fl;
        }
    }

    /**
     * Get <code>InetAddress</code>.
     */
    public InetAddress getInetAddress(final String str) throws SmscException {
        String value = this.getProperty(str);
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
            return this.getInetAddress(str);
        } catch (SmscException ex) {
            return addr;
        }
    }

    /**
     * Get integer value.
     */
    public int getInteger(final String str) throws SmscException {
        String value = this.getProperty(str);
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
            return this.getInteger(str);
        } catch (SmscException ex) {
            return intVal;
        }
    }

    /**
     * Get long value.
     */
    public long getLong(final String str) throws SmscException {
        String value = this.getProperty(str);
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
            return this.getLong(str);
        } catch (SmscException ex) {
            return val;
        }
    }

    /**
     * Get <code>String</code>.
     */
    public String getString(final String str) throws SmscException {
        String value = this.getProperty(str);
        if (value == null) {
            throw new SmscException(str + " not found");
        }

        return value;
    }

    public String getString(final String str, final String s) {
        try {
            return this.getString(str);
        } catch (SmscException ex) {
            return s;
        }
    }

    /**
     * Get <code>TimeZone</code>
     */
    public TimeZone getTimeZone(final String str) throws SmscException {
        String value = this.getProperty(str);
        if (value == null) {
            throw new SmscException(str + " not found");
        }
        return TimeZone.getTimeZone(value);
    }

    public TimeZone getTimeZone(final String str, final TimeZone tz) {
        try {
            return this.getTimeZone(str);
        } catch (SmscException ex) {
            return tz;
        }
    }

    /**
     * Set <code>InetAddress</code>.
     */
    public void setInetAddress(final String key, final InetAddress val) {
        this.setProperty(key, val.getHostAddress());
    }

    // ////////////////////////////////////////
    // ////// Properties Set Methods ////////
    // ////////////////////////////////////////
    /**
     * Set boolean value.
     */
    public void setProperty(final String key, final boolean val) {
        this.setProperty(key, String.valueOf(val));
    }

    /**
     * Set <code>Class</code> object.
     */
    public void setProperty(final String key, final Class<?> val) {
        this.setProperty(key, val.getName());
    }

    /**
     * Set <code>Date</code> object.
     */
    public void setProperty(final String key, final Date val, final DateFormat fmt) {
        this.setProperty(key, fmt.format(val));
    }

    /**
     * Set double value.
     */
    public void setProperty(final String key, final double val) {
        this.setProperty(key, String.valueOf(val));
    }

    /**
     * Set <code>File</code> object.
     */
    public void setProperty(final String key, final File val) {
        this.setProperty(key, val.getAbsolutePath());
    }

    /**
     * Set float value.
     */
    public void setProperty(final String key, final float val) {
        this.setProperty(key, String.valueOf(val));
    }

    /**
     * Set integer value.
     */
    public void setProperty(final String key, final int val) {
        this.setProperty(key, String.valueOf(val));
    }

    /**
     * Set long value.
     */
    public void setProperty(final String key, final long val) {
        this.setProperty(key, String.valueOf(val));
    }

    /**
     * Set <code>DateFormat</code> object.
     */
    public void setProperty(final String key, final SimpleDateFormat val) {
        this.setProperty(key, val.toPattern());
    }

    /**
     * Set <code>TimeZone</code> object.
     */
    public void setProperty(final String key, final TimeZone val) {
        this.setProperty(key, val.getID());
    }

}
