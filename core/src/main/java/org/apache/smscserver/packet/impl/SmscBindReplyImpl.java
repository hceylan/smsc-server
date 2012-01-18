package org.apache.smscserver.packet.impl;

import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.version.SMPPVersion;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.smscserver.smsclet.BindReply;
import org.apache.smscserver.smsclet.SmscRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 */
public class SmscBindReplyImpl extends BindResp implements BindReply {

    private final SmscRequest request;

    /**
     * @param request
     *            the request
     * @param systemId
     *            the system id of the user
     */
    public SmscBindReplyImpl(SmscRequest request, String systemId) {
        super(request.getCommandId() + 0x80000000);

        this.request = request;

        this.setSystemId(systemId);
        this.setSequenceNum(request.getSequenceNum());
        this.setVersion(SMPPVersion.getVersion(request.getVersionId()));
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this) //
                .append("requestId", this.request.getId()) //
                .append("commandId", this.getCommandId()) //
                .append("sequenceNum", this.getSequenceNum()) //
                .append("status", this.getCommandStatus()) //
                .append("systemId", this.getSystemId()) //
                .append("version", this.getVersion()) //
                .toString();

    }
}
