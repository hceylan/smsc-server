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

package org.apache.smscserver.test.clienttests;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;

import junit.framework.Assert;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * 
 * @author hceylan*
 */
public class DecoderTest extends ClientTestTemplate {
    private final String dump = "4C 49 53 54 20 61 62 63 64 AE 2E 0D 0A".replace(" ", "");

    byte[] b;

    public DecoderTest() throws DecoderException {
        this.b = Hex.decodeHex(this.dump.toCharArray());
    }

    public void testDecodeError() throws CharacterCodingException {
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        ByteBuffer buffer = ByteBuffer.wrap(this.b);

        try {
            decoder.decode(buffer);
            Assert.fail("Must throw MalformedInputException");
        } catch (MalformedInputException e) {
            // OK
        }
    }

}
