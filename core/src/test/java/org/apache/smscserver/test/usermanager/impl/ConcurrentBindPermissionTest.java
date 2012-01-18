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

package org.apache.smscserver.test.usermanager.impl;

import org.apache.smscserver.usermanager.impl.ConcurrentBindPermission;
import org.apache.smscserver.usermanager.impl.ConcurrentBindRequest;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * 
 * @author hceylan
 * 
 */
public class ConcurrentBindPermissionTest extends TestCase {

    public void testAllowAnyMaxLogins() {
        ConcurrentBindPermission permission = new ConcurrentBindPermission(0, 2);
        ConcurrentBindRequest request = new ConcurrentBindRequest(5, 2);

        Assert.assertNotNull(permission.authorize(request));
    }

    public void testAllowAnyMaxLoginsPerIP() {
        ConcurrentBindPermission permission = new ConcurrentBindPermission(4, 0);
        ConcurrentBindRequest request = new ConcurrentBindRequest(3, 3);

        Assert.assertNotNull(permission.authorize(request));
    }

    public void testAllowBoth() {
        ConcurrentBindPermission permission = new ConcurrentBindPermission(4, 2);
        ConcurrentBindRequest request = new ConcurrentBindRequest(1, 1);

        Assert.assertNotNull(permission.authorize(request));
    }

    public void testCanAuthorize() {
        ConcurrentBindPermission permission = new ConcurrentBindPermission(4, 2);
        ConcurrentBindRequest request = new ConcurrentBindRequest(1, 1);

        Assert.assertTrue(permission.canAuthorize(request));
    }

    public void testMaxLoginsPerIPTooLarge() {
        ConcurrentBindPermission permission = new ConcurrentBindPermission(4, 2);
        ConcurrentBindRequest request = new ConcurrentBindRequest(3, 3);

        Assert.assertNull(permission.authorize(request));
    }

    public void testMaxLoginsTooLarge() {
        ConcurrentBindPermission permission = new ConcurrentBindPermission(4, 2);
        ConcurrentBindRequest request = new ConcurrentBindRequest(5, 2);

        Assert.assertNull(permission.authorize(request));
    }

    public void testMaxValuesBoth() {
        ConcurrentBindPermission permission = new ConcurrentBindPermission(4, 2);
        ConcurrentBindRequest request = new ConcurrentBindRequest(4, 2);

        Assert.assertNotNull(permission.authorize(request));
    }

}
