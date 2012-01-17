package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.DataSM;

import java.util.UUID;

import org.apache.smscserver.smsclet.DataSMRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class SmscDataSMRequest extends DataSM implements DataSMRequest {

    private final String id;

    public SmscDataSMRequest(int commandStatus, int sequenceNum, byte[] body) {
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
