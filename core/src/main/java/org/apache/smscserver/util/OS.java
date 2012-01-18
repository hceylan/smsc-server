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

import java.util.Locale;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * Condition that tests the OS type.
 * 
 * @author hceylan
 */
public final class OS {
    private static final String FAMILY_OS_400 = "os/400";

    private static final String FAMILY_Z_OS = "z/os";

    private static final String FAMILY_WIN9X = "win9x";

    private static final String FAMILY_OPENVMS = "openvms";

    private static final String FAMILY_UNIX = "unix";

    private static final String FAMILY_TANDEM = "tandem";

    private static final String FAMILY_MAC = "mac";

    private static final String FAMILY_DOS = "dos";

    private static final String FAMILY_NETWARE = "netware";

    private static final String FAMILY_OS_2 = "os/2";

    private static final String FAMILY_WINDOWS = "windows";

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.US);

    private static final String OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.US);

    private static final String OS_VERSION = System.getProperty("os.version").toLowerCase(Locale.US);

    private static final String PATH_SEP = System.getProperty("path.separator");

    /**
     * Determines if the OS on which Ant is executing matches the given OS architecture.
     * 
     * @param arch
     *            the OS architecture to check for
     * @return true if the OS matches
     */
    public static boolean isArch(final String arch) {
        return OS.isOs(null, null, arch, null);
    }

    /**
     * Determines if the OS on which Ant is executing matches the given OS family. * Possible values:<br />
     * <ul>
     * <li>dos</li>
     * <li>mac</li>
     * <li>netware</li>
     * <li>os/2</li>
     * <li>tandem</li>
     * <li>unix</li>
     * <li>windows</li>
     * <li>win9x</li>
     * <li>z/os</li>
     * <li>os/400</li>
     * </ul>
     * 
     * @param family
     *            the family to check for
     * @return true if the OS matches
     */
    private static boolean isFamily(final String family) {
        return OS.isOs(family, null, null, null);
    }

    public static boolean isFamilyDOS() {
        return OS.isFamily(OS.FAMILY_DOS);
    }

    public static boolean isFamilyMac() {
        return OS.isFamily(OS.FAMILY_MAC);
    }

    public static boolean isFamilyNetware() {
        return OS.isFamily(OS.FAMILY_NETWARE);
    }

    public static boolean isFamilyOpenVms() {
        return OS.isFamily(OS.FAMILY_OPENVMS);
    }

    public static boolean isFamilyOS2() {
        return OS.isFamily(OS.FAMILY_OS_2);
    }

    public static boolean isFamilyOS400() {
        return OS.isFamily(OS.FAMILY_OS_400);
    }

    public static boolean isFamilyTandem() {
        return OS.isFamily(OS.FAMILY_TANDEM);
    }

    public static boolean isFamilyUnix() {
        return OS.isFamily(OS.FAMILY_UNIX);
    }

    public static boolean isFamilyWin9x() {
        return OS.isFamily(OS.FAMILY_WIN9X);
    }

    public static boolean isFamilyWindows() {
        return OS.isFamily(OS.FAMILY_WINDOWS);
    }

    public static boolean isFamilyZOS() {
        return OS.isFamily(OS.FAMILY_Z_OS);
    }

    /**
     * Determines if the OS on which Ant is executing matches the given OS name.
     * 
     * @param name
     *            the OS name to check for
     * @return true if the OS matches
     */
    public static boolean isName(final String name) {
        return OS.isOs(null, name, null, null);
    }

    /**
     * Determines if the OS on which Ant is executing matches the given OS family, name, architecture and version
     * 
     * @param family
     *            The OS family
     * @param name
     *            The OS name
     * @param arch
     *            The OS architecture
     * @param version
     *            The OS version
     * @return true if the OS matches
     */
    public static boolean isOs(final String family, final String name, final String arch, final String version) {
        boolean retValue = false;

        if ((family != null) || (name != null) || (arch != null) || (version != null)) {

            boolean isFamily = true;
            boolean isName = true;
            boolean isArch = true;
            boolean isVersion = true;

            if (family != null) {
                if (family.equals(OS.FAMILY_WINDOWS)) {
                    isFamily = OS.OS_NAME.indexOf(OS.FAMILY_WINDOWS) > -1;
                } else if (family.equals(OS.FAMILY_OS_2)) {
                    isFamily = OS.OS_NAME.indexOf(OS.FAMILY_OS_2) > -1;
                } else if (family.equals(OS.FAMILY_NETWARE)) {
                    isFamily = OS.OS_NAME.indexOf(OS.FAMILY_NETWARE) > -1;
                } else if (family.equals(OS.FAMILY_DOS)) {
                    isFamily = OS.PATH_SEP.equals(";") && !OS.isFamily(OS.FAMILY_NETWARE);
                } else if (family.equals(OS.FAMILY_MAC)) {
                    isFamily = OS.OS_NAME.indexOf(OS.FAMILY_MAC) > -1;
                } else if (family.equals(OS.FAMILY_TANDEM)) {
                    isFamily = OS.OS_NAME.indexOf("nonstop_kernel") > -1;
                } else if (family.equals(OS.FAMILY_UNIX)) {
                    isFamily = OS.PATH_SEP.equals(":") && !OS.isFamily(OS.FAMILY_OPENVMS)
                            && (!OS.isFamily(OS.FAMILY_MAC) || OS.OS_NAME.endsWith("x"));
                } else if (family.equals(OS.FAMILY_WIN9X)) {
                    isFamily = OS.isFamily(OS.FAMILY_WINDOWS)
                            && ((OS.OS_NAME.indexOf("95") >= 0) || (OS.OS_NAME.indexOf("98") >= 0)
                                    || (OS.OS_NAME.indexOf("me") >= 0) || (OS.OS_NAME.indexOf("ce") >= 0));
                } else if (family.equals(OS.FAMILY_Z_OS)) {
                    isFamily = (OS.OS_NAME.indexOf(OS.FAMILY_Z_OS) > -1) || (OS.OS_NAME.indexOf("os/390") > -1);
                } else if (family.equals(OS.FAMILY_OS_400)) {
                    isFamily = OS.OS_NAME.indexOf(OS.FAMILY_OS_400) > -1;
                } else if (family.equals(OS.FAMILY_OPENVMS)) {
                    isFamily = OS.OS_NAME.indexOf(OS.FAMILY_OPENVMS) > -1;
                } else {
                    throw new IllegalArgumentException("Don\'t know how to detect os family \"" + family + "\"");
                }
            }
            if (name != null) {
                isName = name.equals(OS.OS_NAME);
            }
            if (arch != null) {
                isArch = arch.equals(OS.OS_ARCH);
            }
            if (version != null) {
                isVersion = version.equals(OS.OS_VERSION);
            }
            retValue = isFamily && isName && isArch && isVersion;
        }
        return retValue;
    }

    /**
     * Determines if the OS on which Ant is executing matches the given OS version.
     * 
     * @param version
     *            the OS version to check for
     * @return true if the OS matches
     */
    public static boolean isVersion(final String version) {
        return OS.isOs(null, null, null, version);
    }

    /**
     * Default constructor
     */
    private OS() {
    }
}
