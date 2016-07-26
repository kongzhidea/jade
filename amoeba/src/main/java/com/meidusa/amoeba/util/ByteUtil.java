package com.meidusa.amoeba.util;

/**
 * byte处理工具包
 * 
 * @author hexianmao
 * @version 2008-10-9 下午10:13:30
 */
public final class ByteUtil {

    private ByteUtil(){
    }

    /**
     * 16bit to int
     */
    public static final int toInt16BE(byte[] src, int offset) {
        return ((src[offset] & 0xFF) << 8) | (src[++offset] & 0xFF);
    }

    /**
     * 32bit to int
     */
    public static final int toInt32BE(byte[] src, int offset) {
        return ((src[offset] & 0xFF) << 24) | ((src[++offset] & 0xFF) << 16) | ((src[++offset] & 0xFF) << 8) | (src[++offset] & 0xFF);
    }

    /**
     * 64bit to long
     */
    public static final long toLong64BE(byte[] src, int offset) {
        return ((src[offset] & 0xFFL) << 56) | ((src[++offset] & 0xFF) << 48) | ((src[++offset] & 0xFF) << 40) | ((src[++offset] & 0xFF) << 32) | ((src[offset] & 0xFF) << 24) | ((src[++offset] & 0xFF) << 16) | ((src[++offset] & 0xFF) << 8) | (src[++offset] & 0xFF);
    }

    /**
     * int to 16bit
     */
    public static final int toByte16BE(short value, byte[] dst, int offset) {
        dst[offset] = (byte) ((value >> 8) & 0xFF);
        dst[++offset] = (byte) (value & 0xFF);
        return 2;
    }

    /**
     * int to 32bit
     */
    public static final int toByte32BE(int value, byte[] dst, int offset) {
        dst[offset] = (byte) ((value >> 24) & 0xFF);
        dst[++offset] = (byte) ((value >> 16) & 0xFF);
        dst[++offset] = (byte) ((value >> 8) & 0xFF);
        dst[++offset] = (byte) (value & 0xFF);
        return 4;
    }

    /**
     * long to 64bit
     */
    public static final int toByte64BE(long value, byte[] dst, int offset) {
        dst[offset] = (byte) ((value >> 56) & 0xFF);
        dst[++offset] = (byte) ((value >> 48) & 0xFF);
        dst[++offset] = (byte) ((value >> 40) & 0xFF);
        dst[++offset] = (byte) ((value >> 32) & 0xFF);
        dst[++offset] = (byte) ((value >> 24) & 0xFF);
        dst[++offset] = (byte) ((value >> 16) & 0xFF);
        dst[++offset] = (byte) ((value >> 8) & 0xFF);
        dst[++offset] = (byte) (value & 0xFF);
        return 8;
    }

    public static String toHex(byte[] b, int offset, int len) {
        StringBuilder s = new StringBuilder();
        for (int i = offset; i < (offset + len); i++) {
            s.append(String.format("%1$02x", (b[i] & 0xff)));
            s.append(" ");
        }
        return s.toString();
    }

    public static String fromHex(String hexString) {
        return fromHex(hexString, null);
    }

    public static String fromHex(String hexString, String charset) {
        try {
            String[] hex = hexString.split(" ");
            byte[] b = new byte[hex.length];
            for (int i = 0; i < hex.length; i++) {
                b[i] = (byte) (Integer.parseInt(hex[i], 16) & 0xff);
            }

            if (charset == null) {
                return new String(b);
            }
            return new String(b, charset);
        } catch (Exception e) {
            return null;
        }
    }

}
