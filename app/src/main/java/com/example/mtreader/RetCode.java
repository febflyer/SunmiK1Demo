package com.example.mtreader;

public class RetCode {
	public static final byte	OKARRAY[]								= { 0, 0 };
	public static final byte	OP_OK									= (byte) 0;

	// cmd err
	public static final byte	ERR_CMDHIGH								= (byte) 1;
	public static final byte	ERR_CMDLOW								= (byte) 2;
	public static final byte	ERR_CMDPARAM							= (byte) 3;
	public static final byte	ERR_F6COMM_BUSY							= (byte) 4;
	public static final byte	ERR_ENCMODE_FAIL						= (byte) 5;
	public static final byte	ERR_SETKEY_FAIL							= (byte) 6;
	public static final byte	ERR_Exception							= (byte) 8;
	public static final byte	ERR_DISCKF6DEV							= (byte) 9;
	// mtreader
	public static final String	spiltTag								= "|";

	public static final byte	ERR_MT_OPEN								= (byte) 10;				// 设备打开错误
	public static final byte	ERR_MT_LOAD_SO							= (byte) 11;				// 加载动态库错误
	public static final byte	ERR_MT_TIMEOUT							= (byte) 12;				// 获取指纹超时
	public static final byte	ERR_MT_POWER_OFF						= (byte) 13;				// 没有对应的执行指令
	public static final byte	ERR_DEVICEBUSY							= (byte) 14;
	public static final byte	ERR_AnalysisCmd							= (byte) 15;

	// wlfinger
	public static final byte	ERR_WEL_OPEN							= (byte) 20;				// 指纹仪设备打开错误
	public static final byte	ERR_WEL_LOAD_SO							= (byte) 21;				// 指纹仪设备打开错误
	public static final byte	ERR_WEL_TIMEOUT							= (byte) 22;				// 获取指纹超时
	public static final byte	ERR_WEL_POWER_OFF						= (byte) 23;				//
	public static final int		ERR_WEL_CJIMG_BY_CHECKFINGER_TIMTOUT	= -111;					//
	// szkb
	public static final byte	ERR_SZKB_OPEN							= (byte) 30;
	public static final byte	ERR_SZKB_LOAD_SO						= (byte) 31;				// 没有加载动态库
	public static final byte	ERR_SZKB_TIMEOUT						= (byte) 32;				// 键盘操作超时
	public static final byte	ERR_SZKB_POWER_OFF						= (byte) 33;				//
	public static final byte	ERR_SZKB_NODATA							= (byte) 34;				//
	// qr
	public static final byte	ERR_QR_OPEN								= (byte) 40;
	public static final byte	ERR_QR_LOAD_SO							= (byte) 41;				// 没有加载动态库
	public static final byte	ERR_QR_TIMEOUT							= (byte) 42;				// 键盘操作超时
	public static final byte	ERR_QR_POWER_OFF						= (byte) 43;				//
	public static final byte	ERR_QR_TRIGGER							= (byte) 44;				//
	// POWER
	public static final byte	ERR_POWER_OPEN							= (byte) 50;
	public static final byte	ERR_POWER_LOAD_SO						= (byte) 51;				// 没有加载动态库
	public static final byte	ERR_POWER_TIMEOUT						= (byte) 52;				// 键盘操作超时
	public static final byte	ERR_POWER_POWER_OFF						= (byte) 53;				//

	// 屏幕加密
	public static final byte	ERR_ENCSIGN_PRMEKY_LEN					= (byte) 60;				// 主密钥长度有误
	public static final byte	ERR_ENCSIGN_WORKKEY_LEN					= (byte) 61;				// 工作密钥长度有误
	public static final byte	ERR_ENCSIGN_SET_PRMKEY					= (byte) 62;				// 设置主密钥失败
	public static final byte	ERR_ENCSIGN_SET_WORKKEY					= (byte) 63;				// 设置工作密钥失败
	public static final byte	ERR_ENCSIGN_GEN_SM2PAIR					= (byte) 64;				// 生成SM2密钥对失败
	public static final byte	ERR_ENCSIGN_GET_SM2PAIR					= (byte) 65;				// 获取SM2公钥失败
	public static final byte	ERR_ENCSIGN_ENCSM2FUNC					= (byte) 66;				// SM2加密失败
	public static final byte	ERR_ENCSIGN_SETSM2FUNC					= (byte) 67;				// SM2加密失败
	public static final byte	ERR_ENCSIGN_TIMEOUT						= (byte) 68;				// 获取签名数据超时

	// 通道加密
	public static final byte	ERR_ENCCHIP_OPENDEV						= (byte) 70;				// 打开设备出错
	public static final byte	ERR_ENCCHIP_LOAD_SO						= (byte) 71;				// 没有加载动态库
	public static final byte	ERR_ENCCHIP								= (byte) 72;				// 工作密钥长度有误

	public static final byte	ERR_POWER_OFF							= (byte) 80;
	public static final byte	ERR_NOT_FOUND_INSTANCE					= (byte) 81;

	public static final byte	ERR_NOT_FOUND_USBDEVICE					= (byte) 90;
	public static final byte	ERR_USB_NOPERMISSION					= (byte) 91;
	public static final byte	ERR_USB_CANNOT_CONNTECT					= (byte) 92;
	//
	public static final int		ERR_ICREADER							= -1;
	public static final int		ERR_ICCARD								= -2;
	public static final int		ERR_BUILDARQC							= -3;
	public static final int		ERR_ARPC								= -4;
	public static final int		ERR_CARD_APDU							= -7;
	public static final int		ERR_WRITE_TIMEOUT						= -8;
	public static final int		ERR_INTERRUPT							= -9;
	public static final int		ERR_READ								= -10;
	public static final int		ERR_WRITESCR							= -11;
	public static final int		ERR_WRITE								= -12;

	public static final int		ERR_CARDTYPE							= -15;

	public static final int		ERR_SOCK								= -0x11;					// 17
	public static final int		ERR_TIMEOUT								= -0x13;					// 19

	public static final int		ERR_GENERAL								= -36;
	public static final int		ERR_DATAFORMAT							= -37;

	public static final int		ERR_PINLEN								= -46;
	public static final int		ERR_VERIFYPIN							= -47;
	public static final int		ERR_MODIFYPIN							= -48;
	public static final int		ERR_AUTH								= -50;
	public static final int		ERR_PINLOCK								= -49;
	public static final int		ERR_RELOAD								= -51;
	public static final int		ERR_RELOAD_UNSAFE						= -52;
	public static final int		ERR_RELOAD_UNIF							= -53;
	public static final int		ERR_RELOAD_DATA							= -54;
	public static final int		ERR_RELOAD_UNSUP						= -55;
	public static final int		ERR_RELOAD_PAR							= -56;
	public static final int		ERR_RELOAD_UNFIND						= -57;
	public static final int		ERR_RELOAD_APPLOCK						= -58;
	public static final int		ERR_POWERON								= -59;
	public static final int		ERR_MAGNETIC_DATA						= -60;

	public static final int		ERR_IDCARDREAD							= -70;
	public static final int		ERR_DATAINDEX							= -71;
	public static final int		ERR_LCD_LNUM							= -72;
	public static final int		ERR_OBJNULL								= -73;
	public static final int		Init_Code								= -74;
	public static final int		ERR_FINDNOTHING							= -75;
	public static final int		ERR_NODATA								= -76;

	public static final int		ERR_KEYEMPTY							= -80;
	public static final int		ERR_INPUTEMPTY							= -81;
	public static final int		ERR_KEYLEN								= -82;
	public static final int		ERR_INPUTLEN							= -83;

	public static final int		ERR_STX									= -0x61;					// -97
	public static final int		ERR_ETX									= -0x62;
	public static final int		ERR_IDCARD								= -0x63;					// -99
	public static final int		ERR_LEN									= -0x64;					// -100
	public static final int		ERR_CRC									= -0x65;
	public static final int		ERR_DISCONN								= -0x66;

	public static final int		ERR_PARSEIDCARD							= -200;
	public static final int		ERR_CHALSEND							= -201;
	public static final int		ERR_SETBAUD								= -202;
	public static final int		ERR_GETIMGNUM							= -203;
	public static final int		ERR_AMOUNT								= -204;
	public static final int		ERR_SETAMOUNT							= -205;
	public static final int		ERR_SETTRANTIME							= -206;
	public static final int		ERR_GETF55								= -207;
	public static final int		ERR_STBD								= -208;
	public static final int		ERR_UNSUPPORTED							= -0x1001;					// 4097
	public static final int		ERR_PARSEFINGER							= -209;
	public static final int		ERR_OVERFLOW							= -210;
	public static final int		ERR_SAVEIMG								= -211;
	public static final int		ERR_GETDATA								= -212;
	public static final int		ERR_ASCTOHEX							= -213;

	public static final String	Key_Value								= "Value";
	public static final String	Key_ErrCode								= "ErrCode";
	public static final String	Key_ErrMsg								= "ErrMsg";
	public static final String	Key_ContactCard							= "ContactCard";
	public static final String	Key_ContactlessCard						= "ContactlessCard";
	public static final String	Key_IDCard								= "IDCard";
	public static final String	Key_ATR									= "ATR";
	public static final String	Key_ContactCardType						= "ContactCardType";
	public static final String	Key_ContactlessCardType					= "ContactlessCardType";
	public static final String	Key_ContactlessCardTypeAB				= "ContactlessCardTypeA/B";
	public static final String	Key_Snr									= "Snr";
	public static final String	Key_Track1								= "Track1";
	public static final String	Key_Track2								= "Track2";
	public static final String	Key_Track3								= "Track3";
	public static final String	Key_Mag									= "Mag";
	public static final String	Key_Name								= "Name";
	public static final String	Key_Sex									= "Sex";
	public static final String	Key_Nation								= "Nation";
	public static final String	Key_Birth								= "Birth";
	public static final String	Key_Address								= "Address";
	public static final String	Key_IDNUM								= "IDNUM";
	public static final String	Key_GrantDepart							= "GrantDepart";
	public static final String	Key_DateStart							= "DateStart";
	public static final String	Key_DateEnd								= "DateEnd";
	public static final String	Key_Photo								= "Photo";
	public static final String	Key_Finger								= "Finger";
	public static final String	Key_AddInfo								= "AddInfo";
	public static final String	Key_CardNO								= "CardNO";

	public static String GetErrMsg(int errCode) {
		// TODO Auto-generated method stub
		switch (errCode) {
		case ERR_SOCK:
			return "通讯通道异常";
		case ERR_TIMEOUT:
			return "通讯超时";
		case ERR_OBJNULL:
			return "对象为空";
		case ERR_UNSUPPORTED:
			return "不支持的类型";
		case ERR_IDCARD:
			return "此卡可能为身份证卡";
		case ERR_FINDNOTHING:
			return "未寻到卡";
		case ERR_DATAFORMAT:
			return "数据格式有误";
		case ERR_STX:
			return "数据包头有误";
		case ERR_ETX:
			return "数据包尾有误";
		case ERR_LEN:
			return "数据长度有误";
		case ERR_CRC:
			return "CRC校验有误";
		case ERR_CHALSEND:
			return "下发透传指令失败";
		case ERR_NODATA:
			return "NO data found";
		case ERR_AMOUNT:
			return "Input amout is invalid";
		case ERR_SETAMOUNT:
			return "Set authed amount fail";
		case ERR_SETTRANTIME:
			return "Set trans time fail";
		case ERR_GETF55:
			return "Get field55 fail";
		case ERR_DISCONN:
			return "Device is disconnected";
		case ERR_STBD:
			return "Set baudrate fail";
		case ERR_PARSEFINGER:
			return "Data transfer Ok, but finger data parse error";
		case ERR_OVERFLOW:
			return "Data over flow";
		case ERR_SAVEIMG:
			return "Save image fail";
		case ERR_ASCTOHEX:
			return "Asc convert to hex data fail";
		}
		return "";
	}
}
