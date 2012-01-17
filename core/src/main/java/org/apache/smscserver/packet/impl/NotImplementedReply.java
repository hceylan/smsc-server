package org.apache.smscserver.packet.impl;

import org.apache.smscserver.smsclet.SmscReply;
import org.apache.smscserver.smsclet.SmscRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class NotImplementedReply implements SmscReply {

    private final SmscRequest request;

    /**
     * @param request
     *            the request
     */
    public NotImplementedReply(SmscRequest request) {
        this.request = request;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getCode() {
        return SmscReply.ESME_PROVIDER_NOT_SUPPORTED;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean isOK() {
        return false;
    }

}
