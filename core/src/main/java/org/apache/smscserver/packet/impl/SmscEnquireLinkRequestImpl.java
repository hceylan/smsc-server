package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.EnquireLink;

import java.util.UUID;

import org.apache.smscserver.smsclet.EnquireLinkRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 */
public class SmscEnquireLinkRequestImpl extends EnquireLink implements EnquireLinkRequest {

    private UUID id;

    public SmscEnquireLinkRequestImpl(int sequenceNum, byte[] body) {
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
