package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.CancelSM;

import java.util.UUID;

import org.apache.smscserver.smsclet.CancelSMRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 */
public class SmscCancelSMRequestImpl extends CancelSM implements CancelSMRequest {

    private UUID id;

    public SmscCancelSMRequestImpl(int sequenceNum, byte[] body) {
        super();

        this.sequenceNum = sequenceNum;

        this.readBodyFrom(body, 0);
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
