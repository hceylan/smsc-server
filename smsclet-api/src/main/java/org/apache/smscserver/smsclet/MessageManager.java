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

/**
 * 
 * User manager interface.
 * 
 * @author hceylan
 */
public interface MessageManager {

    /**
     * Cancels the short message for delivery.
     * 
     * @param shortMessage
     * @return the id of the new short message
     * @throws SmscException
     * @throws SmscOriginalNotFoundException
     *             if the original not found
     */
    public void cancelSM(ShortMessage shortMessage) throws SmscException, SmscOriginalNotFoundException;

    /**
     * Replaces a message with the new one.
     * 
     * @param shortMessage
     *            the short message replacing the short message with the id.
     * @throws SmscException
     * @throws {@link SmscOriginalNotFoundException} if replace cannot be undertaken
     */
    public void replaceSM(ShortMessage shortMessage) throws SmscException, SmscOriginalNotFoundException;

    /**
     * Returns the short message with the id.
     * 
     * @param id
     *            the of the short message
     * @return the short message with the id
     * @throws SmscException
     */
    public ShortMessage selectShortMessage(String id) throws SmscException;

    /**
     * Stores the short message for delivery.
     * <p>
     * If the message has an id then an update will be performed instead
     * 
     * @param shortMessage
     * @return the id of the new short message
     * @throws SmscException
     */
    public void submitSM(ShortMessage shortMessage) throws SmscException;
}
