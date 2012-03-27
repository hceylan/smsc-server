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
package org.apache.smscserver.message.impl;

import ie.omk.smpp.Address;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.smscserver.SmscServerContext;
import org.apache.smscserver.packet.impl.SmscDeliverSMRequestImpl;
import org.apache.smscserver.smsclet.MessageManager;
import org.apache.smscserver.smsclet.ShortMessage;
import org.apache.smscserver.smsclet.ShortMessageStatus;
import org.apache.smscserver.smsclet.SmscIoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to check and deliver pending short messages for bound session.
 * 
 * @version $Rev$ $Date$
 */
public class MessagePoller implements Runnable, Comparable<MessagePoller> {

    private static final Logger LOG = LoggerFactory.getLogger(MessagePoller.class);

    private final SmscIoSession ioSession;

    private long nextCheckTime;

    private final DefaultDeliveryManager deliveryManager;

    public MessagePoller(DefaultDeliveryManager deliveryManager, SmscIoSession ioSession) {
        this.deliveryManager = deliveryManager;
        this.ioSession = ioSession;

        this.nextCheckTime = 0;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int compareTo(MessagePoller o) {
        return (int) (this.nextCheckTime - o.nextCheckTime);
    }

    private void deliverShortMessage(ShortMessage shortMessage) {
        SmscDeliverSMRequestImpl deliverSMRequest = new SmscDeliverSMRequestImpl(this.ioSession.getNextSequnce());

        deliverSMRequest.setSource(new Address(shortMessage.getSourceAddressTON(), shortMessage.getSourceAddressNPI(),
                shortMessage.getSourceAddress()));
        deliverSMRequest.setDestination(new Address(shortMessage.getDestinationAddressTON(), shortMessage
                .getDestinationAddressNPI(), shortMessage.getDestinationAddress()));

        deliverSMRequest.setEsmClass(shortMessage.getEsmClass());
        deliverSMRequest.setMessageText(shortMessage.getShortMessage());
        deliverSMRequest.setMessageId(shortMessage.getId());
        deliverSMRequest.setPriority(shortMessage.getPriorityFlag());
        deliverSMRequest.setServiceType(shortMessage.getServiceType());
        deliverSMRequest.setVersion(null);

        this.ioSession.lock();
        try {

            this.ioSession.write(deliverSMRequest);

        } finally {
            this.ioSession.unlock();
        }
    }

    /**
     * Returns the next check time in milliseconds.
     * 
     * @return the next check time in milliseconds
     */
    public long getNextCheckTime() {
        return this.nextCheckTime;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public void run() {
        boolean success = true;
        try {
            Date now = new Date(System.currentTimeMillis());

            SmscServerContext context = this.deliveryManager.getServerContext();
            MessageManager messageManager = context.getMessageManager();

            List<ShortMessage> messages = messageManager.getPendingMessagesForUser(this.ioSession.getUser());

            for (Iterator<ShortMessage> i = messages.iterator(); i.hasNext();) {
                ShortMessage shortMessage = i.next();

                // Check if message has expired
                if (shortMessage.getValidityPeriod().compareTo(now) <= 0) {
                    shortMessage.setStatus(ShortMessageStatus.EXPIRED);

                    messageManager.updateMesage(shortMessage);

                    i.remove();

                    continue;
                }

                this.deliverShortMessage(shortMessage);
            }
        } catch (Throwable t) {
            success = false;

            MessagePoller.LOG.error("Message delivery failed for " + this.ioSession.getUser().getName(), t);
        }

        if (MessagePoller.LOG.isDebugEnabled()) {
            MessagePoller.LOG.debug("Message delivery finished with " + (success ? "success" : "error")
                    + ". Rescheduling...");
        }

        this.deliveryManager.reschedule(this);
    }

    /**
     * @param nextCheckTime
     *            the next time check for messages
     */
    public void setNextCheckTime(long nextCheckTime) {
        this.nextCheckTime = nextCheckTime;
    }
}
