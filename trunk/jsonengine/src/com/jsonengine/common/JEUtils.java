package com.jsonengine.common;

import java.math.BigDecimal;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * Provides utility methods for jsonengine.
 * 
 * @author kazunori_279
 */
public class JEUtils {

    public static final String ALNUMS =
        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final int UUID_DIGITS = 32;

    public static final JEUtils i = new JEUtils();

    private JEUtils() {
    }

    private static final String MC_KEY_TIMESTAMP =
        "com.jsonengine.common.LogCounterService#timestamp";

    private static final MemcacheService mcService =
        MemcacheServiceFactory.getMemcacheService();

    /**
     * Returns a global time stamp. The time stamp value is based on
     * {@link System#currentTimeMillis()}, but is assured to be the largest and
     * unique value in the application which may be served by several App
     * Servers. But please note it uses the Memcache service to assure the
     * uniqueness, and it would just return the
     * {@link System#currentTimeMillis()} as is when the Memcache value is
     * expired or lost.
     * 
     * TODO make this atomic
     * 
     * @return a global time stamp value
     */
    public long getGlobalTimestamp() {
        long timestamp = System.currentTimeMillis();
        Long lastTimestamp = (Long) mcService.get(MC_KEY_TIMESTAMP);
        if (lastTimestamp != null && lastTimestamp >= timestamp) {
            timestamp = lastTimestamp + 1;
        }
        mcService.put(MC_KEY_TIMESTAMP, timestamp);
        return timestamp;
    }

    /**
     * Converts specified {@link BigDecimal} value to a String which can be
     * sorted by lexical order. It would be useful for building an index table
     * on Datastore. Currently it does not support negative values.
     * 
     * Thanks so much to @ashigeru who advised me how to implement this method.
     * 
     * TODO to support negative value.
     * 
     * @condParam bd a {@link BigDecimal} positive value
     * @return {@link String} key for building a index table for the value
     */
    public String convertBigDecimalToIndexKey(BigDecimal bd) {

        // check if it's positive value
        assert bd.signum() != -1;

        // normalize the value to make it less than 1
        int scaleOffset = 0;
        while (bd.longValue() >= 1) {
            bd = bd.movePointLeft(1);
            scaleOffset++;
        }

        // convert it to String
        final String scalePrefix = String.format("%02d", scaleOffset);
        return scalePrefix + ":" + bd.toPlainString();
    }

    /**
     * Generates an UUID.
     * 
     * @return an UUID
     */
    public String generateUUID() {
        return generateRandomAlnums(UUID_DIGITS);
    }

    /**
     * Generates a String with random characteres made of alpha numerics.
     * 
     * @condParam digits
     * @return random alnum String
     */
    public String generateRandomAlnums(int digits) {
        final StringBuilder sb = new StringBuilder();
        while (sb.length() < digits) {
            sb.append(ALNUMS.charAt((int) (Math.random() * ALNUMS.length())));
        }
        return sb.toString();
    }

    /**
     * Encodes a property value of JSON doc into a String for filtering or
     * sorting. The value can be a String, Boolean or BigDecimal.
     * 
     * @param val
     *            the value to be encoded
     * @return encoded String
     */
    public String encodePropValue(Object val) {
        if (val == null) {
            return null;
        } else if (val instanceof String) {
            return (String) val;
        } else if (val instanceof Boolean) {
            return val.toString();
        } else if (val instanceof BigDecimal) {
            return JEUtils.i.convertBigDecimalToIndexKey((BigDecimal) val);
        } else {
            // try to convert the value to BigDecimal
            try {
                return JEUtils.i.convertBigDecimalToIndexKey(new BigDecimal(val.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    
}
