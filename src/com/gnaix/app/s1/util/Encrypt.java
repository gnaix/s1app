package com.gnaix.app.s1.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.gnaix.common.util.Base64;

public class Encrypt {

    private static final String KEY = "moc'ts1egats.sbb";

    /**
     * Method To Encrypt The String
     */
    public static String encrypt(String unencryptedString) {
        byte[] bytes = unencryptedString.getBytes();
        byte[] keyBytes = KEY.getBytes();
        int size1 = bytes.length;
        int size2 = keyBytes.length;
        for(int i=0;i<size1;i++){
            for(int j=0;j<size2;j++){
                bytes[i]^=keyBytes[j];
            }
        }
        String encryptedString =  new String(bytes);
        System.out.println(encryptedString);
        return encryptedString;
    }

    /**
     * Method To Decrypt An Ecrypted String
     */
    public static String decrypt(String encryptedString) {
        byte[] bytes = encryptedString.getBytes();
        byte[] keyBytes = KEY.getBytes();
        int size1 = bytes.length;
        int size2 = keyBytes.length;
        for(int i=0;i<size1;i++){
            for(int j=0;j<size2;j++){
                bytes[i]^=keyBytes[j];
            }
        }
        String painText =  new String(bytes);
        System.out.println(painText);
        return painText;
    }
}
