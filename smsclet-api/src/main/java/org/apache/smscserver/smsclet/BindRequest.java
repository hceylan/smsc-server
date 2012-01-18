/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.smscserver.smsclet;

/**
 * The purpose of the SMPP bind operation is to register an instance of an ESME with the SMSC system and request an SMPP
 * session over this network connection for the submission or delivery of messages. Thus, the Bind operation may be
 * viewed as a form of SMSC login request to authenticate the ESME entity wishing to establish a connection.
 * <p>
 * As described previously, an ESME may bind to the SMSC as either a Transmitter (called ESME Transmitter), a Receiver
 * (called ESME Receiver) or a Transceiver (called ESME Transceiver). There are three SMPP bind PDUs to support the
 * various modes of operation, namely bind_transmitter, bind_transceiver and bind_receiver. The command_id field setting
 * specifies which PDU is being used.
 * <p>
 * An ESME may bind as both an SMPP Transmitter and Receiver using separate bind_transmitter and bind_receiver
 * operations (having first established two separate network connections). Alternatively an ESME can also bind as a
 * Transceiver having first established a single network connection.
 * <p>
 * If an SMSC does not support the bind_transmitter and bind_receiver operations then it should return a response
 * message with an ”Invalid Command ID” error and the ESME should reattempt to bind using the bind_transceiver
 * operation. Similarly if an SMSC does not support the bind_transceiver command then it should return a response
 * message with an ”Invalid Command ID” error and the ESME should reattempt to bind using the bind_transmitter or
 * bind_receiver operations or both bind_transmitter and bind_receiver operations as appropriate.
 * <ul>
 * ESME Transmitter
 * <li>An ESME bound as a Transmitter is authorised to send short messages to the SMSC and to receive the corresponding
 * SMPP responses from the SMSC. An ESME indicates its desire not to receive (mobile) originated messages from other
 * SME’s (e.g. mobile stations) by binding as a Transmitter.
 * <li>ESME Receiver An ESME bound as a Receiver is authorised to receive short messages from the SMSC and to return the
 * corresponding SMPP message responses to the SMSC.
 * <li>ESME Transceiver An ESME bound as a Transceiver is allowed to send messages to the SMSC and receive messages from
 * the SMSC over a single SMPP session.
 * 
 * @version $Rev$ $Date$
 */
public interface BindRequest extends SmscRequest {

    /**
     * Returns the numbering Plan Indicator for ESME address. If not known set to NULL.
     * 
     * @return numbering Plan Indicator for ESME address or NULL
     */
    int getAddressNpi();

    /**
     * Returns the ESME address. If not known set to NULL.
     * 
     * @return the ESME address or NULL
     */
    String getAddressRange();

    /**
     * Returns the type of Number of the ESME address. If not known set to NULL.
     * 
     * @return the type of Number of the ESME address or NULL
     */
    int getAddressTon();

    /**
     * The password may be used by the SMSC to authenticate the ESME requesting to bind.
     * <p>
     * The password is used for authentication to secure SMSC access. The ESME may set the password to NULL to gain
     * insecure access (if allowed by SMSC administration).
     * 
     * @return the password or NULL
     */
    String getPassword();

    /**
     * Returns the identification of the ESME system requesting to bind with the SMSC.
     * <p>
     * The recommended use of system id is to identify the binding entity, e.g., "InternetGW" in the case of an Internet
     * Gateway or "VMS" for a Voice Mail System.
     * 
     * @return the identification of the type of ESME system requesting to bind with the SMSC
     */
    String getSystemId();

    /**
     * Returns the type of ESME system requesting to bind with the SMSC.
     * <p>
     * The system_type (optional) may be used to categorize the system, e.g., "EMAIL", "WWW", etc.
     * 
     * @return the type of ESME system requesting to bind as a transmitter with the SMSC
     */
    String getSystemType();
}
