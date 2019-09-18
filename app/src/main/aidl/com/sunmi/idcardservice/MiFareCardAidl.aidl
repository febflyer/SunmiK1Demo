package com.sunmi.idcardservice;

interface MiFareCardAidl {

    /**
     * 非接触 CPU 卡函数 --- 激活非接触式卡
     *
     * 函数功能：读写器在传递的时间内查寻卡是否进入感应区，并激活进入感应区的卡片。
     *
     * @param     cardtype 卡类型		0AH  A类卡； 0BH  B类卡; 00H 未获取到
     * @param     snrlen 	字节   卡UID长度
     * @param     snr 	4字节   卡UID
     * @param     atrlen  1字节   ATR长度
     * @param     atr  ATR应答数据
     * @return    <>0 错误
     *             =0 正确
     * 举例：
     * 		byte []cardtype=new byte[8];
     * 		byte []snrlen = new byte[8];
     * 		byte []snr =new byte[10];
     * 		byte []cardinfo=new byte[200];
     * 		byte []infolen =new byte[10];
     * 		openCupCard(delaytime,cardtype,snrlen,snr,infolen,cardinfo);
     *
     * 	注： 由于调用该函数时，不一定有卡在感应区，很有可能需要较长的时间才能等到卡进感应区，故设定较长的时间给读写器，
     * 	完全由读写器在这段时间等待对卡激活，如果超时了便返回“激活失败”。此命令的delaytime参数就是为了传递上述时间参数给读写器。
     * 	如果delaytime参数为0，在无卡进感应区时读写器不用等待直接返回“激活失败”；如果delaytime参数为0xffff时,一直寻卡，
     * 	直到卡进入感应区；如果delaytime参数为其它值时，读写器可在delaytime时间内一直寻卡，直到超时了读	写器才返回“激活失败”，
     * 	此时主机端也是采用delaytime作为超时退出时间。如果有卡在感应区但激活失败，	那么读写器不用继续寻卡就直接返回“激活失败”。
     */
    int openCupCard(out byte[] cardtype,out byte[] snrlen,out byte[] snr,out byte[] atrlen,out byte[] atr);
    /**
     * 非接触式卡片命令交互。
     * @param      cmdlen      发送数据的字节长度
     * @param      cmd         要发送的数据
     * @param   resplen     返回的数据长度
     * @param    resp         nRespLen，返回的指令应答信息
     * @return    <>0 错误
     *             =0 正确
     * 举例:       int st;
     * 		byte []send_hex={0x01,0x02,0x03,0x04};
     * 		byte []resp_hex=new byte[512];
     * 		int []resplen =new int[10];
     * 		st=MainActivity.ExchangePro(send_hex,4,resp_hex,resplen);
     */
     int exchangePro(in byte[] cmd,in int cmdlen,out byte[] resp,out int[] resplen);
    /**
     * 设置非接触式卡片为halt状态mt8rfhalt
     *
     * 函数功能：设置非接触式卡片为halt状态。
     *
     * @param delaytime 等待卡进入感应区时间（单位：毫秒），高位字节在前
     * 			0:无需等待，无卡直接返回
     * 			0xffff:一直等待
     * @return    <>0 错误
     *             =0 正确
     * 举例：
     *  int delaytime = 0;
     *  st = rfhalt();
     *
     * 注： 完成对卡的Halt操作或者Deselect操作后,要求用户将卡离开射频操作区域，否则将一直循环判断。如果delaytime参数为0时，
     * 则不用等待，将直接返回halt操作结果；若delayTime为0xffff时，将无限等待， 直至卡离开感应区；若为其它值时，将在规定时间
     * 判断卡是否还在感应区直至定时时间到或者卡离开感应区。
     */
    int rfhalt();
    /**
     * 获取非接触式CPU卡卡片状态
     *
     * 函数功能： 获取非接触式CPU卡卡片状态
     *
     * @param cardState 非接触式CPU卡状态
     *                  cardState[0] = 0 无卡
     *                  cardState[0] = 1 一张卡
     *                  cardState[0] = 2 多张卡
     * @return    <>0 错误
     *             =0 正确
     *
     * 举例:
     *  byte Status[] = new byte[5];
     * 	st = getCPUCardState(Status);
     */
    int getCPUCardState(out byte[] cardState);
    /**
     * 激活非接触式存储卡
     *
     * 函数功能：读写器在传递的时间内查寻卡是否进入感应区，并激活进入感应区的非接触式存储卡。
     *
     * @param cardtype [0]: 0AH  Type A 卡;  0BH  TypeB卡
     * @param cardID		4字节	卡片UID
     * @return   <>0 错误
     *            =0 正确
     *
     * 举例：
     *  byte[] snr=new byte[20];
     * 	byte[] cardtype = new byte[8];
     * 	st = rfCard(cardtype,snr);
     *
     * 注： 由于调用该函数时，不一定有卡在感应区，很有可能需要较长的时间才能等到卡进感应区，故设定较长的时间给读写器，
     * 完全由读写器在这段时间等待对卡激活，如果超时了便返回“激活失败”。此函数的delaytime参数就是为了传递上述时间参数给
     * 读写器。如果delaytime参数为0，在无卡进感应区时读写器不用等待直接返回“激活失败”；如果delaytime参数为0xffff时,
     * 一直寻卡，直到卡进入感应区；如果delaytime参数为其它值时，读写器可在delaytime时间内一直寻卡，直到超时了读	写器
     * 才返回“激活失败”，此时主机端也是采用delaytime作为超时退出时间。如果有卡在感应区但激活失败，	那么读写器不用继续寻
     * 卡就直接返回“激活失败”。
     */
    int rfCard(out byte[] cardtype,out byte[] cardID);

    /**
     * 非接触式存储卡认证扇区
     *
     * 函数功能：对非接触式存储卡的某一个扇区进行认证。
     *
     * @param mode   认证模式 0 KEYA 模式 1 KEYB 模式
     * @param nsecno 扇区号
     * @param key    要传入的密码，6 字节。
     * @return  <>0 错误
     *           =0 正确
     * 举例：
     *  byte[] key = new byte[10];
     * 	key[0] = (byte)0xFF;
     * 	key[1] = (byte)0xFF;
     * 	key[2] = (byte)0xFF;
     * 	key[3] = (byte)0xFF;
     * 	key[4] = (byte)0xFF;
     * 	key[5] = (byte)0xFF;
     *  st = rfAuthEntication(0,4,key);
     */
    int rfAuthEntication(in int mode,in int nsecno,in byte[] key);
    /**
     * 非接触式存储卡读数据
     * 函数功能：获取非接触存储卡指定块地址的数据
     *
     * @param nblock 块地址
     * @param readdata 读出数据
     * @return  <>0 错误
     *           =0 正确
     * 举例：
     * byte[] rdata=new byte[32];
     * st = rfRead(4,rdata);
     */
    int rfRead(in int nblock,out byte[] readdata);
    /**
     * 获取非接触存储卡指定块地址的数据
     *
     * @param nblock 块地址
     * @param writedata 写进的数据
     * @return   <>0 错误
     *            =0 正确
     * 举例：
     *  byte[] key = new byte[16];
     *  Key[0] = 0x00 .........
     *  st = rfWrite(4,wdata);
     */
    int rfWrite(in int nblock,in byte[] writedata);
    /**
     * 获取非接触存储卡指定块地址的数值
     *
     * @param nblock		块地址
     * @param readvalue 	读出的值
     * @return   <>0 错误
     *            =0 正确
     * 举例：
     * int []Ivalue=new int[50];
     * st = rfReadVal(4,Ivalue);
     */
    int rfReadVal(in int nblock,out int[] readvalue);
    /**
     * 非接触式存储卡写值块
     *
     * 函数功能：设置非接触存储卡指定块地址的数值
     *
     * @param nblock 块地址
     * @param writevalue 传入的值
     * @return    <>0 错误
     *             =0 正确
     *  举例:
     *  st = rfInitVal(4,100);
     */
    int rfInitVal(in int nblock,in int writevalue);
    /**
     * 非接触式存储卡加值
     * 函数功能：对非接触存储卡指定块地址的数据进行加值操作
     *
     * @param nblock 块地址
     * @param incvalue 传入的值
     * @return   <>0 错误
     *            =0 正确
     * 举例：
     * 	st = rfIncrement(4,20);
     */
    int rfIncrement(in int nblock,in int incvalue);
    /**
     * 非接触式存储卡减值mt8rfdecrement
     * 函数功能：对非接触存储卡指定块地址的数据进行减值操作
     *
     * @param nblock 块地址
     * @param decvalue 传入的值
     * @return   <>0 错误
     *            =0 正确
     * 举例：
     *  st = rfDecrement(4,20);
     */
    int rfDecrement(in int nblock,in int decvalue);
    /**
     * 读取金融IC卡卡号跟姓名 readNAN
     * 函数功能：读取金融IC卡卡号及姓名。
     *
     * @param nCardType	卡片类型  	0表示接触式CPU卡、0xFF表示非接触式CPU卡，其他的不认可。（目前仅支持非接IC卡）
     * @param Cardno	金融IC卡卡号
     * @param CardName	金融IC卡姓名
     * @param lpErrMsg	错误信息
     * @return    	<> 0 错误
     *            	== 0 正确
     * 举例:
     * byte []szCardNo=new byte[512];
     * byte []szName=new byte[512];
     * byte []szErrinfo=new byte[1024];
     * int nCardType = 0x01;
     * st = MT3Y.readNAN(nCardType, szCardNo, szName, szErrinfo);
     */
     int readNAN(in int nCardType,out byte[] Cardno,out byte[] CardName,out byte[] lpErrMsg);
    /**
     * 读社保卡信息(仅串口设备支持)
     * @param info 社保 卡信息，输出格式如：卡号|身份证号|姓名|性别|民族|出生日期
     * @param Err  出错信息
     * @return    <0 错误
     *            =0 正确
     *
     */
    int readSBInfo(out byte[] info,out byte[] err);
    /**
     * 读EMID卡号
     *
     * @param datalen  EMID卡号长度
     * @param data EMID卡号
     * @return	<0 错误
     *          =0 正确
     */
     int getEMID(out byte[] datalen,out byte[] data);
}
