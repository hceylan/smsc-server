package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.SubmitMulti;

import java.util.UUID;

import org.apache.smscserver.smsclet.SubmitMultiRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class SmscSubmitMultiRequest extends SubmitMulti implements SubmitMultiRequest {

    private final String id;

    public SmscSubmitMultiRequest(int commandStatus, int sequenceNum, byte[] body) {
        super();

        this.commandStatus = commandStatus;
        this.sequenceNum = sequenceNum;

        this.readBodyFrom(body, 0);

        this.id = UUID.randomUUID().toString();
    }

    /**
     * {@inheritDoc}
     * 
     */
    public String getId() {
        return this.id;
    }

}
