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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.smscserver.smsclet.SmscException;

/**
 * 
 * @author hceylan
 * 
 */
public class BasePropertiesTest extends TestCase {

    public void testGetBoolean() throws SmscException {
        BaseProperties props = new BaseProperties();
        props.setProperty("bool1", "true");
        props.setProperty("bool2", "TRUE");
        props.setProperty("bool3", "True");
        props.setProperty("bool4", "false");
        props.setProperty("bool5", "FALSE");
        props.setProperty("bool6", "False");
        props.setProperty("bool7", "foo");
        props.setProperty("bool8", "");

        Assert.assertEquals(true, props.getBoolean("bool1"));
        Assert.assertEquals(true, props.getBoolean("bool2"));
        Assert.assertEquals(true, props.getBoolean("bool3"));
        Assert.assertEquals(false, props.getBoolean("bool4"));
        Assert.assertEquals(false, props.getBoolean("bool5"));
        Assert.assertEquals(false, props.getBoolean("bool6"));
        Assert.assertEquals(false, props.getBoolean("bool7"));
        Assert.assertEquals(false, props.getBoolean("bool8"));

        // Unknown key
        try {
            props.getBoolean("foo");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // default values
        Assert.assertEquals(true, props.getBoolean("foo", true));
        Assert.assertEquals(false, props.getBoolean("foo", false));
        Assert.assertEquals(true, props.getBoolean("bool1", false));
        Assert.assertEquals(false, props.getBoolean("bool4", true));
    }

    public void testGetClass() throws SmscException {
        BaseProperties props = new BaseProperties();
        props.setProperty("c1", "java.lang.String");
        props.setProperty("c2", "foo");

        Assert.assertEquals(String.class, props.getClass("c1"));

        try {
            props.getClass("c2");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // Unknown value
        try {
            props.getClass("foo");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // default values
        Assert.assertEquals(String.class, props.getClass("c1", Integer.class));
        Assert.assertEquals(Integer.class, props.getClass("c2", Integer.class));
        Assert.assertEquals(Integer.class, props.getClass("foo", Integer.class));
    }

    public void testGetDate() throws SmscException {
        Date d1 = new Date();
        Date d2 = new Date(100);
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSSzzz");

        BaseProperties props = new BaseProperties();
        props.setProperty("d1", format.format(d1));
        props.setProperty("d2", "foo");

        Assert.assertEquals(d1, props.getDate("d1", format));

        try {
            props.getDate("d2", format);
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // Unknown value
        try {
            props.getDate("foo", format);
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // default values
        Assert.assertEquals(d1, props.getDate("d1", format, d2));
        Assert.assertEquals(d2, props.getDate("d2", format, d2));
        Assert.assertEquals(d2, props.getDate("foo", format, d2));
    }

    public void testGetDateFormat() throws SmscException {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmssSSSzzz");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy");

        BaseProperties props = new BaseProperties();
        props.setProperty("d1", "yyyyMMddHHmmssSSSzzz");
        props.setProperty("d2", "foo");

        Assert.assertEquals(format1, props.getDateFormat("d1"));

        try {
            props.getDateFormat("d2");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // Unknown value
        try {
            props.getDateFormat("foo");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // default values
        Assert.assertEquals(format1, props.getDateFormat("d1", format2));
        Assert.assertEquals(format2, props.getDateFormat("d2", format2));
        Assert.assertEquals(format2, props.getDateFormat("foo", format2));
    }

    public void testGetDouble() throws SmscException {
        BaseProperties props = new BaseProperties();
        props.setProperty("d1", "1");
        props.setProperty("d2", "1.23");
        props.setProperty("d3", "1,23");
        props.setProperty("d4", "foo");
        props.setProperty("d5", "");

        Assert.assertEquals(1D, props.getDouble("d1"), 0.1);
        Assert.assertEquals(1.23D, props.getDouble("d2"), 0.1);

        try {
            props.getDouble("d3");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }
        try {
            props.getDouble("d4");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }
        try {
            props.getDouble("d5");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // Unknown value
        try {
            props.getDouble("foo");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // default values
        Assert.assertEquals(1, props.getDouble("d1", 7), 0.1);
        Assert.assertEquals(7, props.getDouble("d3", 7), 0.1);
        Assert.assertEquals(7, props.getDouble("d4", 7), 0.1);
        Assert.assertEquals(7, props.getDouble("d5", 7), 0.1);
        Assert.assertEquals(7, props.getDouble("foo", 7), 0.1);
    }

    public void testGetFile() throws SmscException {
        File file1 = new File("test-tmp/test1.txt").getAbsoluteFile();
        File file2 = new File("test-tmp/test2.txt").getAbsoluteFile();

        BaseProperties props = new BaseProperties();
        props.setProperty("f1", file1.getAbsolutePath());

        Assert.assertEquals(file1, props.getFile("f1"));

        // Unknown value
        try {
            props.getFile("foo");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // default values
        Assert.assertEquals(file1, props.getFile("f1", file2));
        Assert.assertEquals(file2, props.getFile("foo", file2));
    }

    public void testGetInetAddress() throws SmscException, UnknownHostException {
        InetAddress a1 = InetAddress.getByName("1.2.3.4");
        InetAddress a2 = InetAddress.getByName("localhost");
        InetAddress a3 = InetAddress.getByName("1.2.3.5");

        BaseProperties props = new BaseProperties();
        props.setProperty("a1", "1.2.3.4");
        props.setProperty("a2", "localhost");
        props.setProperty("a4", "1.2.3.4.5.6.7.8.9");

        Assert.assertEquals(a1, props.getInetAddress("a1"));
        Assert.assertEquals(a2, props.getInetAddress("a2"));

        // Unknown value
        try {
            props.getInetAddress("foo");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // Incorrect host name
        try {
            props.getInetAddress("a4");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // default values
        Assert.assertEquals(a1, props.getInetAddress("a1", a3));
        Assert.assertEquals(a3, props.getInetAddress("foo", a3));
    }

    public void testGetInteger() throws SmscException {
        BaseProperties props = new BaseProperties();
        props.setProperty("int1", "1");
        props.setProperty("int2", "123");
        props.setProperty("int3", "1.23");
        props.setProperty("int4", "foo");
        props.setProperty("int5", "");
        props.setProperty("int6", "99999999999999999");

        Assert.assertEquals(1, props.getInteger("int1"));
        Assert.assertEquals(123, props.getInteger("int2"));

        try {
            props.getInteger("int3");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }
        try {
            props.getInteger("int4");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }
        try {
            props.getInteger("int5");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }
        try {
            props.getInteger("int6");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // Unknown value
        try {
            props.getInteger("foo");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // default values
        Assert.assertEquals(1, props.getInteger("int1", 7));
        Assert.assertEquals(7, props.getInteger("int3", 7));
        Assert.assertEquals(7, props.getInteger("int4", 7));
        Assert.assertEquals(7, props.getInteger("int5", 7));
        Assert.assertEquals(7, props.getInteger("int6", 7));
        Assert.assertEquals(7, props.getInteger("foo", 7));
    }

    public void testGetLong() throws SmscException {
        BaseProperties props = new BaseProperties();
        props.setProperty("l1", "1");
        props.setProperty("l2", "123");
        props.setProperty("l3", "1.23");
        props.setProperty("l4", "foo");
        props.setProperty("l5", "");
        props.setProperty("l6", "99999999999999999");

        Assert.assertEquals(1, props.getLong("l1"));
        Assert.assertEquals(123, props.getLong("l2"));
        Assert.assertEquals(99999999999999999L, props.getLong("l6"));

        try {
            props.getLong("l3");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }
        try {
            props.getLong("l4");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }
        try {
            props.getLong("l5");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // Unknown value
        try {
            props.getLong("foo");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // default values
        Assert.assertEquals(1, props.getLong("l1", 7));
        Assert.assertEquals(7, props.getLong("l3", 7));
        Assert.assertEquals(7, props.getLong("l4", 7));
        Assert.assertEquals(7, props.getLong("l5", 7));
        Assert.assertEquals(7, props.getLong("foo", 7));
    }

    public void testGetString() throws SmscException {
        BaseProperties props = new BaseProperties();
        props.setProperty("s1", "bar");

        Assert.assertEquals("bar", props.getString("s1"));

        // Unknown value
        try {
            props.getString("foo");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // default values
        Assert.assertEquals("bar", props.getString("s1", "baz"));
        Assert.assertEquals("baz", props.getString("foo", "baz"));
    }

    public void testGetTimeZone() throws SmscException {
        TimeZone tz1 = TimeZone.getTimeZone("PST");
        TimeZone tz2 = TimeZone.getTimeZone("GMT-8:00");
        TimeZone tz3 = TimeZone.getTimeZone("foo");

        BaseProperties props = new BaseProperties();
        props.setProperty("tz1", "PST");
        props.setProperty("tz2", "GMT-8:00");
        props.setProperty("tz3", "foo");

        Assert.assertEquals(tz1, props.getTimeZone("tz1"));
        Assert.assertEquals(tz2, props.getTimeZone("tz2"));
        Assert.assertEquals(tz3, props.getTimeZone("tz3"));

        // Unknown value
        try {
            props.getTimeZone("foo");
            Assert.fail("Must throw SmscException");
        } catch (SmscException e) {
            // ok
        }

        // default values
        Assert.assertEquals(tz1, props.getTimeZone("tz1", tz2));
        Assert.assertEquals(tz2, props.getTimeZone("foo", tz2));
    }

    public void testSetBoolean() throws SmscException {
        BaseProperties props = new BaseProperties();
        props.setProperty("b1", true);

        Assert.assertEquals(true, props.getBoolean("b1"));
        Assert.assertEquals("true", props.getProperty("b1"));
        Assert.assertEquals("true", props.getString("b1"));
    }

    public void testSetClass() throws SmscException {
        BaseProperties props = new BaseProperties();
        props.setProperty("c1", String.class);

        Assert.assertEquals(String.class, props.getClass("c1"));
        Assert.assertEquals("java.lang.String", props.getProperty("c1"));
        Assert.assertEquals("java.lang.String", props.getString("c1"));
    }

    public void testSetDate() throws SmscException {
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSSzzz");

        BaseProperties props = new BaseProperties();
        props.setProperty("d1", d, format);

        Assert.assertEquals(d, props.getDate("d1", format));
        Assert.assertEquals(format.format(d), props.getProperty("d1"));
        Assert.assertEquals(format.format(d), props.getString("d1"));
    }

    public void testSetDateFormat() throws SmscException {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSSzzz");

        BaseProperties props = new BaseProperties();
        props.setProperty("f1", format);

        Assert.assertEquals(format, props.getDateFormat("f1"));
        Assert.assertEquals("yyyyMMddHHmmssSSSzzz", props.getProperty("f1"));
        Assert.assertEquals("yyyyMMddHHmmssSSSzzz", props.getString("f1"));
    }

    public void testSetDouble() throws SmscException {
        BaseProperties props = new BaseProperties();
        props.setProperty("d1", 1.23);

        Assert.assertEquals(1.23, props.getDouble("d1"), 0.1);
        Assert.assertEquals("1.23", props.getProperty("d1"));
        Assert.assertEquals("1.23", props.getString("d1"));
    }

    public void testSetFile() throws SmscException {
        File file = new File("test-tmp/test1.txt").getAbsoluteFile();

        BaseProperties props = new BaseProperties();
        props.setProperty("f1", file);

        Assert.assertEquals(file, props.getFile("f1"));
        Assert.assertEquals(file.getAbsolutePath(), props.getProperty("f1"));
        Assert.assertEquals(file.getAbsolutePath(), props.getString("f1"));
    }

    public void testSetInteger() throws SmscException {
        BaseProperties props = new BaseProperties();
        props.setProperty("i1", 1);

        Assert.assertEquals(1, props.getInteger("i1"));
        Assert.assertEquals("1", props.getProperty("i1"));
        Assert.assertEquals("1", props.getString("i1"));
    }

    public void testSetLong() throws SmscException {
        BaseProperties props = new BaseProperties();
        props.setProperty("l1", 1L);

        Assert.assertEquals(1, props.getLong("l1"));
        Assert.assertEquals("1", props.getProperty("l1"));
        Assert.assertEquals("1", props.getString("l1"));
    }

    public void testSetString() throws SmscException {
        BaseProperties props = new BaseProperties();
        props.setProperty("s1", "bar");

        Assert.assertEquals("bar", props.getProperty("s1"));
        Assert.assertEquals("bar", props.getString("s1"));
    }

    public void testSetTimeZone() throws SmscException {
        TimeZone tz1 = TimeZone.getTimeZone("PST");

        BaseProperties props = new BaseProperties();
        props.setProperty("tz1", tz1);

        Assert.assertEquals(tz1, props.getTimeZone("tz1"));
        Assert.assertEquals("PST", props.getProperty("tz1"));
        Assert.assertEquals("PST", props.getString("tz1"));
    }
}