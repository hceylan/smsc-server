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

package org.apache.smscserver.usermanager.impl;

import junit.framework.Assert;

import org.apache.smscserver.usermanager.PasswordEncryptor;
import org.apache.smscserver.usermanager.SaltedPasswordEncryptor;

/**
 * 
 * @author hceylan
 * 
 */
public class SaltedPasswordEncryptorTest extends ClearTextPasswordEncryptorTest {

    @Override
    protected PasswordEncryptor createPasswordEncryptor() {
        return new SaltedPasswordEncryptor();
    }

    @Override
    public void testMatches() {
        PasswordEncryptor encryptor = this.createPasswordEncryptor();

        Assert.assertTrue(encryptor.matches("foo", "71288966:F7B097C7BB2D02B8C2ACCE8A17284715"));

        // check lower case
        Assert.assertTrue(encryptor.matches("foo", "71288966:f7b097C7BB2D02B8C2ACCE8A17284715"));

        Assert.assertFalse(encryptor.matches("foo", "bar:bar"));
    }
}
