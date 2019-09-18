package com.example.mtreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

public class ToolFun {

	public static int asc_hex(byte[] asc, byte[] hex, int asclen) {
		String ss = new String(asc);
		int string_len = ss.length();
		int len = asclen; // int len = asclen/2;
		if (string_len % 2 == 1) {// if(asclen%2 ==1){
			ss = "0" + ss;
			len++;
		}
		for (int i = 0; i < len; i++) {
			hex[i] = (byte) Integer.parseInt(ss.substring(2 * i, 2 * i + 2), 16);
		}
		return 0;
	}

	public static int hex_asc(byte[] hex, byte[] asc, int blen) {
		for (int i = 0; i < blen; i++) {
			byte temp = (byte) (hex[i] & 0xf0);
			if (temp < 0) {
				temp = (byte) (hex[i] & 0x70);
				temp = (byte) ((byte) (temp >> 4) + 0x08);
			} else
				temp = (byte) (hex[i] >> 4);

			if ((temp >= 0) && (temp <= 9))
				asc[i * 2 + 0] = (byte) ((byte) temp + '0');
			else if ((temp >= 10) && (temp <= 15))
				asc[i * 2 + 0] = (byte) ((byte) temp + 'A' - 10);
			else
				asc[i * 2 + 0] = '0';

			temp = (byte) (hex[i] & 0x0f);
			if ((temp >= 0) && (temp <= 9))
				asc[i * 2 + 1] = (byte) ((byte) temp + '0');
			else if ((temp >= 10) && (temp <= 15))
				asc[i * 2 + 1] = (byte) ((byte) temp + 'A' - 10);
			else
				asc[i * 2 + 1] = '0';
		}
		return 0;
	}

	public static int hex_asc(byte[] hex, int hexPos, byte[] asc, int blen) {
		for (int i = hexPos; i < hexPos + blen; i++) {
			byte temp = (byte) (hex[i] & 0xf0);
			if (temp < 0) {
				temp = (byte) (hex[i] & 0x70);
				temp = (byte) ((byte) (temp >> 4) + 0x08);
			} else
				temp = (byte) (hex[i] >> 4);

			if ((temp >= 0) && (temp <= 9))
				asc[(i - hexPos) * 2 + 0] = (byte) ((byte) temp + '0');
			else if ((temp >= 10) && (temp <= 15))
				asc[(i - hexPos) * 2 + 0] = (byte) ((byte) temp + 'A' - 10);
			else
				asc[(i - hexPos) * 2 + 0] = '0';

			temp = (byte) (hex[i] & 0x0f);
			if ((temp >= 0) && (temp <= 9))
				asc[(i - hexPos) * 2 + 1] = (byte) ((byte) temp + '0');
			else if ((temp >= 10) && (temp <= 15))
				asc[(i - hexPos) * 2 + 1] = (byte) ((byte) temp + 'A' - 10);
			else
				asc[(i - hexPos) * 2 + 1] = '0';
		}
		return 0;
	}

	public static int cr_bcc(final int len, final byte[] data) {
		int temp = 0, i;
		for (i = 0; i < len; i++)
			temp = temp ^ data[i];
		return temp;
	}

	public static void splitFun(byte usplitdata[], byte ulen, byte splitdata[], byte slen) {
		// cmddata
		for (int nI = 0, nJ = 0; nI < ulen * 2; nI = nI + 2, nJ++) {
			splitdata[nI] = (byte) (((usplitdata[nJ] & 0xF0) >> 4) + 48);
			splitdata[nI + 1] = (byte) ((usplitdata[nJ] & 0x0F) + 48);
		}
	}

	public static int GetIntLen(byte byLenH, byte byLenL) {
		// TODO Auto-generated method stub
		int highLen = byLenH;
		if (highLen < 0)
			highLen = highLen + 256;
		int lowLen = byLenL;
		if (lowLen < 0)
			lowLen = lowLen + 256;
		return highLen * 256 + lowLen;
	}

	public static byte[] getHignAndLowByte(int st) {
		st = st < 0 ? ~st + 1 : st;
		byte[] retByte = new byte[2];
		retByte[0] = (byte) ((st & 0xFF00) >> 8);
		retByte[1] = (byte) ((st & 0x00FF));
		return retByte;
	}

	public static byte getUnsigedByte(byte sdata) {
		return (byte) (sdata < 0 ? sdata + 256 : sdata);
	}

	public static String PrintBuffer(byte[] bufData) {
		String str = "";
		int printLen = (bufData.length < 1000 ? bufData.length : 1000);
		for (int i = 0; i < printLen; i++) {
			str = str + String.format("%02X", bufData[i]) + " ";
		}
		return str;
	}

	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	public static int stringToInt(String intstr) {
		if (intstr.isEmpty()) {
			return 0;
		}
		Integer integer;
		integer = Integer.valueOf(intstr);
		return integer.intValue();
	}

	public static boolean checkWriteData(String strwrite) {
		boolean bPass = false;
		byte[] writeData = strwrite.toUpperCase().getBytes();
		for (int i = 0; i < writeData.length; i++) {
			if ((0x30 <= writeData[i] && writeData[i] <= 0x39) || ('A' <= writeData[i] && writeData[i] <= 'F')) {
				bPass = true;
			} else {
				bPass = false;
			}
		}
		return bPass;
	}

	public static void copyFileFromAssets(Context context, String assetsFilePath, String targetFileFullPath)
			throws IOException {

		Log.d("Tag", "copyFileFromAssets ");
		InputStream assestsFileImputStream;
		// try {
		assestsFileImputStream = context.getAssets().open(assetsFilePath);
		copyFile(assestsFileImputStream, targetFileFullPath);
		// } catch (IOException e) {
		// LogMg.d("Tag", "copyFileFromAssets " + "IOException-" +
		// e.getMessage());
		// e.printStackTrace();
		// }
	}

	private static void copyFile(InputStream in, String targetPath) throws IOException {
		// try {
		FileOutputStream fos = new FileOutputStream(new File(targetPath));
		byte[] buffer = new byte[1024];
		int byteCount = 0;
		while ((byteCount = in.read(buffer)) != -1) {// 循环从输入流读取
			// buffer字节
			fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
		}
		fos.flush();// 刷新缓冲区
		in.close();
		fos.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	private static boolean isFileByName(String string) {
		if (string.contains(".")) {
			return true;
		}
		return false;
	}

	public static void initLicData(Context context, String workPath) {
		File file = new File(workPath);
		if (!file.exists()) {
			file.mkdirs();
			try {
				String[] listFiles = context.getAssets().list("");
				for (String s : listFiles) {
					if (isFileByName(s)) {// 文件
						copyFileFromAssets(context, s, workPath + "/" + s);
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				if (file.exists())
					file.delete();
				e1.printStackTrace();
			}
		}

	}
}
