package com.hotato.udp.sdk.util;


import com.hotato.udp.sdk.common.SdkConstants;

import java.io.UnsupportedEncodingException;

public class SdkUtil {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String[] list = null;
        for (int i = 0; i < 10000000; i++) {
            byte[] bytes = SdkUtil.encodeBytes("[demo]", "我啊");
            list = SdkUtil.decodeBytes(bytes);
        }
        System.out.println("send:\n" + SdkUtil.encode(list));
        System.out.println("format cost: " + (System.currentTimeMillis() - start));

    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static String testKey(int i) {
        return SdkConstants.TEST_PIN_PREFIX + i;
    }

    public static String[] decodeBytes(byte[] bytes) {
        return decode(bytes).split(SdkConstants.SPLIT);
    }

    public static String decode(byte[] bytes) {
        try {
            return new String(bytes, SdkConstants.DEFAULT_CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] encodeBytes(String... args) {
        try {
            return encode(args).getBytes(SdkConstants.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encode(String... args) {
        if (args == null || args.length == 0) {
            System.out.println("encode args is null ! ");
            return "";
        }
        StringBuilder sb = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; i++) {
            sb.append(SdkConstants.SPLIT).append(args[i]);
        }
        return sb.toString();
    }

}
