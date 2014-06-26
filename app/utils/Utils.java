package utils;

public class Utils
{
    public static <T extends Enum<T>> T safeValueOf (Class<T> enumType, String sName, T defaultValue) {
        if (enumType == null || sName == null)
            return defaultValue;
        try { return Enum.valueOf(enumType, sName); }
        catch (IllegalArgumentException iae) {
            return defaultValue;
        }
    }
}
