package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.DeliverSM;

import java.util.UUID;

import org.apache.smscserver.smsclet.DeliverSMRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 */
public class SmscDeliverSMRequestImpl extends DeliverSM implements DeliverSMRequest {

    private UUID id;

    public SmscDeliverSMRequestImpl(int sequenceNum) {
        super();

        this.sequenceNum = sequenceNum;
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
    public int getVersionId() {
        return this.getVersion().getVersionID();
    }
}
