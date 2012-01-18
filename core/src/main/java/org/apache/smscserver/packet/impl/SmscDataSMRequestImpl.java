package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.DataSM;

import java.util.UUID;

import org.apache.smscserver.smsclet.DataSMRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 */
public class SmscDataSMRequestImpl extends DataSM implements DataSMRequest {

    private UUID id;

    public SmscDataSMRequestImpl(int sequenceNum, byte[] body) {
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
