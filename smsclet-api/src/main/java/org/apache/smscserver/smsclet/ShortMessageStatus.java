package org.apache.smscserver.smsclet;

/**
 * Status constants for short messages
 * 
 * @version $Rev$ $Date$
 */
public enum ShortMessageStatus {

    /**
     * Message is waiting for delivery.
     */
    PENDING(),

    /**
     * Message has been delivered.
     */
    DELIVERED(),

    /**
     * Message has expired.
     */
    EXPIRED()
}
