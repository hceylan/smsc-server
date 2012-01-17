package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.DeliverSM;

import java.util.UUID;

import org.apache.smscserver.smsclet.DeliverSMRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class SmscDeliverSMRequest extends DeliverSM implements DeliverSMRequest {

    private final String id;

    public SmscDeliverSMRequest(int commandStatus, int sequenceNum, byte[] body) {
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
