package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.ParamRetrieve;

import java.util.UUID;

import org.apache.smscserver.smsclet.ParamRetrieveRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 */
public class SmscParamRetrieveRequestImpl extends ParamRetrieve implements ParamRetrieveRequest {

    private UUID id;

    public SmscParamRetrieveRequestImpl(int sequenceNum, byte[] body) {
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
