package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.BindTransmitter;

import java.util.UUID;

import org.apache.smscserver.smsclet.BindTransmitterRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class SmscBindTransmitterRequest extends BindTransmitter implements BindTransmitterRequest {

    private final String id;

    public SmscBindTransmitterRequest(int commandStatus, int sequenceNum, byte[] body) {
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
