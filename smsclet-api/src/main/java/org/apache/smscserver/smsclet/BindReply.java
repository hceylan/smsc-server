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
 * 
 * 
 * @version $Rev$ $Date$
 */
public interface BindReply extends SmscReply {

    /**
     * Returns the identification of the type of ESME system requesting to bind as a transmitter with the SMSC.
     * <p>
     * The recommended use of system id is to identify the binding entity, e.g., "InternetGW" in the case of an Internet
     * Gateway or "VMS" for a Voice Mail System.
     * 
     * @return the identification of the type of ESME system requesting to bind as a transmitter with the SMSC
     */
    String getSystemId();

    /**
     * Sets the identification of the type of SMSC
     * 
     * @see #getSystemId()
     * @param systemId
     */
    void setSystemId(String systemId);
}
