package org.apache.smscserver.listener.nio;

import ie.omk.smpp.message.SMPPResponse;

import java.io.OutputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * @author hceylan
 */
public class SmppProtocolEncoder extends ProtocolEncoderAdapter implements ProtocolEncoder {

    /**
     * {@inheritDoc}
     * 
     */
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        IoBuffer io = IoBuffer.allocate(16, true) //
                .setAutoExpand(true);
        OutputStream os = io.asOutputStream();

        SMPPResponse response = (SMPPResponse) message;
        response.writeTo(os, true);

        io.flip();

        out.write(io);
    }

}
