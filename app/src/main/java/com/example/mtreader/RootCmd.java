package com.example.mtreader;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;

import android.util.Log;

public final class RootCmd {
	private static final String	TAG	= "RootCmd";

	public static boolean isRoot(){
		boolean bool = false;
		try{
			bool = new File("/system/xbin/su").exists() || new File("/system/bin/su").exists();
		}catch(Exception e){
			e.printStackTrace();
		}
		return bool;
	}
	// --执行adb 命令--//
	private static void execShell(String cmd) {
		try {
			Process process = Runtime.getRuntime().exec("su");
			// 输出流
			DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
			// 输入流
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			dataOutputStream.writeBytes(cmd);
			dataOutputStream.flush();
			dataOutputStream.close();

			// 读取执行信息
			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			reader.close();
			Log.d(TAG, "execShell retsultMsg:" + output.toString());

			process.waitFor();
			// outputStream.close();
		} catch (Throwable t) {
			t.printStackTrace();
			Log.d(TAG, "execShell " + cmd + " " + t.getMessage());
		}
	}

	// Executes UNIX command.
	public static String getSELinuxMode() {
		String command = "getenforce";
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			reader.close();
			process.waitFor();
			return output.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void chmodShell(ArrayList<String> strCmdList) {
		for (int i = 0; i < strCmdList.size(); i++) {
			execShell(strCmdList.get(i));
		}
	}

	public static void chmodShell(Set<String> strCmdList) {
		for (String strcmd : strCmdList) {
			execShell(strcmd);
		}
	}
}
