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
package org.apache.smscserver.command.impl;

import org.apache.smscserver.SmscServerContext;
import org.apache.smscserver.command.Command;
import org.apache.smscserver.impl.DefaultSmscIoSession;
import org.apache.smscserver.messagemanager.impl.ShortMessageImpl;
import org.apache.smscserver.packet.impl.SmscStatusReplyImpl;
import org.apache.smscserver.smsclet.MessageManager;
import org.apache.smscserver.smsclet.SmscException;
import org.apache.smscserver.smsclet.SmscReply;
import org.apache.smscserver.smsclet.SmscReply.ErrorCode;
import org.apache.smscserver.smsclet.SmscRequest;
import org.apache.smscserver.smsclet.SubmitSMRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * Bind Command
 * 
 * <strong><strong>Internal class, do not use directly.</strong></strong>
 * 
 * @author hceylan
 */
public class SubmitSMCommand implements Command {

    public static final SubmitSMCommand SINGLETON = new SubmitSMCommand();

    /**
     * @param type
     * 
     */
    private SubmitSMCommand() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public SmscReply execute(DefaultSmscIoSession session, SmscServerContext context, SmscRequest request) {
        SubmitSMRequest submitSMRequest = (SubmitSMRequest) request;

        MessageManager messageManager = context.getMessageManager();

        ShortMessageImpl shortMessage = new ShortMessageImpl();
        shortMessage.setDatacoding(submitSMRequest.getDataCoding());
        shortMessage.setDefaultMessageId(submitSMRequest.getDefaultMessageId());
        shortMessage.setDestinationAddress(submitSMRequest.getDestinationAddress());
        shortMessage.setDestinationAddressNPI(submitSMRequest.getDestinationAddressNPI());
        shortMessage.setDestinationAddressTON(submitSMRequest.getDestinationAddressTON());
        shortMessage.setEsmClass(submitSMRequest.getEsmClass());
        shortMessage.setMessageLength(submitSMRequest.getMessageLength());
        shortMessage.setPriorityFlag(submitSMRequest.getPriorityFlag());
        shortMessage.setProtocolId(submitSMRequest.getProtocolId());
        shortMessage.setReplaceIfPresent(submitSMRequest.replaceIfPresent());
        shortMessage.setScheduleDeliveryTime(submitSMRequest.getScheduleDeliveryTime());
        shortMessage.setServiceType(submitSMRequest.getServiceType());
        // FIXME: HCEYLAN check source address for spoofing
        // FIXME: Create a source addr filter
        shortMessage.setSourceAddress(submitSMRequest.getSourceAddress() != null ? submitSMRequest.getSourceAddress()
                : session.getUser().getName());
        shortMessage.setSourceAddressNPI(submitSMRequest.getSourceAddressNPI());
        shortMessage.setSourceAddressTON(submitSMRequest.getSourceAddressTON());
        shortMessage.setValidityPeriod(submitSMRequest.getValidityPeriod());

        try {
            messageManager.storeShortMessage(shortMessage);
        } catch (SmscException e) {
            return new SmscStatusReplyImpl(submitSMRequest, ErrorCode.ESME_RSUBMITFAIL);
        }

        return new SmscStatusReplyImpl(request, ErrorCode.ESME_ROK);
    }
}
