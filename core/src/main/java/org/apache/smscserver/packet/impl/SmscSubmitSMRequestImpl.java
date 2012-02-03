package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.util.SMPPDate;

import java.util.Date;
import java.util.UUID;

import org.apache.smscserver.smsclet.SubmitSMRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 */
public class SmscSubmitSMRequestImpl extends SubmitSM implements SubmitSMRequest {

    private UUID id;

    public SmscSubmitSMRequestImpl(int sequenceNum, byte[] body) {
        super();

        this.sequenceNum = sequenceNum;

        this.readBodyFrom(body, 0);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getDefaultMessageId() {
        return super.getDefaultMsg();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public String getDestinationAddress() {
        return this.getDestination().getAddress();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getDestinationAddressNPI() {
        return this.getDestination().getNPI();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getDestinationAddressTON() {
        return this.getDestination().getTON();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public UUID getId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }

        return this.id;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getMessageLength() {
        return super.getMessageLen();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getPriorityFlag() {
        return super.getPriority();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Date getScheduleDeliveryTime() {
        SMPPDate deliveryTime = super.getDeliveryTime();

        if (deliveryTime == null) {
            return new Date(System.currentTimeMillis());
        }

        if (!deliveryTime.isRelative()) {
            return deliveryTime.getCalendar().getTime();
        }

        long now = System.currentTimeMillis();
        long delta = deliveryTime.getCalendar().getTimeInMillis();

        long then = deliveryTime.getSign() == '+' ? now + delta : now - delta;

        return new Date(then);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public byte[] getShortMessage() {
        return super.getMessage();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public String getSourceAddress() {
        return super.getSource().getAddress();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getSourceAddressNPI() {
        return super.getSource().getNPI();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getSourceAddressTON() {
        return this.getSource().getNPI();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Date getValidityPeriod() {
        SMPPDate expiryTime = super.getExpiryTime();

        if (expiryTime == null) {
            return null;
        }

        if (!expiryTime.isRelative()) {
            return expiryTime.getCalendar().getTime();
        }

        long now = System.currentTimeMillis();
        long delta = expiryTime.getCalendar().getTimeInMillis();

        long then = expiryTime.getSign() == '+' ? now + delta : now - delta;

        return new Date(then);
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getVersionId() {
        return this.getVersion().getVersionID();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean replaceIfPresent() {
        return super.getReplaceIfPresent() > 0;
    }
}
