package org.apache.smscserver.smsclet;

/**
 * This message can be sent by either the ESME or SMSC and is used to provide a confidence- check of the communication
 * path between an ESME and an SMSC. On receipt of this request the receiving party should respond with an
 * enquire_link_resp, thus verifying that the application level connection between the SMSC and the ESME is functioning.
 * The ESME may also respond by sending any valid SMPP primitive.
 * 
 * @version $Rev$ $Date$
 */
public interface EnquireLinkRequest extends SmscRequest {

}
