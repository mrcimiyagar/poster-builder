package com.one.two.three.poster.back.utils;

/**
 * Created by Pouyan-PC on 11/11/2017.
 */

public class CryptoHelper {

    public static String xor(String input, String key){
        byte[] a = input.getBytes();
        byte[] b = key.getBytes();
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ b[i % b.length]);
        }
        return new String(out);
    }

}
