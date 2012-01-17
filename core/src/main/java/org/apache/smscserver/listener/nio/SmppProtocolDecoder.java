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
import org.apache.smscserver.packet.impl.SmscBindReceiverRequest;
import org.apache.smscserver.packet.impl.SmscBindTranceiverRequest;
import org.apache.smscserver.packet.impl.SmscBindTransmitterRequest;
import org.apache.smscserver.packet.impl.SmscCancelSMRequest;
import org.apache.smscserver.packet.impl.SmscDataSMRequest;
import org.apache.smscserver.packet.impl.SmscDeliverSMRequest;
import org.apache.smscserver.packet.impl.SmscEnquireLinkRequest;
import org.apache.smscserver.packet.impl.SmscMsgDetailsRequest;
import org.apache.smscserver.packet.impl.SmscOutbindRequest;
import org.apache.smscserver.packet.impl.SmscParamRetrieveRequest;
import org.apache.smscserver.packet.impl.SmscQueryLastMsgsRequest;
import org.apache.smscserver.packet.impl.SmscQuerySMRequest;
import org.apache.smscserver.packet.impl.SmscReplaceSMRequest;
import org.apache.smscserver.packet.impl.SmscSubmitMultiRequest;
import org.apache.smscserver.packet.impl.SmscSubmitSMRequest;
import org.apache.smscserver.packet.impl.SmscUnbindRequest;
import org.apache.smscserver.smsclet.SmscRequest;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * Common base class for listener implementations
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
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
        int commandStatus = SMPPIO.readInt(is, 4);
        int sequenceNum = SMPPIO.readInt(is, 4);

        byte[] body = this.readBody(is, length - 16);

        SmscRequest request = null;
        switch (id) {
        case SMPPPacket.BIND_RECEIVER:
            request = new SmscBindReceiverRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.BIND_TRANSCEIVER:
            request = new SmscBindTranceiverRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.BIND_TRANSMITTER:
            request = new SmscBindTransmitterRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.CANCEL_SM:
            request = new SmscCancelSMRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.DATA_SM:
            request = new SmscDataSMRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.DELIVER_SM:
            request = new SmscDeliverSMRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.ENQUIRE_LINK:
            request = new SmscEnquireLinkRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.OUTBIND:
            request = new SmscOutbindRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.PARAM_RETRIEVE:
            request = new SmscParamRetrieveRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.QUERY_LAST_MSGS:
            request = new SmscQueryLastMsgsRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.QUERY_MSG_DETAILS:
            request = new SmscMsgDetailsRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.QUERY_SM:
            request = new SmscQuerySMRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.REPLACE_SM:
            request = new SmscReplaceSMRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.SUBMIT_MULTI:
            request = new SmscSubmitMultiRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.SUBMIT_SM:
            request = new SmscSubmitSMRequest(commandStatus, sequenceNum, body);
            break;
        case SMPPPacket.UNBIND:
            request = new SmscUnbindRequest(commandStatus, sequenceNum, body);
            break;
        default:
            break;
        }

        out.write(request);

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
