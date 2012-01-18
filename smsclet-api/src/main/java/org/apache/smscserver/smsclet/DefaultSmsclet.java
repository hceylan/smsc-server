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

import java.io.IOException;

/**
 * Default smsclet implementation. All the callback method returns null. It is just an empty implementation. You can
 * derive your smsclet implementation from this class.
 * 
 * @author hceylan
 */
public class DefaultSmsclet implements Smsclet {

    public void destroy() {
    }

    public void init(SmscletContext smscletContext) throws SmscException {
    }

    public SmscReply onAlertNotification(SmscSession session, AlertNotificationRequest request) {
        return null;
    }

    public SmscReply onBind(SmscSession session, BindRequest request) {
        return null;
    }

    public SmscReply onCancelSM(SmscSession session, CancelSMRequest request) {
        return null;
    }

    public boolean onConnect(SmscSession session) throws SmscException, IOException {
        return true;
    }

    public SmscReply onDataSM(SmscSession session, DataSMRequest request) {
        return null;
    }

    public SmscReply onDeliverSM(SmscSession session, DeliverSMRequest request) {
        return null;
    }

    public void onDisconnect(SmscSession session) throws SmscException, IOException {
    }

    public SmscReply onEnquireLink(SmscSession session, EnquireLinkRequest request) {
        return null;
    }

    public SmscReply onOutbind(SmscSession session, OutbindRequest request) {
        return null;
    }

    public SmscReply onParamRetrieveRequest(SmscSession session, ParamRetrieveRequest request) {
        return null;
    }

    public SmscReply onQueryLastMsgs(SmscSession session, QueryLastMsgsRequest request) {
        return null;
    }

    public SmscReply onQueryMsgDetails(SmscSession session, QueryMsgDetailsRequest request) {
        return null;
    }

    public SmscReply onQuerySM(SmscSession session, QuerySMRequest request) {
        return null;
    }

    public SmscReply onReplaceSM(SmscSession session, ReplaceSMRequest request) {
        return null;
    }

    public final SmscReply onRequest(SmscSession session, SmscRequest request) throws SmscException, IOException {
        if (request instanceof AlertNotificationRequest) {
            return this.onAlertNotification(session, (AlertNotificationRequest) request);
        } else if (request instanceof BindRequest) {
            return this.onBind(session, (BindRequest) request);
        } else if (request instanceof CancelSMRequest) {
            return this.onCancelSM(session, (CancelSMRequest) request);
        } else if (request instanceof CancelSMRequest) {
            return this.onDataSM(session, (DataSMRequest) request);
        } else if (request instanceof DeliverSMRequest) {
            return this.onDeliverSM(session, (DeliverSMRequest) request);
        } else if (request instanceof EnquireLinkRequest) {
            return this.onEnquireLink(session, (EnquireLinkRequest) request);
        } else if (request instanceof OutbindRequest) {
            return this.onOutbind(session, (OutbindRequest) request);
        } else if (request instanceof ParamRetrieveRequest) {
            return this.onParamRetrieveRequest(session, (ParamRetrieveRequest) request);
        } else if (request instanceof QueryLastMsgsRequest) {
            return this.onQueryLastMsgs(session, (QueryLastMsgsRequest) request);
        } else if (request instanceof QueryMsgDetailsRequest) {
            return this.onQueryMsgDetails(session, (QueryMsgDetailsRequest) request);
        } else if (request instanceof QuerySMRequest) {
            return this.onQuerySM(session, (QuerySMRequest) request);
        } else if (request instanceof ReplaceSMRequest) {
            return this.onReplaceSM(session, (ReplaceSMRequest) request);
        } else if (request instanceof SubmitMultiRequest) {
            return this.onSubmitMulti(session, (SubmitMultiRequest) request);
        } else if (request instanceof SubmitSMRequest) {
            return this.onSubmitSM(session, (SubmitSMRequest) request);
        } else if (request instanceof UnbindRequest) {
            return this.onUnbind(session, (UnbindRequest) request);
        } else {
            // TODO should we call a catch all?
            return null;
        }
    }

    public SmscReply onSubmitMulti(SmscSession session, SubmitMultiRequest request) {
        return null;
    }

    public SmscReply onSubmitSM(SmscSession session, SubmitSMRequest request) {
        return null;
    }

    public SmscReply onUnbind(SmscSession session, UnbindRequest request) {
        return null;
    }

}