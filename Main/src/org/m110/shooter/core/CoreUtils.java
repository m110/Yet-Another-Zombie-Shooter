package org.m110.shooter.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author m1_10sz <m110@m110.pl>
 */
public abstract class CoreUtils {

    public static String sha1(String input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] result = md.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
