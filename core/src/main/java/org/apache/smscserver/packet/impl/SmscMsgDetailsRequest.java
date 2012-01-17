package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.QueryLastMsgs;

import java.util.UUID;

import org.apache.smscserver.smsclet.QueryLastMsgsRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class SmscMsgDetailsRequest extends QueryLastMsgs implements QueryLastMsgsRequest {

    private final String id;

    public SmscMsgDetailsRequest(int commandStatus, int sequenceNum, byte[] body) {
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
