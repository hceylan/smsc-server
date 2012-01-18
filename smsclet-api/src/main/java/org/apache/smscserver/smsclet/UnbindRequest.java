package org.apache.smscserver.smsclet;

/**
 * The purpose of the SMPP unbind operation is to deregister an instance of an ESME from the SMSC and inform the SMSC
 * that the ESME no longer wishes to use this network connection for the submission or delivery of messages.
 * <p>
 * Thus, the unbind operation may be viewed as a form of SMSC logoff request to close the current SMPP session.
 * 
 * @version $Rev$ $Date$
 */
public interface UnbindRequest extends SmscRequest {

}
