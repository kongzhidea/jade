package com.meidusa.amoeba.util;

/**
 * 字符串填充格式化工具
 * 
 * @author hexianmao
 * @version 2008-11-24 下午12:58:17
 */
public class StringFillFormat {

    // 右对齐格式化字符串
    public static final int   ALIGN_RIGHT      = 0;

    // 左对齐格式化字符串
    public static final int   ALIGN_LEFT       = 1;

    private static final char defaultSplitChar = ' ';

    /**
     * 格式化后返回的字符串
     * 
     * @param s 需要格式化的原始字符串，默认按左对齐。
     * @param fillLength 填充长度
     * @return String
     */
    public static String format(String s, int fillLength) {
        return format(s, fillLength, defaultSplitChar, ALIGN_LEFT);
    }

    /**
     * 格式化后返回的字符串
     * 
     * @param i 需要格式化的数字类型，默认按右对齐。
     * @param fillLength 填充长度
     * @return String
     */
    public static String format(int i, int fillLength) {
        return format(Integer.toString(i), fillLength, defaultSplitChar, ALIGN_RIGHT);
    }

    /**
     * 格式化后返回的字符串
     * 
     * @param l 需要格式化的数字类型，默认按右对齐。
     * @param fillLength 填充长度
     * @return String
     */
    public static String format(long l, int fillLength) {
        return format(Long.toString(l), fillLength, defaultSplitChar, ALIGN_RIGHT);
    }

    /**
     * @param s 需要格式化的原始字符串
     * @param fillLength 填充长度
     * @param fillChar 填充的字符
     * @param align 填充方式（左边填充还是右边填充）
     * @return String
     */
    public static String format(String s, int fillLength, char fillChar, int align) {
        if (s == null) {
            s = "";
        } else {
            s = s.trim();
        }
        int charLen = fillLength - s.length();
        if (charLen > 0) {
            char[] fills = new char[charLen];
            for (int i = 0; i < charLen; i++) {
                fills[i] = fillChar;
            }
            StringBuilder str = new StringBuilder(s);
            switch (align) {
                case ALIGN_RIGHT:
                    str.insert(0, fills);
                    break;
                case ALIGN_LEFT:
                    str.append(fills);
                    break;
                default:
                    str.append(fills);
            }
            return str.toString();
        } else {
            return s;
        }
    }
}
