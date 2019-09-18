package com.example.mtreader;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;


/**
 * Created by mt739 on 2017-11-22.
 */

public class AnysizeIDCMsg {
	private static String		TAG			= "AnysizeIDCMsg";
	public final static byte[]	byLicData	= { (byte) 0x05, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x5B,(byte) 0x03, (byte) 0x33, (byte) 0x01, (byte) 0x5A, (byte) 0xB3, (byte) 0x1E, (byte) 0x00 };

	/* =-=-=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-= */
	/*
	 * 获取性别信息
	 */
	public static byte[] getsexinfo(byte bsex[]) {
		String StrSexInfo = "";
		if (bsex[0] == 0x30) {
			StrSexInfo = "未知";
		} else if (bsex[0] == 0x31) {
			StrSexInfo = "男";
		} else if (bsex[0] == 0x32) {
			StrSexInfo = "女";
		} else if (bsex[0] == 0x39) {
			StrSexInfo = "未说明";
		} else {
			StrSexInfo = " ";
		}
		return StrSexInfo.getBytes();
	}

	/*
	 * 获取民族信息
	 */
	public static byte[] getnation(byte bNationinfo[]) {
		String StrNation = "";
		int nNationNo = 0;
		nNationNo = (bNationinfo[0] - 0x30) * 10 + bNationinfo[2] - 0x30;
		switch (nNationNo) {
		case 1:
			StrNation = "汉";
			break;
		case 2:
			StrNation = "蒙古";
			break;
		case 3:
			StrNation = "回";
			break;
		case 4:
			StrNation = "藏";
			break;
		case 5:
			StrNation = "维吾尔";
			break;
		case 6:
			StrNation = "苗";
			break;
		case 7:
			StrNation = "彝";
			break;
		case 8:
			StrNation = "壮";
			break;
		case 9:
			StrNation = "布依";
			break;
		case 10:
			StrNation = "朝鲜";
			break;
		case 11:
			StrNation = "满";
			break;
		case 12:
			StrNation = "侗";
			break;
		case 13:
			StrNation = "瑶";
			break;
		case 14:
			StrNation = "白";
			break;
		case 15:
			StrNation = "土家";
			break;
		case 16:
			StrNation = "哈尼";
			break;
		case 17:
			StrNation = "哈萨克";
			break;
		case 18:
			StrNation = "傣";
			break;
		case 19:
			StrNation = "黎";
			break;
		case 20:
			StrNation = "傈僳";
			break;
		case 21:
			StrNation = "佤";
			break;
		case 22:
			StrNation = "畲";
			break;
		case 23:
			StrNation = "高山";
			break;
		case 24:
			StrNation = "拉祜";
			break;
		case 25:
			StrNation = "水";
			break;
		case 26:
			StrNation = "东乡";
			break;
		case 27:
			StrNation = "纳西";
			break;
		case 28:
			StrNation = "景颇";
			break;
		case 29:
			StrNation = "柯尔克孜";
			break;
		case 30:
			StrNation = "土";
			break;
		case 31:
			StrNation = "达斡尔";
			break;
		case 32:
			StrNation = "仫佬";
			break;
		case 33:
			StrNation = "羌";
			break;
		case 34:
			StrNation = "布朗";
			break;
		case 35:
			StrNation = "撒拉";
			break;
		case 36:
			StrNation = "毛南";
			break;
		case 37:
			StrNation = "仡佬";
			break;
		case 38:
			StrNation = "锡伯";
			break;
		case 39:
			StrNation = "阿昌";
			break;
		case 40:
			StrNation = "普米";
			break;
		case 41:
			StrNation = "塔吉克";
			break;
		case 42:
			StrNation = "怒";
			break;
		case 43:
			StrNation = "乌孜别克";
			break;
		case 44:
			StrNation = "俄罗斯";
			break;
		case 45:
			StrNation = "鄂温克";
			break;
		case 46:
			StrNation = "德昂";
			break;
		case 47:
			StrNation = "保安";
			break;
		case 48:
			StrNation = "裕固";
			break;
		case 49:
			StrNation = "京";
			break;
		case 50:
			StrNation = "塔塔尔";
			break;
		case 51:
			StrNation = "独龙";
			break;
		case 52:
			StrNation = "鄂伦春";
			break;
		case 53:
			StrNation = "赫哲";
			break;
		case 54:
			StrNation = "门巴";
			break;
		case 55:
			StrNation = "珞巴";
			break;
		case 56:
			StrNation = "基诺";
			break;
		case 57:
			StrNation = "其他";
			break;
		case 58:
			StrNation = "外国血统中国籍人士";
			break;
		default:
			StrNation = " ";
			break;
		}
		return StrNation.getBytes();
	}

	public static HashMap<String, byte[]> anysizeIDCMsg(int isReadFinger, int idcMsgLen, byte[] idcMsg) {
		HashMap<String, byte[]> idcMsg_HM = new HashMap<String, byte[]>();
		int wzLen = 0, zpLen = 0, fgLen = 0;

		byte[] sForeignName = new byte[120]; // 英文姓名
		byte[] sCode = new byte[6]; // 国籍代码
		// byte[] sCountry = new byte[64] ; //国籍
		byte[] sTypeID = new byte[2]; // 证件类型标识
		byte[] sIDVer = new byte[4]; // 证件版本号

		byte[] sName = new byte[30]; // 姓名
		byte[] sGender = new byte[2]; // 性别
		byte[] sNation = new byte[4]; // 民族
		byte[] sBirth = new byte[16]; // 出生日期
		byte[] sAddress = new byte[70]; // 住址
		byte[] sIDNumber = new byte[36]; // 公民身份证号
		byte[] sIssued = new byte[30]; // 签发机关
		byte[] sDateBegin = new byte[16]; // 有效期起始日期
		byte[] sDateEnd = new byte[16]; // 有效期截止日期
		byte[] sPassNo = new byte[18]; // 通行证号
		byte[] sIssueTime = new byte[4]; // 签发次数
		byte[] sPhotoData = new byte[1024]; // 照片文件内容
		byte[] sFingerData = new byte[1024]; // 指纹信息
		byte[] sReserved = new byte[36]; // 预留区

		byte SW1, SW2, SW3;
		SW1 = idcMsg[2];
		SW2 = idcMsg[3];
		SW3 = idcMsg[4];
		if (SW1 == 0 && SW2 == 0 && SW3 == (byte) 0x90) {
			idcMsg_HM.put("anysizeState", "SUCCESS".getBytes());
			int pos = 0;
			wzLen = idcMsg[5] * 256 + idcMsg[6];
			zpLen = idcMsg[7] * 256 + idcMsg[8];
			pos = 9;
			if (1 == isReadFinger) {
				fgLen = idcMsg[9] * 256 + idcMsg[10];
				pos = 11;
			}
			// 取证件类型
			System.arraycopy(idcMsg, pos + 248, sTypeID, 0, 2);
			idcMsg_HM.put("TypeID", sTypeID);
			String idType = "";
			try {
				idType = new String(sTypeID, "UTF-16LE");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (idType.compareTo("I") == 0) // 外国人
			{
				// 英文名字
				System.arraycopy(idcMsg, pos, sForeignName, 0, 120);
				pos += 120;
				idcMsg_HM.put("ForeignName", sForeignName);
				// 性别
				System.arraycopy(idcMsg, pos, sGender, 0, 2);
				pos += 2;
				idcMsg_HM.put("Sex", getsexinfo(sGender));
				// 永久居留证号码
				System.arraycopy(idcMsg, pos, sIDNumber, 0, 30);
				pos += 30;
				idcMsg_HM.put("IDNum", sIDNumber);
				// 国籍所在地区代码
				System.arraycopy(idcMsg, pos, sCode, 0, 6);
				pos += 6;
				idcMsg_HM.put("CountryCode", sCode);
				// 中文姓名
				System.arraycopy(idcMsg, pos, sName, 0, 30);
				pos += 30;
				idcMsg_HM.put("ChinaName", sName);
				// 有效期起始日期
				System.arraycopy(idcMsg, pos, sDateBegin, 0, 16);
				pos += 16;
				idcMsg_HM.put("DateStart", sDateBegin);
				// 有效期截止日期
				System.arraycopy(idcMsg, pos, sDateEnd, 0, 16);
				pos += 16;
				idcMsg_HM.put("DateEnd", sDateEnd);
				// 出生日期
				System.arraycopy(idcMsg, pos, sBirth, 0, 16);
				pos += 16;
				idcMsg_HM.put("Birth", sBirth);
				// 证件版本号
				System.arraycopy(idcMsg, pos, sIDVer, 0, 4);
				pos += 4;
				idcMsg_HM.put("IDVer", sIDVer);
				// 当次申请受理机关代码
				System.arraycopy(idcMsg, pos, sIssued, 0, 8);
				pos += 8;
				idcMsg_HM.put("Issued", sIssued);
				// 证件类型标识
				System.arraycopy(idcMsg, pos, sTypeID, 0, 2);
				pos += 2;
				idcMsg_HM.put("TypeID", sTypeID);
				// 预留
				System.arraycopy(idcMsg, pos, sReserved, 0, 6);
				pos += 6;
				idcMsg_HM.put("Reserved", sReserved);
				// 照片数据

				System.arraycopy(idcMsg, pos + wzLen, sPhotoData, 0, zpLen);
				idcMsg_HM.put("PhotoData", sPhotoData);

				if (fgLen > 0) {
					System.arraycopy(idcMsg, pos + wzLen + zpLen, sFingerData, 0, fgLen);
					idcMsg_HM.put("FingerData", sFingerData);
				} else {
					idcMsg_HM.put("FingerData", new byte[0]);
				}

			} else {
				System.arraycopy(idcMsg, pos, sName, 0, 30);
				pos += 30;
				idcMsg_HM.put("ChinaName", sName);

				System.arraycopy(idcMsg, pos, sGender, 0, 2);
				pos += 2;
				idcMsg_HM.put("Sex", getsexinfo(sGender));

				System.arraycopy(idcMsg, pos, sNation, 0, 4);
				pos += 4;
				idcMsg_HM.put("Nation", (idType.compareTo("J") == 0 ? "".getBytes() : getnation(sNation)));

				System.arraycopy(idcMsg, pos, sBirth, 0, 16);
				pos += 16;
				idcMsg_HM.put("Birth", sBirth);

				System.arraycopy(idcMsg, pos, sAddress, 0, 70);
				pos += 70;
				idcMsg_HM.put("Address", sAddress);

				System.arraycopy(idcMsg, pos, sIDNumber, 0, 36);
				pos += 36;
				idcMsg_HM.put("IDNum", sIDNumber);

				System.arraycopy(idcMsg, pos, sIssued, 0, 30);
				pos += 30;
				idcMsg_HM.put("Issued", sIssued);

				System.arraycopy(idcMsg, pos, sDateBegin, 0, 16);
				pos += 16;
				idcMsg_HM.put("DateStart", sDateBegin);

				System.arraycopy(idcMsg, pos, sDateEnd, 0, 16);
				pos += 16;
				idcMsg_HM.put("DateEnd", sDateEnd);

				if (idType.compareTo("J") == 0) // 港澳台
				{
					System.arraycopy(idcMsg, pos, sPassNo, 0, 18);
					pos += 18;
					idcMsg_HM.put("PassNumber", sPassNo);
					System.arraycopy(idcMsg, pos, sIssueTime, 0, 4);
					pos += 4;
					idcMsg_HM.put("IssueTime", sIssueTime);
				}

				System.arraycopy(idcMsg, pos + wzLen, sPhotoData, 0, zpLen);
				idcMsg_HM.put("PhotoData", sPhotoData);

				if (fgLen > 0) {
					System.arraycopy(idcMsg, pos + wzLen + zpLen, sFingerData, 0, fgLen);
					idcMsg_HM.put("FingerData", sFingerData);
				} else {
					idcMsg_HM.put("FingerData", new byte[0]);
				}
			}
		} else {
			idcMsg_HM.put("anysizeState", "FAIL".getBytes());
		}
		return idcMsg_HM;
	}

	// 中国人身份证
	public static byte[] CombinationWltData(byte[] name, byte[] sex, byte[] nation, byte[] birth, byte[] address,
			byte[] idnumber, byte[] department, byte[] datestart, byte[] dateend, byte[] reserved, byte[] photodata) {
		byte[] wltData = new byte[1384];
		byte headData[] = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, (byte) 0x69, (byte) 0x05, (byte) 0x08,(byte) 0x00, (byte) 0x00, (byte) 0x90, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00 };
		byte Reserved[] = new byte[36];
		String strReserved = "200020002000200020002000200020002000200020002000200020002000200020002000";
		ToolFun.asc_hex(strReserved.getBytes(), Reserved, 36);
		Arrays.fill(wltData, (byte) 0);
		System.arraycopy(headData, 0, wltData, 0, headData.length);

		int pos = headData.length;
		System.arraycopy(name, 0, wltData, pos, 30);
		pos += 30;
		System.arraycopy(sex, 0, wltData, pos, 2);
		pos += 2;
		System.arraycopy(nation, 0, wltData, pos, 4);
		pos += 4;
		System.arraycopy(birth, 0, wltData, pos, 16);
		pos += 16;
		System.arraycopy(address, 0, wltData, pos, 70);
		pos += 70;
		System.arraycopy(idnumber, 0, wltData, pos, 36);
		pos += 36;
		System.arraycopy(department, 0, wltData, pos, 30);
		pos += 30;
		System.arraycopy(datestart, 0, wltData, pos, 16);
		pos += 16;
		System.arraycopy(dateend, 0, wltData, pos, 16);
		pos += 16;
		System.arraycopy(Reserved, 0, wltData, pos, 36);
		pos += 36;
		System.arraycopy(photodata, 0, wltData, pos, 1024);

		return wltData;
	}

	// 外国人身份证
	public static byte[] CombinationWltData(byte[] sForeignName, byte[] sGende, byte[] sIDNumber, byte[] sCountry,
			byte[] sName, byte[] sDateBegin, byte[] sDateEnd, byte[] sBirth, byte[] sIDVe, byte[] sIssued,
			byte[] sTypeI, byte[] sReserved, byte[] sPhotoData) {
		byte wltData[] = new byte[1384];
		byte headData[] = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, (byte) 0x69, (byte) 0x05, (byte) 0x08,(byte) 0x00, (byte) 0x00, (byte) 0x90, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00 };
		Arrays.fill(wltData, (byte) 0);
		System.arraycopy(headData, 0, wltData, 0, headData.length);
		int pos = headData.length;
		System.arraycopy(sForeignName, 0, wltData, pos, 120);
		pos += 120;
		System.arraycopy(sGende, 0, wltData, pos, 2);
		pos += 2;
		System.arraycopy(sIDNumber, 0, wltData, pos, 30);
		pos += 30;
		System.arraycopy(sCountry, 0, wltData, pos, 6);
		pos += 6;
		System.arraycopy(sName, 0, wltData, pos, 30);
		pos += 30;
		System.arraycopy(sDateBegin, 0, wltData, pos, 16);
		pos += 16;
		System.arraycopy(sDateEnd, 0, wltData, pos, 16);
		pos += 16;
		System.arraycopy(sBirth, 0, wltData, pos, 16);
		pos += 16;
		System.arraycopy(sIDVe, 0, wltData, pos, 4);
		pos += 4;
		System.arraycopy(sIssued, 0, wltData, pos, 8);
		pos += 8;
		System.arraycopy(sTypeI, 0, wltData, pos, 2);
		pos += 2;
		System.arraycopy(sReserved, 0, wltData, pos, 6);
		pos += 6;
		System.arraycopy(sPhotoData, 0, wltData, pos, 1024);

		return wltData;
	}

	public static byte[] CombinationWltData(int[] idcMsgLen, byte[] idcMsg, int isReadFinger) {
		// TODO Auto-generated method stub
		byte wltData[] = new byte[1024];
		
		Arrays.fill(wltData, (byte) 0);
		
		int index = (1 == isReadFinger ? 11 : 9);
		System.arraycopy(idcMsg, index +  256, wltData, 0, 1024);
		return wltData;
	}
}