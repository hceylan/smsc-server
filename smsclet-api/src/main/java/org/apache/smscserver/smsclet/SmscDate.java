package org.apache.smscserver.smsclet;

import java.util.Calendar;
import java.util.TimeZone;

public interface SmscDate {

    /**
     * Get a calendar object representing the internal time of this SMPPDate. This method should not be used to retrieve
     * a calendar from relative time specifications. That is, if <code>this.isRelative() == true</code>, you should not
     * attempt to use the object returned from this method.
     */
    public Calendar getCalendar();

    /**
     * Get the day, or number of days in a relative time spec. Day is in the range [1..31]
     */
    public int getDay();

    /**
     * Get the hour, or number of hours in a relative time spec. Hour is in the range [00..23]
     */
    public int getHour();

    /**
     * Get the minute, or number of minutes in a relative time spec. Minute is in the range [00..59]
     */
    public int getMinute();

    /**
     * Get the month, or number of months in a relative time spec. January is month 1.
     */
    public int getMonth();

    /**
     * Get the second, or number of seconds in a relative time spec. Second is in the range [00..59]
     */
    public int getSecond();

    /**
     * Get the UTC offset qualifier. This flag is '+' to indicate that the time spec is ahead of UTC or '-' to indicate
     * it is behind UTC. If the time spec is a relative time spec, this flag will be 'R'.
     * 
     * @see #getUtcOffset
     */
    public char getSign();

    /**
     * Get the tenths of a second. Always zero in a relative time spec. Tenths is in the range [0..9]
     */
    public int getTenth();

    /**
     * Get the timezone that this date is in. If this object represents a relative time definition, then this method
     * will return <code>null
     * </code>.
     * 
     * @return The timezone of this <code>SMPPDate</code>.
     * @see #isRelative()
     */
    public TimeZone getTimeZone();

    /**
     * Get the number of quarter-hours from UTC the time spec is offset by. This value is always positive. Use
     * {@link #getSign} to determine if the time is ahead of or behind UTC. utcOffset is in the range [0..48]
     */
    public int getUtcOffset();

    /**
     * Get the year, or number of years in a relative time spec.
     */
    public int getYear();

    /**
     * Test if this SMPPDate has timezone information associated with it. Relative time specs have no timezone
     * information, neither does the short (12-character) form of the absolute time spec. The short-form absolute format
     * should only be used by an SMSC - applications should never create a short-form format to send to the SMSC.
     * 
     * @return
     */
    public boolean hasTimezone();

    /**
     * Test if this SMPPDate represents a relative time specification.
     * 
     * @return true is this object represents a relative time spec, false if it represents an absolute time spec.
     */
    public boolean isRelative();

}