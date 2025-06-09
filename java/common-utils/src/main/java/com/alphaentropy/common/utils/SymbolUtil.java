package com.alphaentropy.common.utils;

public class SymbolUtil {
    public static boolean isAShare(String symbol) {
        return symbol.startsWith("6") || symbol.startsWith("3") || symbol.startsWith("0");
    }
}
