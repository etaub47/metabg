package utils;

import java.util.Random;

public class Utils
{
    private static Random random = new Random();
    
    /**
     * A safer version of Enum.valueOf() that returns a default value if the string passed in is null or 
     * is not a valid enum value
     *
     * @param <T> the type
     * @param enumType the class representing this enum object
     * @param sName the string to convert to an enum
     * @param defaultValue the default value to return if sName is null or not a valid enum value
     * @return the enum object
     * @author etaub
     * @since Aug 12, 2014
     */
    public static <T extends Enum<T>> T safeValueOf (Class<T> enumType, String sName, T defaultValue) {
        if (enumType == null || sName == null)
            return defaultValue;
        try { return Enum.valueOf(enumType, sName); }
        catch (IllegalArgumentException iae) {
            return defaultValue;
        }
    }
    
    /**
     * Returns a random number between 1 and N, inclusive
     * 
     * @param range the maximum integer to select
     * @return the random number
     * @author etaub
     * @since Aug 12, 2014
     */    
    public static int getRandomInt (int range) {
        return random.nextInt(range) + 1;
    }
}
