package mr.minecraft15.shop.utils;

public class IntUtil {
    public static boolean isInt(final String s) {
	try {
	    Integer.parseInt(s);
	    return true;
	} catch (final NumberFormatException ex) {
	    return false;
	}
    }

    public static boolean isInt(final char c) {
	return isInt(c + "");
    }

    public static int getInt(final String s) {
	try {
	    return Integer.parseInt(s);
	} catch (final NumberFormatException ex) {
	    return 0;
	}
    }

    public static int getInt(final char c) {
	return getInt(c + "");
    }

    public static boolean isDouble(final String s) {
	try {
	    Double.parseDouble(s);
	    return true;
	} catch (final NumberFormatException ex) {
	    return false;
	}
    }

    public static double getDouble(final String s) {
	try {
	    return Double.parseDouble(s);
	} catch (final NumberFormatException ex) {
	    return 0;
	}
    }
}
