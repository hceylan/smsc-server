package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.Unbind;

import java.util.UUID;

import org.apache.smscserver.smsclet.UnbindRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 */
public class SmscUnbindRequestImpl extends Unbind implements UnbindRequest {

    private UUID id;

    public SmscUnbindRequestImpl(int sequenceNum, byte[] body) {
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
