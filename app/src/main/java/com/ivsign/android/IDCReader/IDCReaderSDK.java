package com.ivsign.android.IDCReader;

import android.os.Environment;
import android.util.Log;

public class IDCReaderSDK {
    private static final String	TAG				= "unpack";
    private static boolean		LOAD_SO_FLAG	= false;

    private static final String LICENSE_PATH	= Environment.getExternalStorageDirectory() + "/wltlib";//file:///android_asset/wltlib";
    public static int Init() {
        return wltInit(Environment.getExternalStorageDirectory() + "/wltlib");
    }

    public static String GetLicensePath(){
        return LICENSE_PATH;
    }

    public static int unpack(byte[] wltdata, byte[] licdata) {
        return wltGetBMP(wltdata, licdata);
    }

    // native functin interface
    public static native int wltInit(String workPath);

    public static native int wltGetBMP(byte[] wltdata, byte[] licdata);

    /*
     * this is used to load the 'wltdecode' library on application
     */
    static {
        try {
            System.loadLibrary("wltdecode");
            LOAD_SO_FLAG = true;
        } catch (Throwable ex) {
            LOAD_SO_FLAG = false;
            Log.e(TAG, ex.toString());
        }

    }

    public static boolean GetLoadSoState() {
        return LOAD_SO_FLAG;
    }
}
