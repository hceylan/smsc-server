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

import java.io.IOException;

/**
 * Defines methods that all smsclets must implement.
 * 
 * A smsclet is a small Java program that runs within an SMSC server. Smsclets receive and respond to requests from SMSC
 * clients.
 * 
 * This interface defines methods to initialize a smsclet, to service requests, and to remove a smsclet from the server.
 * These are known as life-cycle methods and are called in the following sequence:
 * 
 * <ol>
 * <li>The smsclet is constructed.</li>
 * <li>Then initialized with the init method.</li>
 * <li>All the callback methods will be invoked.</li>
 * <li>The smsclet is taken out of service, then destroyed with the destroy method.</li>
 * <li>Then garbage collected and finalized.</li>
 * </ol>
 * 
 * All the callback methods return SmscletEnum. If it returns null SmscletEnum.DEFAULT will be assumed. If any smsclet
 * callback method throws exception, that particular connection will be disconnected.
 * 
 * @author hceylan
 */
public interface Smsclet {

    /**
     * Called by the Smsclet container to indicate to a smsclet that the smsclet is being taken out of service. This
     * method is only called once all threads within the smsclet's service method have exited. After the smsclet
     * container calls this method, callback methods will not be executed. If the smsclet initialization method fails,
     * this method will not be called.
     */
    void destroy();

    /**
     * Called by the smsclet container to indicate to a smsclet that the smsclet is being placed into service. The
     * smsclet container calls the init method exactly once after instantiating the smsclet. The init method must
     * complete successfully before the smsclet can receive any requests.
     * 
     * @param smscletContext
     *            The current {@link SmscletContext}
     * @throws SmscException
     */
    void init(SmscletContext smscletContext) throws SmscException;

    /**
     * Client connect notification method.
     * 
     * @param session
     *            The current {@link SmscSession}
     * @return false if client is not allowed
     * @throws SmscException
     * @throws IOException
     */
    boolean onConnect(SmscSession session) throws SmscException, IOException;

    /**
     * Client disconnect notification method. This is the last callback method.
     * 
     * @param session
     *            The current {@link SmscSession}
     * @return The desired action to be performed by the server
     * @throws SmscException
     * @throws IOException
     */
    void onDisconnect(SmscSession session) throws SmscException, IOException;

    /**
     * Called by the smsclet container after a request has been received by the server. The implementation should return
     * based on the desired action to be taken by the server:
     * 
     * @param session
     *            The current session
     * @param request
     *            The current request
     * @return the reply that will be sent for this command.
     * @throws SmscException
     * @throws IOException
     */
    SmscReply onRequest(SmscSession session, SmscRequest request) throws SmscException, IOException;
}