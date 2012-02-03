package org.apache.smscserver.smsclet;

import java.util.Date;

/**
 * This operation is used by an ESME to submit a short message to the SMSC for onward transmission to a specified short
 * message entity (SME). The submit_sm PDU does not support the transaction message mode.
 * 
 * @version $Rev$ $Date$
 */
public interface ShortMessage {

    /**
     * Returns the Defines the encoding scheme of the short message user data.
     * 
     * @return the Defines the encoding scheme of the short message user data
     */
    int getDataCoding();

    /**
     * Returns the identifier for the short message to send from a list of predefined ("canned") short messages stored
     * on the SMSC.
     * <p>
     * If not using an SMSC canned message, set to NULL.
     * 
     * @return the identifier for the short message to send from a list of predefined ("canned") short messages stored
     *         on the SMSC
     */
    int getDefaultMessageId();

    /**
     * Returns the destination address of this short message. For mobile terminated messages, this is the directory
     * number of the recipient MS.
     * 
     * @return the destination address of this short message
     */
    String getDestinationAddress();

    /**
     * Returns the numbering Plan Indicator for destination address. If not known, set to NULL
     * 
     * @return the numbering Plan Indicator for destination address or NULL
     */
    int getDestinationAddressNPI();

    /**
     * Returns the type of number for destination address.
     * <p>
     * If not known, set to NULL
     * 
     * @return the type of number for destination address or NULL
     */
    int getDestinationAddressTON();

    /**
     * Returns the Message Mode & Message Type.
     * 
     * @return the Message Mode & Message Type
     */
    int getEsmClass();

    /**
     * Returns the SMSC assigned id of the message.
     * <p>
     * For transient messages the id should be NULL.
     * 
     * @return the SMSC assigned id of the message or NULL
     */
    String getId();

    /**
     * Returns the length in octets of the shortMessage user data.
     * 
     * @see #getShortMessage()
     * @return the length in octets of the shortMessage user data
     */
    int getMessageLength();

    /**
     * Returns the next time the message will be tried to deliver.
     * 
     * @return the next time the message will be tried to deliver
     */
    Date getNextTryDeliverTime();

    /**
     * Designates the priority level of the message.
     * 
     * @return the priority level of the message
     */
    int getPriorityFlag();

    /**
     * returns the Protocol Identifier. Network specific field.
     * 
     * @return the Protocol Identifier
     */
    int getProtocolId();

    /**
     * Returns the date (time) the message was received.
     * 
     * @return the date (time) the message was received
     */
    Date getReceived();

    /**
     * Returns the id of the short message which this message replaces.
     * 
     * @return the id of the short message which this message replaces
     */
    public String getReplaced();

    /**
     * Returns the id of the short message which replaces this message.
     * 
     * @return the id of the short message which replaces this message
     */
    public String getReplacedBy();

    /**
     * Returns the short message is to be scheduled by the SMSC for delivery.
     * <p>
     * Set to NULL for immediate message delivery.
     * 
     * @return the short message is to be scheduled by the SMSC for delivery or NULL
     */
    Date getScheduleDeliveryTime();

    /**
     * Returns the service type.
     * <ul>
     * The service_type parameter can be used to indicate the SMS Application service associated with the message.
     * Specifying the service_type allows the ESME to
     * <li>avail of enhanced messaging services such as "replace by service" type
     * <li>to control the teleservice used on the air interface.
     * </ul>
     * <p>
     * Set to NULL for default SMSC settings.
     * 
     * @return the service type or NULL
     */
    String getServiceType();

    /**
     * Returns the up to 254 octets of short message user data. The exact physical limit for short_message size may vary
     * according to the underlying network.
     * <p>
     * Applications which need to send messages longer than 254 octets use the messagePayload parameter. In this case
     * the sm_length field should be set to zero
     * 
     * 
     * @return the up to 254 octets of short message user data
     */
    String getShortMessage();

    /**
     * Returns the address of SME which originated this message. If not known, set to NULL
     * 
     * @return the address of SME which originated this message or NULL
     */
    String getSourceAddress();

    /**
     * Returns the numbering Plan Indicator for source address. If not known, set to NULL
     * 
     * @return the numbering Plan Indicator for source address or NULL
     */
    int getSourceAddressNPI();

    /**
     * Returns the type of number for source address.
     * <p>
     * If not known, set to NULL
     * 
     * @return the type of number for source address or NULL
     */
    int getSourceAddressTON();

    /**
     * Returns the status of the short message.
     * 
     * @see ShortMessageStatus
     * @return the status of the short message
     */
    ShortMessageStatus getStatus();

    /**
     * Returns the validity period of this message.
     * <p>
     * Set to NULL to request the SMSC default validity period.
     * 
     * @return the validity period of this message or NULL
     */
    Date getValidityPeriod();

    /**
     * Sets the next time the message will be tried to be delivered.
     * 
     * @param nextTryDeliverTime
     *            the time to try
     */
    void setNextTryDeliverTime(Date nextTryDeliverTime);

    /**
     * Sets the new status for the message
     * 
     * @param status
     *            the status to set.
     */
    void setStatus(ShortMessageStatus status);
}
