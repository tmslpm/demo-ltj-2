
package com.jtorleonstudios.libraryferret.utils;

import java.util.Arrays;

public interface Color {
    int INVISIBLE = toHex(0, 0, 0, 0);
    int BLACK = toHex(0, 0, 0, 255);
    int DARK_BLUE = toHex(0, 0, 170, 255);
    int DARK_GREEN = toHex(0, 170, 0, 255);
    int DARK_AQUA = toHex(0, 170, 170, 255);
    int DARK_RED = toHex(170, 0, 0, 255);
    int DARK_PURPLE = toHex(170, 0, 170, 255);
    int GOLD = toHex(255, 180, 100, 255);
    int GRAY = toHex(170, 170, 170, 255);
    int DARK_GRAY = toHex(85, 85, 85, 255);
    int BLUE = toHex(85, 85, 255, 255);
    int GREEN = toHex(85, 255, 85, 255);
    int AQUA = toHex(85, 255, 255, 255);
    int LIGHT_PURPLE = toHex(255, 85, 255, 255);
    int RED = toHex(255, 85, 85, 255);
    int YELLOW = toHex(255, 255, 85, 255);
    int WHITE = toHex(255, 255, 255, 255);

    static int formattingToHex(String colorName) {
        if (colorName == null) {
            return BLACK;
        } else {
            switch (colorName.toUpperCase().trim()) {
                case "DARK_BLUE":
                    return DARK_BLUE;
                case "DARK_GREEN":
                    return DARK_GREEN;
                case "DARK_AQUA":
                    return DARK_AQUA;
                case "DARK_RED":
                    return DARK_RED;
                case "DARK_PURPLE":
                    return DARK_PURPLE;
                case "GOLD":
                    return GOLD;
                case "GRAY":
                    return GRAY;
                case "DARK_GRAY":
                    return DARK_GRAY;
                case "BLUE":
                    return BLUE;
                case "GREEN":
                    return GREEN;
                case "AQUA":
                    return AQUA;
                case "RED":
                    return RED;
                case "LIGHT_PURPLE":
                    return LIGHT_PURPLE;
                case "YELLOW":
                    return YELLOW;
                case "WHITE":
                    return WHITE;
                case "BLACK":
                default:
                    return BLACK;
            }
        }
    }

    static int toHex(int[] RGBA) {
        return RGBA.length == 4 ? toHex(RGBA[0], RGBA[1], RGBA[2], RGBA[3]) : (RGBA.length == 3 ? toHex(RGBA[0], RGBA[1], RGBA[2], 255) : (RGBA.length == 2 ? toHex(RGBA[0], RGBA[1], 0, 255) : (RGBA.length == 1 ? toHex(RGBA[0], 0, 0, 255) : toHex(0, 0, 0, 255))));
    }

    static int toHex(int red, int green, int blue, int alpha) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    static int toHex(String RGBA) {
        return toHex(toRGBA(RGBA));
    }

    static int[] toRGBA(String RGBA) {
        if (isRGBorRGBA(RGBA)) {
            int[] data;
            try {
                data = Arrays.stream(RGBA.replaceAll("\\s+", "").toLowerCase().replaceFirst("^rgb\\(|^rgba\\(", "").replaceFirst("\\)", "").split(",")).mapToInt(Integer::parseInt).toArray();
            } catch (Exception var3) {
                return new int[]{0, 0, 0, 255};
            }

            if (data.length == 4) {
                return new int[]{data[0], data[1], data[2], data[3]};
            } else if (data.length == 3) {
                return new int[]{data[0], data[1], data[2], 255};
            } else if (data.length == 2) {
                return new int[]{data[0], data[1], 0, 255};
            } else {
                return data.length == 1 ? new int[]{data[0], 0, 0, 255} : new int[]{0, 0, 0, 255};
            }
        } else {
            return new int[]{0, 0, 0, 255};
        }
    }

    static boolean isRGBorRGBA(String entry) {
        return entry.contains("rgba") || entry.contains("rgb");
    }

    static String toString(int[] RGBA) {
        return RGBA.length == 4 ? toString(RGBA[0], RGBA[1], RGBA[2], RGBA[3]) : (RGBA.length == 3 ? toString(RGBA[0], RGBA[1], RGBA[2], 255) : (RGBA.length == 2 ? toString(RGBA[0], RGBA[1], 0, 255) : (RGBA.length == 1 ? toString(RGBA[0], 0, 0, 255) : "rgba(0, 0, 0, 255)")));
    }

    static String toString(int red, int green, int blue) {
        return toString(red, green, blue, 255);
    }

    static String toString(int red, int green, int blue, int alpha) {
        return "rgba(" + red + ", " + green + ", " + blue + ", " + alpha + ")";
    }
}
