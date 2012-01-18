package org.apache.smscserver.messagemanager.impl;

import java.util.Date;

import org.apache.smscserver.smsclet.ShortMessage;
import org.apache.smscserver.smsclet.ShortMessageStatus;

public class ShortMessageImpl implements ShortMessage {

    private int datacoding;
    private int defaultMessageId;
    private String destinationAddress;
    private int destinationAddressNPI;
    private int destinationAddressTON;
    private int esmClass;
    private String id;
    private int messageLength;
    private int priorityFlag;
    private int protocolId;
    private String replaced;
    private String replacedBy;
    private Date scheduleDeliveryTime;
    private String serviceType;
    private String shortMessage;
    private String sourceAddress;
    private int sourceAddressNPI;
    private int sourceAddressTON;
    private ShortMessageStatus status;
    private Date validityPeriod;
    private Date received;
    private Date nextTryDeliverTime;
    private boolean replaceIfPresent;

    public ShortMessageImpl() {
        super();
    }

    /**
     * @return the datacoding
     */
    public int getDatacoding() {
        return this.datacoding;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getDataCoding() {
        return this.datacoding;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getDefaultMessageId() {
        return this.defaultMessageId;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public String getDestinationAddress() {
        return this.destinationAddress;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getDestinationAddressNPI() {
        return this.destinationAddressNPI;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getDestinationAddressTON() {
        return this.destinationAddressTON;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getEsmClass() {
        return this.esmClass;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public String getId() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getMessageLength() {
        return this.messageLength;
    }

    public Date getNextTryDeliverTime() {
        return this.nextTryDeliverTime;
    }

    /**
     * @return the ourceAddressNPI
     */
    public int getOurceAddressNPI() {
        return this.sourceAddressNPI;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getPriorityFlag() {
        return this.priorityFlag;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getProtocolId() {
        return this.protocolId;
    }

    public Date getReceived() {
        return this.received;
    }

    /**
     * @return the replaced
     */
    public String getReplaced() {
        return this.replaced;
    }

    /**
     * @return the replacedBy
     */
    public String getReplacedBy() {
        return this.replacedBy;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Date getScheduleDeliveryTime() {
        return this.scheduleDeliveryTime;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public String getServiceType() {
        return this.serviceType;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public String getShortMessage() {
        return this.shortMessage;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public String getSourceAddress() {
        return this.sourceAddress;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getSourceAddressNPI() {
        return this.sourceAddressNPI;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public int getSourceAddressTON() {
        return this.sourceAddressTON;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public ShortMessageStatus getStatus() {
        return this.status;
    }

    /**
     * {@inheritDoc}
     * 
     */
    public Date getValidityPeriod() {
        return this.validityPeriod;
    }

    public boolean isReplaceIfPresent() {
        return this.replaceIfPresent;
    }

    /**
     * @param datacoding
     *            the datacoding to set
     */
    public void setDatacoding(int datacoding) {
        this.datacoding = datacoding;
    }

    /**
     * @param defaultMessageId
     *            the defaultMessageId to set
     */
    public void setDefaultMessageId(int defaultMessageId) {
        this.defaultMessageId = defaultMessageId;
    }

    /**
     * @param destinationAddress
     *            the destinationAddress to set
     */
    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    /**
     * @param destinationAddressNPI
     *            the destinationAddressNPI to set
     */
    public void setDestinationAddressNPI(int destinationAddressNPI) {
        this.destinationAddressNPI = destinationAddressNPI;
    }

    /**
     * @param destinationAddressTON
     *            the destinationAddressTON to set
     */
    public void setDestinationAddressTON(int destinationAddressTON) {
        this.destinationAddressTON = destinationAddressTON;
    }

    /**
     * @param esmClass
     *            the esmClass to set
     */
    public void setEsmClass(int esmClass) {
        this.esmClass = esmClass;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param messageLength
     *            the messageLength to set
     */
    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    public void setNextTryDeliverTime(Date nextTryDeliverTime) {
        this.nextTryDeliverTime = nextTryDeliverTime;

    }

    /**
     * @param priorityFlag
     *            the priorityFlag to set
     */
    public void setPriorityFlag(int priorityFlag) {
        this.priorityFlag = priorityFlag;
    }

    /**
     * @param protocolId
     *            the protocolId to set
     */
    public void setProtocolId(int protocolId) {
        this.protocolId = protocolId;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    /**
     * @param replaced
     *            the replaced to set
     */
    public void setReplaced(String replaced) {
        this.replaced = replaced;
    }

    /**
     * @param replacedBy
     *            the replacedBy to set
     */
    public void setReplacedBy(String replacedBy) {
        this.replacedBy = replacedBy;
    }

    /**
     * @param replaceIfPresent
     *            the replaceIfPresent to set
     */
    public void setReplaceIfPresent(boolean replaceIfPresent) {
        this.replaceIfPresent = replaceIfPresent;
    }

    /**
     * @param scheduleDeliveryTime
     *            the scheduleDeliveryTime to set
     */
    public void setScheduleDeliveryTime(Date scheduleDeliveryTime) {
        this.scheduleDeliveryTime = scheduleDeliveryTime;
    }

    /**
     * @param serviceType
     *            the serviceType to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @param shortMessage
     *            the shortMessage to set
     */
    public void setShortMessage(String shortMessage) {
        this.shortMessage = shortMessage;
    }

    /**
     * @param sourceAddress
     *            the sourceAddress to set
     */
    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    /**
     * @param sourceAddressNPI
     *            the sourceAddressNPI to set
     */
    public void setSourceAddressNPI(int sourceAddressNPI) {
        this.sourceAddressNPI = sourceAddressNPI;
    }

    /**
     * @param sourceAddressTON
     *            the sourceAddressTON to set
     */
    public void setSourceAddressTON(int sourceAddressTON) {
        this.sourceAddressTON = sourceAddressTON;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(ShortMessageStatus status) {
        this.status = status;
    }

    /**
     * @param validityPeriod
     *            the validityPeriod to set
     */
    public void setValidityPeriod(Date validityPeriod) {
        this.validityPeriod = validityPeriod;
    }
}
