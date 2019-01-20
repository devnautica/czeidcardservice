package com.devnautica.czeidcardservice.utils;

import cz.paralelnipolis.obcanka.core.certificates.Certificate;
import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

public class FileEncoder {
	public static String certDataToBase64String(Certificate cert){
		return certDataToBase64String(cert.getData());
	}
	public static String certDataToBase64String(byte[] data){
		try{

			byte[] encoded = Base64.getEncoder().encode(data);
			return new String(encoded);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static String fileToString(String filePath){
		try{
			File fileHandle = new File(filePath);
			long length = fileHandle.length();
			byte[] bytes = new byte[(int)length];

			DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
			int index = 0;
			while (in.available() != 0){
				bytes[index] = in.readByte();
				index++;
			}

			byte[] encoded = Base64.getEncoder().encode(bytes);
			return new String(encoded);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static void base64StringToFile(String encodedString, String destinationPath){
		try{
			byte[] decoded = Base64.getDecoder().decode(encodedString);
			FileUtils.writeByteArrayToFile(new File(destinationPath), decoded);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
