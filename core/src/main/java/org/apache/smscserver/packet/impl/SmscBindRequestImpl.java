package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.Bind;

import java.util.UUID;

import org.apache.smscserver.smsclet.BindRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 */
public class SmscBindRequestImpl extends Bind implements BindRequest {

    private UUID id;

    public SmscBindRequestImpl(int type, int sequenceNum, byte[] body) {
        super(type);

        this.setSequenceNum(sequenceNum);

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
