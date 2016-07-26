/**
 * <pre>
 * 	This program is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, 
 * or (at your option) any later version. 
 * 
 * 	This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details. 
 * 	You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * </pre>
 */
package com.meidusa.amoeba.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StringUtil {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private final static char[]   c                  = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm', 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M' };

    public static String getRandomString(int size) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer(size);
        for (int i = 0; i < size; i++) {
            sb.append(c[Math.abs(random.nextInt()) % c.length]);
        }
        return sb.toString();
    }

    /**
     * Attempts to generate a string representation of the object using {@link Object#toString}, but catches any
     * exceptions that are thrown and reports them in the returned string instead. Useful for situations where you can't
     * trust the rat bastards that implemented the object you're toString()ing.
     */
    public static String safeToString(Object object) {
        try {
            return object.toString();
        } catch (Throwable t) {
            // We catch any throwable, even Errors. Someone is just trying to
            // debug something,
            // probably inside another catch block.
            return "<toString() failure: " + t + ">";
        }
    }

    public static boolean isEmpty(String str) {
        return ((str == null) || (str.length() == 0));
    }

    /**
     * Used to convert a time interval to a more easily human readable string of the form: <code>1d
     * 15h 4m 15s 987m</code>.
     */
    public static String intervalToString(long millis) {
        StringBuilder buf = new StringBuilder();
        boolean started = false;

        long days = millis / (24 * 60 * 60 * 1000);
        if (days != 0) {
            buf.append(days).append("d ");
            started = true;
        }

        long hours = (millis / (60 * 60 * 1000)) % 24;
        if (started || hours != 0) {
            buf.append(hours).append("h ");
        }

        long minutes = (millis / (60 * 1000)) % 60;
        if (started || minutes != 0) {
            buf.append(minutes).append("m ");
        }

        long seconds = (millis / (1000)) % 60;
        if (started || seconds != 0) {
            buf.append(seconds).append("s ");
        }

        buf.append(millis % 1000).append("ms");

        return buf.toString();
    }

    /**
     * Dumps the given bytes to STDOUT as a hex dump (up to length bytes).
     * 
     * @param byteBuffer the data to print as hex
     * @param length the number of bytes to print
     * @return ...
     */
    public static final String dumpAsHex(byte[] byteBuffer, int length) {
        StringBuffer outputBuf = new StringBuffer(length * 4);

        int p = 0;
        int rows = length / 8;

        for (int i = 0; (i < rows) && (p < length); i++) {
            int ptemp = p;

            for (int j = 0; j < 8; j++) {
                String hexVal = Integer.toHexString(byteBuffer[ptemp] & 0xff);

                if (hexVal.length() == 1) {
                    hexVal = "0" + hexVal; //$NON-NLS-1$
                }

                outputBuf.append(hexVal + " "); //$NON-NLS-1$
                ptemp++;
            }

            outputBuf.append("    "); //$NON-NLS-1$

            for (int j = 0; j < 8; j++) {
                int b = 0xff & byteBuffer[p];

                if (b > 32 && b < 127) {
                    outputBuf.append((char) b + " "); //$NON-NLS-1$
                } else {
                    outputBuf.append(". "); //$NON-NLS-1$
                }

                p++;
            }

            outputBuf.append("\n"); //$NON-NLS-1$
        }

        int n = 0;

        for (int i = p; i < length; i++) {
            String hexVal = Integer.toHexString(byteBuffer[i] & 0xff);

            if (hexVal.length() == 1) {
                hexVal = "0" + hexVal; //$NON-NLS-1$
            }

            outputBuf.append(hexVal + " "); //$NON-NLS-1$
            n++;
        }

        for (int i = n; i < 8; i++) {
            outputBuf.append("   "); //$NON-NLS-1$
        }

        outputBuf.append("    "); //$NON-NLS-1$

        for (int i = p; i < length; i++) {
            int b = 0xff & byteBuffer[i];

            if (b > 32 && b < 127) {
                outputBuf.append((char) b + " "); //$NON-NLS-1$
            } else {
                outputBuf.append(". "); //$NON-NLS-1$
            }
        }

        outputBuf.append("\n"); //$NON-NLS-1$

        return outputBuf.toString();
    }

    /**
     * Unfortunately, SJIS has 0x5c as a high byte in some of its double-byte characters, so we need to escape it.
     * 
     * @param origBytes the original bytes in SJIS format
     * @param origString the string that had .getBytes() called on it
     * @param offset where to start converting from
     * @param length how many characters to convert.
     * @return byte[] with 0x5c escaped
     */
    public static byte[] escapeEasternUnicodeByteStream(byte[] origBytes, String origString, int offset, int length) {
        if ((origBytes == null) || (origBytes.length == 0)) {
            return origBytes;
        }

        int bytesLen = origBytes.length;
        int bufIndex = 0;
        int strIndex = 0;

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(bytesLen);

        while (true) {
            if (origString.charAt(strIndex) == '\\') {
                // write it out as-is
                bytesOut.write(origBytes[bufIndex++]);

                // bytesOut.write(origBytes[bufIndex++]);
            } else {
                // Grab the first byte
                int loByte = origBytes[bufIndex];

                if (loByte < 0) {
                    loByte += 256; // adjust for signedness/wrap-around
                }

                // We always write the first byte
                bytesOut.write(loByte);

                //
                // The codepage characters in question exist between
                // 0x81-0x9F and 0xE0-0xFC...
                //
                // See:
                //
                // http://www.microsoft.com/GLOBALDEV/Reference/dbcs/932.htm
                //
                // Problematic characters in GBK
                //
                // U+905C : CJK UNIFIED IDEOGRAPH
                //
                // Problematic characters in Big5
                //
                // B9F0 = U+5C62 : CJK UNIFIED IDEOGRAPH
                //
                if (loByte >= 0x80) {
                    if (bufIndex < (bytesLen - 1)) {
                        int hiByte = origBytes[bufIndex + 1];

                        if (hiByte < 0) {
                            hiByte += 256; // adjust for signedness/wrap-around
                        }

                        // write the high byte here, and increment the index
                        // for the high byte
                        bytesOut.write(hiByte);
                        bufIndex++;

                        // escape 0x5c if necessary
                        if (hiByte == 0x5C) {
                            bytesOut.write(hiByte);
                        }
                    }
                } else if (loByte == 0x5c) {
                    if (bufIndex < (bytesLen - 1)) {
                        int hiByte = origBytes[bufIndex + 1];

                        if (hiByte < 0) {
                            hiByte += 256; // adjust for signedness/wrap-around
                        }

                        if (hiByte == 0x62) {
                            // we need to escape the 0x5c
                            bytesOut.write(0x5c);
                            bytesOut.write(0x62);
                            bufIndex++;
                        }
                    }
                }

                bufIndex++;
            }

            if (bufIndex >= bytesLen) {
                // we're done
                break;
            }

            strIndex++;
        }

        return bytesOut.toByteArray();
    }

    public static String toString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";
        StringBuffer buffer = new StringBuffer();
        for (byte byt : bytes) {
            buffer.append((char) byt);
        }
        return buffer.toString();
    }

    /**
     * 比较两个字符串（大小写敏感）。
     * 
     * <pre>
     * StringUtil.equals(null, null)   = true
     * StringUtil.equals(null, &quot;abc&quot;)  = false
     * StringUtil.equals(&quot;abc&quot;, null)  = false
     * StringUtil.equals(&quot;abc&quot;, &quot;abc&quot;) = true
     * StringUtil.equals(&quot;abc&quot;, &quot;ABC&quot;) = false
     * </pre>
     * 
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equals(str2);
    }

    /**
     * 比较两个字符串（大小写不敏感）。
     * 
     * <pre>
     * StringUtil.equalsIgnoreCase(null, null)   = true
     * StringUtil.equalsIgnoreCase(null, &quot;abc&quot;)  = false
     * StringUtil.equalsIgnoreCase(&quot;abc&quot;, null)  = false
     * StringUtil.equalsIgnoreCase(&quot;abc&quot;, &quot;abc&quot;) = true
     * StringUtil.equalsIgnoreCase(&quot;abc&quot;, &quot;ABC&quot;) = true
     * </pre>
     * 
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equalsIgnoreCase(str2);
    }

    /* ============================================================================ */
    /* 字符串分割函数。 */
    /*                                                                              */
    /* 将字符串按指定分隔符分割。 */
    /* ============================================================================ */

    /**
     * 将字符串按空白字符分割。
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.split(null)       = null
     * StringUtil.split(&quot;&quot;)         = []
     * StringUtil.split(&quot;abc def&quot;)  = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtil.split(&quot;abc  def&quot;) = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtil.split(&quot; abc &quot;)    = [&quot;abc&quot;]
     * </pre>
     * 
     * </p>
     * 
     * @param str 要分割的字符串
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split(String str) {
        return split(str, null, -1);
    }

    /**
     * 将字符串按指定字符分割。
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.split(null, *)         = null
     * StringUtil.split(&quot;&quot;, *)           = []
     * StringUtil.split(&quot;a.b.c&quot;, '.')    = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * StringUtil.split(&quot;a..b.c&quot;, '.')   = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * StringUtil.split(&quot;a:b:c&quot;, '.')    = [&quot;a:b:c&quot;]
     * StringUtil.split(&quot;a b c&quot;, ' ')    = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * </pre>
     * 
     * </p>
     * 
     * @param str 要分割的字符串
     * @param separatorChar 分隔符
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static String[] split(String str, char separatorChar) {
        if (str == null) {
            return null;
        }

        int length = str.length();

        if (length == 0) {
            return EMPTY_STRING_ARRAY;
        }

        List list = new ArrayList();
        int i = 0;
        int start = 0;
        boolean match = false;

        while (i < length) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                }

                start = ++i;
                continue;
            }

            match = true;
            i++;
        }

        if (match) {
            list.add(str.substring(start, i));
        }

        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * 将字符串按指定字符分割。
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.split(null, *)                = null
     * StringUtil.split(&quot;&quot;, *)                  = []
     * StringUtil.split(&quot;abc def&quot;, null)        = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtil.split(&quot;abc def&quot;, &quot; &quot;)         = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtil.split(&quot;abc  def&quot;, &quot; &quot;)        = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtil.split(&quot; ab:  cd::ef  &quot;, &quot;:&quot;)  = [&quot;ab&quot;, &quot;cd&quot;, &quot;ef&quot;]
     * StringUtil.split(&quot;abc.def&quot;, &quot;&quot;)          = [&quot;abc.def&quot;]
     * </pre>
     * 
     * </p>
     * 
     * @param str 要分割的字符串
     * @param separatorChars 分隔符
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split(String str, String separatorChars) {
        return split(str, separatorChars, -1);
    }

    /**
     * 将字符串按指定字符分割。
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.split(null, *, *)                 = null
     * StringUtil.split(&quot;&quot;, *, *)                   = []
     * StringUtil.split(&quot;ab cd ef&quot;, null, 0)        = [&quot;ab&quot;, &quot;cd&quot;, &quot;ef&quot;]
     * StringUtil.split(&quot;  ab   cd ef  &quot;, null, 0)  = [&quot;ab&quot;, &quot;cd&quot;, &quot;ef&quot;]
     * StringUtil.split(&quot;ab:cd::ef&quot;, &quot;:&quot;, 0)        = [&quot;ab&quot;, &quot;cd&quot;, &quot;ef&quot;]
     * StringUtil.split(&quot;ab:cd:ef&quot;, &quot;:&quot;, 2)         = [&quot;ab&quot;, &quot;cdef&quot;]
     * StringUtil.split(&quot;abc.def&quot;, &quot;&quot;, 2)           = [&quot;abc.def&quot;]
     * </pre>
     * 
     * </p>
     * 
     * @param str 要分割的字符串
     * @param separatorChars 分隔符
     * @param max 返回的数组的最大个数，如果小于等于0，则表示无限制
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static String[] split(String str, String separatorChars, int max) {
        if (str == null) {
            return null;
        }

        int length = str.length();

        if (length == 0) {
            return EMPTY_STRING_ARRAY;
        }

        List list = new ArrayList();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;

        if (separatorChars == null) {
            // null表示使用空白作为分隔符
            while (i < length) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add(str.substring(start, i));
                        match = false;
                    }

                    start = ++i;
                    continue;
                }

                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // 优化分隔符长度为1的情形
            char sep = separatorChars.charAt(0);

            while (i < length) {
                if (str.charAt(i) == sep) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add(str.substring(start, i));
                        match = false;
                    }

                    start = ++i;
                    continue;
                }

                match = true;
                i++;
            }
        } else {
            // 一般情形
            while (i < length) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add(str.substring(start, i));
                        match = false;
                    }

                    start = ++i;
                    continue;
                }

                match = true;
                i++;
            }
        }

        if (match) {
            list.add(str.substring(start, i));
        }

        return (String[]) list.toArray(new String[list.size()]);
    }

    /* ============================================================================ */
    /* 替换子串。 */
    /* ============================================================================ */

    /**
     * 替换指定的子串，只替换第一个出现的子串。
     * <p>
     * 如果字符串为<code>null</code>则返回<code>null</code>，如果指定子串为<code>null</code>，则返回原字符串。
     * 
     * <pre>
     * StringUtil.replaceOnce(null, *, *)        = null
     * StringUtil.replaceOnce(&quot;&quot;, *, *)          = &quot;&quot;
     * StringUtil.replaceOnce(&quot;aba&quot;, null, null) = &quot;aba&quot;
     * StringUtil.replaceOnce(&quot;aba&quot;, null, null) = &quot;aba&quot;
     * StringUtil.replaceOnce(&quot;aba&quot;, &quot;a&quot;, null)  = &quot;aba&quot;
     * StringUtil.replaceOnce(&quot;aba&quot;, &quot;a&quot;, &quot;&quot;)    = &quot;ba&quot;
     * StringUtil.replaceOnce(&quot;aba&quot;, &quot;a&quot;, &quot;z&quot;)   = &quot;zba&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param text 要扫描的字符串
     * @param repl 要搜索的子串
     * @param with 替换字符串
     * @return 被替换后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String replaceOnce(String text, String repl, String with) {
        return replace(text, repl, with, 1);
    }

    /**
     * 替换指定的子串，替换所有出现的子串。
     * <p>
     * 如果字符串为<code>null</code>则返回<code>null</code>，如果指定子串为<code>null</code>，则返回原字符串。
     * 
     * <pre>
     * StringUtil.replace(null, *, *)        = null
     * StringUtil.replace(&quot;&quot;, *, *)          = &quot;&quot;
     * StringUtil.replace(&quot;aba&quot;, null, null) = &quot;aba&quot;
     * StringUtil.replace(&quot;aba&quot;, null, null) = &quot;aba&quot;
     * StringUtil.replace(&quot;aba&quot;, &quot;a&quot;, null)  = &quot;aba&quot;
     * StringUtil.replace(&quot;aba&quot;, &quot;a&quot;, &quot;&quot;)    = &quot;b&quot;
     * StringUtil.replace(&quot;aba&quot;, &quot;a&quot;, &quot;z&quot;)   = &quot;zbz&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param text 要扫描的字符串
     * @param repl 要搜索的子串
     * @param with 替换字符串
     * @return 被替换后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }

    /**
     * 替换指定的子串，替换指定的次数。
     * <p>
     * 如果字符串为<code>null</code>则返回<code>null</code>，如果指定子串为<code>null</code>，则返回原字符串。
     * 
     * <pre>
     * StringUtil.replace(null, *, *, *)         = null
     * StringUtil.replace(&quot;&quot;, *, *, *)           = &quot;&quot;
     * StringUtil.replace(&quot;abaa&quot;, null, null, 1) = &quot;abaa&quot;
     * StringUtil.replace(&quot;abaa&quot;, null, null, 1) = &quot;abaa&quot;
     * StringUtil.replace(&quot;abaa&quot;, &quot;a&quot;, null, 1)  = &quot;abaa&quot;
     * StringUtil.replace(&quot;abaa&quot;, &quot;a&quot;, &quot;&quot;, 1)    = &quot;baa&quot;
     * StringUtil.replace(&quot;abaa&quot;, &quot;a&quot;, &quot;z&quot;, 0)   = &quot;abaa&quot;
     * StringUtil.replace(&quot;abaa&quot;, &quot;a&quot;, &quot;z&quot;, 1)   = &quot;zbaa&quot;
     * StringUtil.replace(&quot;abaa&quot;, &quot;a&quot;, &quot;z&quot;, 2)   = &quot;zbza&quot;
     * StringUtil.replace(&quot;abaa&quot;, &quot;a&quot;, &quot;z&quot;, -1)  = &quot;zbzz&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param text 要扫描的字符串
     * @param repl 要搜索的子串
     * @param with 替换字符串
     * @param max maximum number of values to replace, or <code>-1</code> if no maximum
     * @return 被替换后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String replace(String text, String repl, String with, int max) {
        if ((text == null) || (repl == null) || (with == null) || (repl.length() == 0) || (max == 0)) {
            return text;
        }

        StringBuffer buf = new StringBuffer(text.length());
        int start = 0;
        int end = 0;

        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();

            if (--max == 0) {
                break;
            }
        }

        buf.append(text.substring(start));
        return buf.toString();
    }

    /**
     * 将字符串中所有指定的字符，替换成另一个。
     * <p>
     * 如果字符串为<code>null</code>则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.replaceChars(null, *, *)        = null
     * StringUtil.replaceChars(&quot;&quot;, *, *)          = &quot;&quot;
     * StringUtil.replaceChars(&quot;abcba&quot;, 'b', 'y') = &quot;aycya&quot;
     * StringUtil.replaceChars(&quot;abcba&quot;, 'z', 'y') = &quot;abcba&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要扫描的字符串
     * @param searchChar 要搜索的字符
     * @param replaceChar 替换字符
     * @return 被替换后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String replaceChars(String str, char searchChar, char replaceChar) {
        if (str == null) {
            return null;
        }

        return str.replace(searchChar, replaceChar);
    }

    /**
     * 将字符串中所有指定的字符，替换成另一个。
     * <p>
     * 如果字符串为<code>null</code>则返回<code>null</code>。如果搜索字符串为<code>null</code>或空，则返回原字符串。
     * </p>
     * <p>
     * 例如： <code>replaceChars(&quot;hello&quot;, &quot;ho&quot;, &quot;jy&quot;) = jelly</code>。
     * </p>
     * <p>
     * 通常搜索字符串和替换字符串是等长的，如果搜索字符串比替换字符串长，则多余的字符将被删除。 如果搜索字符串比替换字符串短，则缺少的字符将被忽略。
     * 
     * <pre>
     * StringUtil.replaceChars(null, *, *)           = null
     * StringUtil.replaceChars(&quot;&quot;, *, *)             = &quot;&quot;
     * StringUtil.replaceChars(&quot;abc&quot;, null, *)       = &quot;abc&quot;
     * StringUtil.replaceChars(&quot;abc&quot;, &quot;&quot;, *)         = &quot;abc&quot;
     * StringUtil.replaceChars(&quot;abc&quot;, &quot;b&quot;, null)     = &quot;ac&quot;
     * StringUtil.replaceChars(&quot;abc&quot;, &quot;b&quot;, &quot;&quot;)       = &quot;ac&quot;
     * StringUtil.replaceChars(&quot;abcba&quot;, &quot;bc&quot;, &quot;yz&quot;)  = &quot;ayzya&quot;
     * StringUtil.replaceChars(&quot;abcba&quot;, &quot;bc&quot;, &quot;y&quot;)   = &quot;ayya&quot;
     * StringUtil.replaceChars(&quot;abcba&quot;, &quot;bc&quot;, &quot;yzx&quot;) = &quot;ayzya&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要扫描的字符串
     * @param searchChars 要搜索的字符串
     * @param replaceChars 替换字符串
     * @return 被替换后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String replaceChars(String str, String searchChars, String replaceChars) {
        if ((str == null) || (str.length() == 0) || (searchChars == null) || (searchChars.length() == 0)) {
            return str;
        }

        char[] chars = str.toCharArray();
        int len = chars.length;
        boolean modified = false;

        for (int i = 0, isize = searchChars.length(); i < isize; i++) {
            char searchChar = searchChars.charAt(i);

            if ((replaceChars == null) || (i >= replaceChars.length())) {
                // 删除
                int pos = 0;

                for (int j = 0; j < len; j++) {
                    if (chars[j] != searchChar) {
                        chars[pos++] = chars[j];
                    } else {
                        modified = true;
                    }
                }

                len = pos;
            } else {
                // 替换
                for (int j = 0; j < len; j++) {
                    if (chars[j] == searchChar) {
                        chars[j] = replaceChars.charAt(i);
                        modified = true;
                    }
                }
            }
        }

        if (!modified) {
            return str;
        }

        return new String(chars, 0, len);
    }
}
