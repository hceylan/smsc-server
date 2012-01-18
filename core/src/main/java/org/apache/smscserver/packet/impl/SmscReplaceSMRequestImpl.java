package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.ReplaceSM;

import java.util.UUID;

import org.apache.smscserver.smsclet.ReplaceSMRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 */
public class SmscReplaceSMRequestImpl extends ReplaceSM implements ReplaceSMRequest {

    private UUID id;

    public SmscReplaceSMRequestImpl(int sequenceNum, byte[] body) {
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
    public int getVersionID() {
        return this.getVersion().getVersionID();
    }
}
