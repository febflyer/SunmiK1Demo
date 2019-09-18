package com.example.mtreader;

import java.util.HashMap;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class UsbDevPermission {
	
	private static String				TAG				= "UsbDevPermission";
	private static UsbManager			mUsbManager;
	private static UsbDevice			mUsbDevice;
	// private static UsbInterface mInterface;
	private static UsbDeviceConnection	mDeviceConnection;
	private static PendingIntent		mPermissionIntent;
	private static Context				mContext;

	private static String				usbDevicePath	= "";

	// 注1：UsbManager.ACTION_USB_DEVICE_ATTACHED对应的广播在USB每次插入时都能监听到，所以用这个就可以监听USB插入。
	// 注2：UsbManager.ACTION_USB_DEVICE_DETACHED用来监听USB拔出广播。
	
	public UsbDevPermission(Context context) {
		mContext = context;
		mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent( "com.hdos.usbdevice.UsbDeviceLib.USB_PERMISSION"), 0);
		// 注册USB设备权限管理广播
		IntentFilter filter = new IntentFilter("com.hdos.usbdevice.UsbDeviceLib.USB_PERMISSION");
		filter.addAction("com.android.example.USB_PERMISSION");
		filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
		filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
		mContext.registerReceiver(mUsbReceiver, filter);
	}
	
	//USB授权
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");
			switch (intent.getAction()) {
			//USB设备插入
			case "android.hardware.usb.action.USB_DEVICE_ATTACHED":
				if ((intent.getBooleanExtra("permission", false)) && (device != null)) {
					Log.d(TAG, "UsbDevice (VID:"+device.getVendorId()+",PID:"+device.getProductId() +") DEVICE_ATTACHED");
				}
				break;
			//USB设备拔出
			case "android.hardware.usb.action.USB_DEVICE_DETACHED":
				if ((intent.getBooleanExtra("permission", false)) && (device != null)) {
					Log.d(TAG, "UsbDevice (VID:"+device.getVendorId()+",PID:"+device.getProductId() +") DEVICE_DETACHED");
				}
				break;
			case "com.android.example.USB_PERMISSION":
			case "com.hdos.usbdevice.UsbDeviceLib.USB_PERMISSION":
				synchronized (this) {
					bRequestPer = false;	//add by mayflower on 19/7/9
					if ((intent.getBooleanExtra("permission", false)) && (device != null)) {
						Log.d(TAG, "permission request get" + device);
						onRequestPermissionListener.onRequestPermission(true);	//add by mayflower on 19/7/9
					} else {
						Log.d(TAG, "permission denied for device " + device);
						onRequestPermissionListener.onRequestPermission(false);	//add by mayflower on 19/7/9
					}
				}
				break;
			}
		}
	};

	//add by mayflower on 19/7/9,给申请权限结果的回调，申请完成后，根据申请结果执行后续步骤-----------
	public boolean bRequestPer = false;		//是否做了权限申请，这个标记是需要的，因为串口版不会做这个申请

	private OnRequestPermissionListener onRequestPermissionListener = null;

	public static interface OnRequestPermissionListener{
		public void onRequestPermission(boolean request);
	}

	public void setOnRequestPermissionListener(OnRequestPermissionListener requestPermissionListener){
		onRequestPermissionListener = requestPermissionListener;
	}
	//---------------------------------------------------------------------------------------------

	public int getUsbFileDescriptor(int VendorId,int ProductID) {
		
		int UsbFileDescriptor = 0;
		mUsbManager = ((UsbManager) mContext.getSystemService("usb"));
		HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
		if (deviceList.size() <= 0) {
			return -RetCode.ERR_NOT_FOUND_USBDEVICE;
		}
		if (!deviceList.isEmpty()) {
			for (UsbDevice device : deviceList.values()) {
				usbDevicePath = device.getDeviceName();
				if ((device.getVendorId() == VendorId) && (device.getProductId() == ProductID)) {
					mUsbDevice = device;
					// 判断下设备权限，如果没有权限，则请求权限
					if (!mUsbManager.hasPermission(mUsbDevice)) {
						bRequestPer = true;
						mUsbManager.requestPermission(mUsbDevice, mPermissionIntent);
					}
				}
			}
		}

		if (mUsbDevice != null)
		{
			UsbDeviceConnection conn = null;
			if (mUsbManager.hasPermission(mUsbDevice)) {
				Log.d(TAG, "has permission");
				conn = mUsbManager.openDevice(mUsbDevice);
			} else {
				Log.d(TAG, "no permission");
				return -RetCode.ERR_USB_NOPERMISSION;
			}
			if (conn == null) {
				return -RetCode.ERR_USB_CANNOT_CONNTECT;
			}

			mDeviceConnection = conn;
			UsbFileDescriptor = conn.getFileDescriptor();
			Log.d(TAG, " getFileDescriptor is "+ UsbFileDescriptor);

			return UsbFileDescriptor;
		}
		return -RetCode.ERR_NOT_FOUND_USBDEVICE;
	}

	public String getUsbDevPath() {
		return usbDevicePath;
	}

	public boolean isOpen() {
		if ((mUsbManager != null) && (mUsbDevice != null)
		/* && (mInterface != null) */
		&& (mDeviceConnection != null) && (mUsbManager.hasPermission(mUsbDevice))) {
			return true;
		}
		return false;
	}
	
	public void close(){
		if (mDeviceConnection != null)
			mDeviceConnection.close();
	}

}
