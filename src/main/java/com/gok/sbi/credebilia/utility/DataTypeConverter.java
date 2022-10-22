package com.gok.sbi.credebilia.utility;

import org.web3j.utils.Numeric;

import java.util.Collections;

public class DataTypeConverter {

    public static String asciiToHex(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char aChar : chars) {
            hex.append(Integer.toHexString((int) aChar));
        }

        return hex + String.join("", Collections.nCopies(32 - (hex.length() / 2), "00"));
    }



    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String toHexStringNoPrefix(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return Numeric.cleanHexPrefix(hexString.toString());
    }

    public static String hexToAscii(String hex) {
        String cleanHex = Numeric.cleanHexPrefix(hex);
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < cleanHex.length(); i+=2) {
            String str = cleanHex.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }
        return output.toString().replaceAll("\u0000","");
    }

}
