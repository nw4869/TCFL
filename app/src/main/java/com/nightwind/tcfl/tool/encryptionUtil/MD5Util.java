/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.nightwind.tcfl.tool.encryptionUtil;

import java.security.MessageDigest;

public class MD5Util {
// 
//    public static String SHA1(String decript) {
//        try {
//            MessageDigest digest = java.security.MessageDigest
//                    .getInstance("SHA-1");
//            digest.update(decript.getBytes());
//            byte messageDigest[] = digest.digest();
//            // Create Hex String
//            StringBuffer hexString = new StringBuffer();
//            // 字节数组转换为 十六进制 数
//            for (int i = 0; i < messageDigest.length; i++) {
//                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
//                if (shaHex.length() < 2) {
//                    hexString.append(0);
//                }
//                hexString.append(shaHex);
//            }
//            return hexString.toString().toUpperCase();
// 
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
// 
//    public static String SHA(String decript) {
//        try {
//            MessageDigest digest = java.security.MessageDigest
//                    .getInstance("SHA");
//            digest.update(decript.getBytes());
//            byte messageDigest[] = digest.digest();
//            // Create Hex String
//            StringBuffer hexString = new StringBuffer();
//            // 字节数组转换为 十六进制 数
//            for (int i = 0; i < messageDigest.length; i++) {
//                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
//                if (shaHex.length() < 2) {
//                    hexString.append(0);
//                }
//                hexString.append(shaHex);
//            }
//            return hexString.toString();
// 
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
// 
//    public static String MD5(String input) {
//        try {
//            // 获得MD5摘要算法的 MessageDigest 对象
//            MessageDigest mdInst = MessageDigest.getInstance("MD5");
//            // 使用指定的字节更新摘要
//            mdInst.update(input.getBytes());
//            // 获得密文
//            byte[] md = mdInst.digest();
//            // 把密文转换成十六进制的字符串形式
//            StringBuffer hexString = new StringBuffer();
//            // 字节数组转换为 十六进制 数
//            for (int i = 0; i < md.length; i++) {
//                String shaHex = Integer.toHexString(md[i] & 0xFF);
//                if (shaHex.length() < 2) {
//                    hexString.append(0);
//                }
//                hexString.append(shaHex);
//            }
//            return hexString.toString();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
    ////////////////////////////////////////////////
    
    
    
    

	private static final String ALGORITHM = "MD5";

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * encode string
	 *
	 * @param algorithm
	 * @param str
	 * @return String
	 */
//	public static String encode(String algorithm, String str) {
//		if (str == null) {
//			return null;
//		}
//		try {
//			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
//			messageDigest.update(str.getBytes());
//			return getFormattedText(messageDigest.digest());
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//
//	}

	/**
	 * encode By MD5
	 *
	 * @param str
	 * @return String
	 */
	public static String encode(String str) {
		if (str == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
			messageDigest.update(str.getBytes());
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Takes the raw bytes from the digest and formats them correct.
	 *
	 * @param bytes
	 *            the raw bytes from the digest.
	 * @return the formatted bytes.
	 */
	private static String getFormattedText(byte[] bytes) {
		int len = bytes.length;
		StringBuilder buf = new StringBuilder(len * 2);
		// 把密文转换成十六进制的字符串形式
		for (int j = 0; j < len; j++) { 			
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString().toUpperCase();
	}
    
    
    
    
    
    
    
    public static void main(String[] args) {
//    	String md5 = SHA1("111111");
//    	System.out.println(md5);
    	

		System.out.println("48690B165087-F73D-F6F0-AD3F-AA19DA28F094 MD5  :"
				+ encode("48690B165087-F73D-F6F0-AD3F-AA19DA28F094"));
		//4869+salt --> 2B44D7F4C91E8F9646CF7D9D55359983
		
		
//		System.out.println("111111 MD5  :"
//				+ encode("MD5", "111111"));
//		System.out.println("111111 SHA1 :"
//				+ encode("SHA1", "111111"));
    }
}