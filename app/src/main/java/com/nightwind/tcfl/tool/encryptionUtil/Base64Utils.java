/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.nightwind.tcfl.tool.encryptionUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import it.sauronsoftware.base64.Base64;

/** */

/**
 * <p>
 * BASE64������빤�߰�
 * </p>
 * <p>
 * ����javabase64-1.3.1.jar
 * </p>
 * 
 * @author IceWee
 * @date 2012-5-19
 * @version 1.0
 */
public class Base64Utils {

	/** */
	/**
	 * �ļ���ȡ�������С
	 */
	private static final int CACHE_SIZE = 1024;

	/** */
	/**
	 * <p>
	 * BASE64�ַ����Ϊ���������
	 * </p>
	 * 
	 * @param base64
	 * @return
	 * @throws Exception
	 */
	public static byte[] decode(String base64) throws Exception {
		return Base64.decode(toOriginBase64(base64).getBytes());
	}
	

	/** */
	/**
	 * <p>
	 * ��������ݱ���ΪBASE64�ַ�
	 * </p>
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public static String encode(byte[] bytes) throws Exception {
		return toUrlSafeBase64(new String(Base64.encode(bytes)));
	}

	/** */
	/**
	 * <p>
	 * ���ļ�����ΪBASE64�ַ�
	 * </p>
	 * <p>
	 * ���ļ����ã����ܻᵼ���ڴ����
	 * </p>
	 * 
	 * @param filePath
	 *            �ļ����·��
	 * @return
	 * @throws Exception
	 */
	public static String encodeFile(String filePath) throws Exception {
		byte[] bytes = fileToByte(filePath);
		return encode(bytes);
	}

	/** */
	/**
	 * <p>
	 * BASE64�ַ�ת���ļ�
	 * </p>
	 * 
	 * @param filePath
	 *            �ļ����·��
	 * @param base64
	 *            �����ַ�
	 * @throws Exception
	 */
	public static void decodeToFile(String filePath, String base64)
			throws Exception {
		byte[] bytes = decode(base64);
		byteArrayToFile(bytes, filePath);
	}

	/** */
	/**
	 * <p>
	 * �ļ�ת��Ϊ����������
	 * </p>
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @return
	 * @throws Exception
	 */
	public static byte[] fileToByte(String filePath) throws Exception {
		byte[] data = new byte[0];
		File file = new File(filePath);
		if (file.exists()) {
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
			byte[] cache = new byte[CACHE_SIZE];
			int nRead = 0;
			while ((nRead = in.read(cache)) != -1) {
				out.write(cache, 0, nRead);
				out.flush();
			}
			out.close();
			in.close();
			data = out.toByteArray();
		}
		return data;
	}

	/** */
	/**
	 * <p>
	 * ���������д�ļ�
	 * </p>
	 * 
	 * @param bytes
	 *            ���������
	 * @param filePath
	 *            �ļ����Ŀ¼
	 */
	public static void byteArrayToFile(byte[] bytes, String filePath)
			throws Exception {
		InputStream in = new ByteArrayInputStream(bytes);
		File destFile = new File(filePath);
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}
		destFile.createNewFile();
		OutputStream out = new FileOutputStream(destFile);
		byte[] cache = new byte[CACHE_SIZE];
		int nRead = 0;
		while ((nRead = in.read(cache)) != -1) {
			out.write(cache, 0, nRead);
			out.flush();
		}
		out.close();
		in.close();
	}


	public static String toUrlSafeBase64(String origin) {
		return origin.replaceAll("\\+", "-").replaceAll("/", "_");
	}

	public static String toOriginBase64(String safe) {
		return safe.replaceAll("-", "\\+").replaceAll("_", "/");
	}
}
