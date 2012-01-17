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

package org.apache.smscserver.smsclet;

/**
 * Interface for a reply to an FTP request.
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public interface SmscReply {

    public enum ErrorCode {

        ESME_ROK(0, "No Error"), // OK
        ESME_RINVMSGLEN(1, "Message too long"), // ERROR
        ESME_RINVCMDLEN(2, "Command length is invalid"), // ERROR
        ESME_RINVCMDID(3, "Command ID is invalid or not supported"), // ERROR
        ESME_RINVBNDSTS(4, "Incorrect bind status for given command"), // ERROR
        ESME_RALYBND(5, "Already bound"), // ERROR
        ESME_RINVPRTFLG(6, "Invalid Priority Flag"), // ERROR
        ESME_RINVREGDLVFLG(7, "Invalid registered delivery flag"), // ERROR
        ESME_RSYSERR(8, "System error"), // ERROR
        ESME_RINVSRCADR(10, "Invalid source address"), // ERROR
        ESME_RINVDSTADR(11, "Invalid destination address"), // ERROR
        ESME_RINVMSGID(12, "Message ID is invalid"), // ERROR
        ESME_RBINDFAIL(13, "Bind failed"), // ERROR
        ESME_RINVPASWD(14, "Invalid password"), // ERROR
        ESME_RINVSYSID(15, "Invalid System ID"), // ERROR
        ESME_RCANCELFAIL(17, "Cancelling message failed"), // ERROR
        ESME_RREPLACEFAIL(19, "Message recplacement failed"), // ERROR
        ESME_RMSSQFUL(20, "Message queue full"), // ERROR
        ESME_RINVSERTYP(21, "Invalid service type"), // ERROR
        ESME_RINVNUMDESTS(51, "Invalid number of destinations"), // ERROR
        ESME_RINVDLNAME(52, "Invalid distribution list name"), // ERROR
        ESME_RINVDESTFLAG(64, "Invalid destination flag"), // ERROR
        ESME_RINVSUBREP(66, "Invalid submit with replace request"), // ERROR
        ESME_RINVESMCLASS(67, "Invalid esm class set"), // ERROR
        ESME_RCNTSUBDL(68, "Invalid submit to ditribution list"), // ERROR
        ESME_RSUBMITFAIL(69, "Submitting message has failed"), // ERROR
        ESME_RINVSRCTON(72, "Invalid source address type of number ( TON )"), // ERROR
        ESME_RINVSRCNPI(73, "Invalid source address numbering plan ( NPI )"), // ERROR
        ESME_RINVDSTTON(80, "Invalid destination address type of number ( TON )"), // ERROR
        ESME_RINVDSTNPI(81, "Invalid destination address numbering plan ( NPI )"), // ERROR
        ESME_RINVSYSTYP(83, "Invalid system type"), // ERROR
        ESME_RINVREPFLAG(84, "Invalid replace_if_present flag"), // ERROR
        ESME_RINVNUMMSGS(85, "Invalid number of messages"), // ERROR
        ESME_RTHROTTLED(88, "Throttling error"), // ERROR
        ESME_RINVSCHED(97, "Invalid scheduled delivery time"), // ERROR
        ESME_RINVEXPIRY(98, "Invalid Validty Period value"), // ERROR
        ESME_RINVDFTMSGID(99, "Predefined message not found"), // ERROR
        ESME_RX_T_APPN(100, "ESME Receiver temporary error"), // ERROR
        ESME_RX_P_APPN(101, "ESME Receiver permanent error"), // ERROR
        ESME_RX_R_APPN(102, "ESME Receiver reject message error"), // ERROR
        ESME_RQUERYFAIL(103, "Message query request failed"), // ERROR
        ESME_RINVTLVSTREAM(192, "Error in the optional part of the PDU body"), // ERROR
        ESME_RTLVNOTALLWD(193, "TLV not allowed"), // ERROR
        ESME_RINVTLVLEN(194, "Invalid parameter length"), // ERROR
        ESME_RMISSINGTLV(195, "Expected TLV missing"), // ERROR
        ESME_RINVTLVVAL(196, "Invalid TLV value"), // ERROR
        ESME_RDELIVERYFAILURE(254, "Transaction delivery failure"), // ERROR
        ESME_RUNKNOWNERR(255, "Unknown error"), // ERROR

        ESME_PROVIDER_RUNKNOWNERR(0x401, "Message not supported"); // PROVIDER ERROR

        private final int code;
        private final String message;

        ErrorCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return this.code;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public static final int ESME_ROK = 0;

    public static final int ESME_RINVMSGLEN = 1;
    public static final int ESME_RINVCMDLEN = 2;
    public static final int ESME_RINVCMDID = 3;
    public static final int ESME_RINVBNDSTS = 4;
    public static final int ESME_RALYBND = 5;
    public static final int ESME_RINVPRTFLG = 6;
    public static final int ESME_RINVREGDLVFLG = 7;
    public static final int ESME_RSYSERR = 8;
    public static final int ESME_RINVSRCADR = 10;
    public static final int ESME_RINVDSTADR = 11;
    public static final int ESME_RINVMSGID = 12;
    public static final int ESME_RBINDFAIL = 13;
    public static final int ESME_RINVPASWD = 14;
    public static final int ESME_RINVSYSID = 15;
    public static final int ESME_RCANCELFAIL = 17;
    public static final int ESME_RREPLACEFAIL = 19;
    public static final int ESME_RMSSQFUL = 20;
    public static final int ESME_RINVSERTYP = 21;
    public static final int ESME_RINVNUMDESTS = 51;
    public static final int ESME_RINVDLNAME = 52;
    public static final int ESME_RINVDESTFLAG = 64;
    public static final int ESME_RINVSUBREP = 66;
    public static final int ESME_RINVESMCLASS = 67;
    public static final int ESME_RCNTSUBDL = 68;
    public static final int ESME_RSUBMITFAIL = 69;
    public static final int ESME_RINVSRCTON = 72;
    public static final int ESME_RINVSRCNPI = 73;
    public static final int ESME_RINVDSTTON = 80;
    public static final int ESME_RINVDSTNPI = 81;
    public static final int ESME_RINVSYSTYP = 83;
    public static final int ESME_RINVREPFLAG = 84;
    public static final int ESME_RINVNUMMSGS = 85;
    public static final int ESME_RTHROTTLED = 88;
    public static final int ESME_RINVSCHED = 97;
    public static final int ESME_RINVEXPIRY = 98;
    public static final int ESME_RINVDFTMSGID = 99;
    public static final int ESME_RX_T_APPN = 100;
    public static final int ESME_RX_P_APPN = 101;
    public static final int ESME_RX_R_APPN = 102;
    public static final int ESME_RQUERYFAIL = 103;
    public static final int ESME_RINVTLVSTREAM = 192;
    public static final int ESME_RTLVNOTALLWD = 193;
    public static final int ESME_RINVTLVLEN = 194;
    public static final int ESME_RMISSINGTLV = 195;
    public static final int ESME_RINVTLVVAL = 196;
    public static final int ESME_RDELIVERYFAILURE = 254;
    public static final int ESME_RUNKNOWNERR = 255;

    public static final int ESME_PROVIDER_NOT_SUPPORTED = 0x401;

    /**
     * The reply code
     * 
     * @return The reply code
     */
    int getCode();

    /**
     * Tells whether or not this reply indicates a positive completion.
     * 
     * @return <code>true</code>, if this reply is a positive completion or positive intermediate reply;
     *         <code>false</code>, otherwise.
     */
    boolean isOK();
}
