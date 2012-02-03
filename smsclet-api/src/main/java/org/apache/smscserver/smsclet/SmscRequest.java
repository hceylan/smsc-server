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

package org.apache.smscserver.smsclet;

import java.util.UUID;

/**
 * One SmscRequest made by the client.
 * 
 * @author hceylan
 */
public interface SmscRequest extends SmscPacket {

    /**
     * Get the Command Id of this SMPP packet.
     * 
     * @return The Command Id of this packet
     */
    public int getCommandId();

    /**
     * Returns the UUID for this request.
     * 
     * @return the UUID for this request.
     */
    public UUID getId();

    /**
     * Get the sequence number of this packet.
     * 
     * @return The sequence number of this SMPP packet
     */
    public int getSequenceNum();

    /**
     * Returns the version of the SMPP Protocol.
     * 
     * @return the version of the SMPP Protocol
     */
    public int getVersionId();
}
