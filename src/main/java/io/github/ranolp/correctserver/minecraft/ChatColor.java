package io.github.ranolp.correctserver.minecraft;

import java.util.*;

public enum ChatColor {
    BLACK(0x000000, 0x000000, '0'),
    DARK_BLUE(0x0000AA, 0x00002A, '1'),
    DARK_GREEN(0x00AA00, 0x002A00, '2'),
    DARK_AQUA(0x00AAAA, 0x002A2A, '3'),
    DARK_RED(0xAA0000, 0x2A0000, '4'),
    DARK_PURPLE(0xAA00AA, 0x2A002A, '5'),
    GOLD(0xFFAA00, 0x3F2A00, '6'),
    GRAY(0xAAAAAA, 0x2A2A2A, '7'),
    DARK_GRAY(0x555555, 0x151515, '8'),
    BLUE(0x5555FF, 0x15153F, '9'),
    GREEN(0x55FF55, 0x153F15, 'a'),
    AQUA(0x55FFFF, 0x153F3F, 'b'),
    RED(0xFF5555, 0x3F1515, 'c'),
    PURPLE(0xFF55FF, 0x3F153F, 'd'),
    YELLOW(0xFFFF55, 0x3F3F15, 'e'),
    WHITE(0xFFFFFF, 0x3F3F3F, 'f');
    public static final char COLOR_CHAR = '§';

    private final int foreground;
    private final int background;
    private final char code;

    ChatColor(int foreground, int background, char code) {
        this.foreground = foreground;
        this.background = background;
        this.code = code;
    }

    public int getForeground() {
        return foreground;
    }

    public int getBackground() {
        return background;
    }

    public char getCode() {
        return code;
    }

    private static final Set<Character> codes = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'l', 'o', 'n',
                          'm', 'k', 'r')));
    private static final Map<Character, ChatColor> colorMap;

    static {
        Map<Character, ChatColor> map = new HashMap<>();
        for (ChatColor color : values()) {
            map.put(color.code, color);
        }
        colorMap = Collections.unmodifiableMap(map);
    }

    public static Set<Character> colorCodes() {
        return colorMap.keySet();
    }
    public static Set<Character> codes() {
        return codes;
    }

    public static boolean hasCode(char c){
        return codes.contains(c);
    }

    public static boolean hasColorCode(char c) {
        return colorMap.containsKey(c);
    }

    public static ChatColor byCode(char c) {
        return colorMap.get(c);
    }

    public static String stripColor(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '§' && i + 1 < input.length()) {
                char next = Character.toLowerCase(input.charAt(i + 1));
                if (hasCode(next)) {
                    i++;
                    continue;
                }
            }
            result.append(c);
        }
        return result.toString();
    }
}
