package io.github.ranolp.correctserver.util;

public class StringUtil {
    public static boolean isInteger(String s, boolean positive) {
        char[] chars = s.toCharArray();
        if (chars.length == 1) { return Character.isDigit(chars[0]); }
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if ((positive && c == '-') || (!Character.isDigit(c) && i != 0 && c != '+')) {
                return false;
            }
        }
        return true;
    }
}
