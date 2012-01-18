package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.SMPPProtocolException;
import ie.omk.smpp.message.SMPPResponse;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.smscserver.smsclet.SmscRequest;
import org.apache.smscserver.smsclet.StatusReply;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 */
public class SmscStatusReplyImpl extends SMPPResponse implements StatusReply {

    private final SmscRequest request;

    /**
     * @param request
     *            the request
     * @param errorCode
     */
    public SmscStatusReplyImpl(SmscRequest request, ErrorCode errorCode) {
        super(request.getCommandId() + 0x80000000, request.getSequenceNum());
        this.request = request;

        this.setCommandStatus(errorCode.getCode());
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    protected void encodeBody(OutputStream out) throws IOException {
        // SMPPIO.writeInt(this.version.getVersionID(), 1, out);
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public int getBodyLength() {
        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    protected void readBodyFrom(byte[] b, int offset) throws SMPPProtocolException {
        // not required for server implementation
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("requestId", this.request.getId()) //
                .append("commandId", this.getCommandId()) //
                .append("sequenceNum", this.getSequenceNum()) //
                .append("status", this.getCommandStatus()) //
                .append("version", this.getVersion()) //
                .toString();

    }
}
