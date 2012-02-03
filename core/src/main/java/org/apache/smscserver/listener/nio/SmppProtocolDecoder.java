/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.smscserver.listener.nio;

import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.util.SMPPIO;

import java.io.InputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.smscserver.packet.impl.SmscBindRequestImpl;
import org.apache.smscserver.packet.impl.SmscCancelSMRequestImpl;
import org.apache.smscserver.packet.impl.SmscDataSMRequestImpl;
import org.apache.smscserver.packet.impl.SmscDeliverSMResponseImpl;
import org.apache.smscserver.packet.impl.SmscEnquireLinkRequestImpl;
import org.apache.smscserver.packet.impl.SmscMsgDetailsRequestImpl;
import org.apache.smscserver.packet.impl.SmscOutbindRequestImpl;
import org.apache.smscserver.packet.impl.SmscParamRetrieveRequestImpl;
import org.apache.smscserver.packet.impl.SmscQueryLastMsgsRequestImpl;
import org.apache.smscserver.packet.impl.SmscQuerySMRequestImpl;
import org.apache.smscserver.packet.impl.SmscReplaceSMRequestImpl;
import org.apache.smscserver.packet.impl.SmscSubmitMultiRequestImpl;
import org.apache.smscserver.packet.impl.SmscSubmitSMRequestImpl;
import org.apache.smscserver.packet.impl.SmscUnbindRequestImpl;
import org.apache.smscserver.smsclet.SmscPacket;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * Common base class for listener implementations
 * 
 * @author hceylan
 */
public class SmppProtocolDecoder extends CumulativeProtocolDecoder {

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        InputStream is = in.asInputStream();

        // if there is not enough data then postpone the operation
        if (in.remaining() < 16) {
            return false;
        }

        // Read the length and check if enough data is available
        int length = SMPPIO.readInt(is, 4);
        if (in.remaining() < (length - 4)) {
            // rewind and postpone
            in.rewind();
            return false;
        }

        int id = SMPPIO.readInt(is, 4);
        SMPPIO.readInt(is, 4);
        int sequenceNum = SMPPIO.readInt(is, 4);

        byte[] body = this.readBody(is, length - 16);

        SmscPacket packet = null;
        switch (id) {
        case SMPPPacket.BIND_RECEIVER:
            packet = new SmscBindRequestImpl(SMPPPacket.BIND_RECEIVER, sequenceNum, body);
            break;
        case SMPPPacket.BIND_TRANSCEIVER:
            packet = new SmscBindRequestImpl(SMPPPacket.BIND_TRANSCEIVER, sequenceNum, body);
            break;
        case SMPPPacket.BIND_TRANSMITTER:
            packet = new SmscBindRequestImpl(SMPPPacket.BIND_TRANSMITTER, sequenceNum, body);
            break;
        case SMPPPacket.CANCEL_SM:
            packet = new SmscCancelSMRequestImpl(sequenceNum, body);
            break;
        case SMPPPacket.DATA_SM:
            packet = new SmscDataSMRequestImpl(sequenceNum, body);
            break;
        case SMPPPacket.DELIVER_SM_RESP:
            packet = new SmscDeliverSMResponseImpl(sequenceNum, body);
            break;
        case SMPPPacket.ENQUIRE_LINK:
            packet = new SmscEnquireLinkRequestImpl(sequenceNum, body);
            break;
        case SMPPPacket.OUTBIND:
            packet = new SmscOutbindRequestImpl(sequenceNum, body);
            break;
        case SMPPPacket.PARAM_RETRIEVE:
            packet = new SmscParamRetrieveRequestImpl(sequenceNum, body);
            break;
        case SMPPPacket.QUERY_LAST_MSGS:
            packet = new SmscQueryLastMsgsRequestImpl(sequenceNum, body);
            break;
        case SMPPPacket.QUERY_MSG_DETAILS:
            packet = new SmscMsgDetailsRequestImpl(sequenceNum, body);
            break;
        case SMPPPacket.QUERY_SM:
            packet = new SmscQuerySMRequestImpl(sequenceNum, body);
            break;
        case SMPPPacket.REPLACE_SM:
            packet = new SmscReplaceSMRequestImpl(sequenceNum, body);
            break;
        case SMPPPacket.SUBMIT_MULTI:
            packet = new SmscSubmitMultiRequestImpl(sequenceNum, body);
            break;
        case SMPPPacket.SUBMIT_SM:
            packet = new SmscSubmitSMRequestImpl(sequenceNum, body);
            break;
        case SMPPPacket.UNBIND:
            packet = new SmscUnbindRequestImpl(sequenceNum, body);
            break;
        default:
            break;
        }

        out.write(packet);

        return in.remaining() >= 16;
    }

    private byte[] readBody(InputStream is, int length) throws java.io.IOException {
        byte[] body = new byte[length];

        int p = 0;
        for (int loop = 0; loop < (length - p); loop++) {
            int r = is.read(body, p, length - p);
            if (r == -1) {
                break;
            }

            p += r;
        }

        return body;
    }
}
