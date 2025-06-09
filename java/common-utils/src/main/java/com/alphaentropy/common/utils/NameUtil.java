package com.alphaentropy.common.utils;

public class NameUtil {
    public static String camelToUnix(String string) {
        StringBuilder sb = new StringBuilder(string);
        int temp = 0;
        for (int i=0 ;i < string.length(); i++) {
            if(Character.isUpperCase(string.charAt(i)) && i > 0) {
                sb.insert(i+temp,"_");
                temp+=1;
            }
        }
        return sb.toString().toLowerCase();
    }

    public static String unixToCamel(String string) {
        String[] sub = string.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sub.length; i++) {
            if (i > 0) {
                char[] chars = sub[i].toCharArray();
                chars[0] -= 32;
                sb.append(String.valueOf(chars));
            } else {
                sb.append(sub[i]);
            }
        }
        return sb.toString();
    }
}
