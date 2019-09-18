// IMifareCard.aidl
package com.sunmi.mifarecard;

import java.lang.String;

// Declare any non-default types here with import statements
// 需要要申明权限：<uses-permission android:name="android.sunmi.permission.MIFARECARD_ACCESS" />

interface IMifareCard {

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------机器操作函数--------------------------------------

    /**
     * 功能：	一般查询，K720状态信息，返回3字节的状态信息，对应协议中“RF”这条指令
     * 参数：
     *     [out]stateInfo	存储K720状态信息，返回3个字节，详情请参K720照通讯协议
     * 返回值：	正确=0，错误=非0
     */
    int query(out byte[] stateInfo);

    /**
     * 功能：	高级查询，K720状态信息，返回4字节的状态信息，对应协议中“AP”这条指令
     * 参数：
     *     		[out]stateInfo	存储D1801状态信息，返回4个字节，详情请参K720照通讯协议
     * 返回值：	正确=0，错误=非0
     */
    int sensorQuery(out byte[] stateInfo);

    /**
     * 功能：	发送K720的操作命令
     * 参数：
     *     	[in]cmd		    存储命令字符串
     *     	[in]cmdLen		命令字符串口的长度
     * 返回值：	正确=0，错误=非0
     *
     * 注: 此函数可以执行命令如下：
     * 	sendCmd(“DC”, 2 )	发卡（到取卡位置）
     * 	sendCmd(“CP”, 2 )	收卡
     * 	sendCmd(“RS”, 2 )	复位
     * 	sendCmd(“BE”, 2 )	允许蜂鸣（卡少，卡空，出错蜂鸣器会报警）
     * 	sendCmd(“BD”, 2 )	停止蜂鸣器工作
     * 	sendCmd(“CS0”, 3)	设置机器通讯为波特率1200bps
     * 	sendCmd(“CS1”, 3)	设置机器通讯为波特率2400bps
     * 	sendCmd(“CS2”, 3)	设置机器通讯为波特率4800bps
     * 	sendCmd(“CS3”, 3)	设置机器通讯为波特率9600bps
     * 	sendCmd(“CS4”, 3)	设置机器通讯为波特率19200bps
     * 	sendCmd(“CS5”, 3)	设置机器通讯为波特率38400bps
     * 	sendCmd(“FC6”, 3)	发卡到传感器2
     * 	sendCmd(“FC7”, 3)	发卡到读卡位置
     *  sendCmd(“FC8”, 3)	前端进卡//这条指令，请确认好版本，再执行与否，因为有些版本是不支持前端进卡。
     * 	sendCmd(“FC0”, 3)	发卡到取卡位置
     * 	sendCmd(“FC4”, 3)	发卡到卡口外
     * 	sendCmd(”LPX”, 3)	设置闪灯频率,其中X=1-14表示1秒闪烁X次；X=15-28,表示(X-13)秒闪烁1次
     *
     * 	建议使用标准的9600的波特率通讯，否则速率太低，可能影响命令发送和接受(请谨慎修改)
     */
    int sendCmd(in byte[] cmd, in int cmdLen);

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------RF610接口函数------------------------------------

    //=========================射频卡 S50 卡操作函数===================================

    /**
    *
    * 功能：	S50卡-寻卡
    * 参数：
    * 返回值： 正确=0，错误=非 0
    */
    int detectS50Card();

    /**
    * 功能：	S50 读取序列号
    * 参数：
    *  [out]cardID           存储卡片序号
    * 返回值： 正确=0，错误=非0
    */
    int getS50CardID(out byte[] cardID);

    /**
     * 功能：	S50 卡检验密码
     * 参数：
     *      [in]sectorAddr        扇区的地址(0x00-0x0F)
     *      [in]keyType           密码类型 0x30=KEYA,0x31=KEYB
     *      [in]key               存储要验证的 6 字节密码
     * 返回值： 正确=0，错误=非 0
    */
    int loadS50SecKey(in byte sectorAddr, in byte keyType, in byte[] key);

    /**
     * 功能：	S50 卡读数据
     * 参数：
     * [in]sectorAddr          扇区的地址(0x00-0x0F)
     * [in] blockAddr           块的地址(0x00-0x03)
     * [out] blockData          存储要读取 16 字节数据
     * 返回值： 正确=0，错误=非 0
     */
    int readS50Block(in byte sectorAddr, in byte blockAddr, out byte[] blockData);

    /**
     * 功能：	S50 卡写数据
     * 参数：
     * [in]sectorAddr          扇区的地址(0x00-0x0F)
     * [in] blockAddr          块的地址(0x00-0x03)
     * [in] blockData          存储要写入 16 字节数据
     * 返回值： 正确=0，错误=非 0
     */
    int writeS50Block(in byte sectorAddr, in byte blockAddr, in byte[] blockData);

    /**
    * 功能：	S50 卡值初始化
    * 参数：
    * [in]sectorAddr         扇区的地址(0x00-0x0F)
    * [in] blockAddr         块的地址(0x00-0x03)
    * [in] data              存储要写入 4 字节数据
    * 返回值： 正确=0，错误=非 0
    */
    int initS50Value(in byte sectorAddr, in byte blockAddr, in byte[] data);

    /**
     * 功能：	S50 卡增值操作
     * 参数：
     * [in]sectorAddr          扇区的地址(0x00-0x0F)
     * [in] blockAddr          块的地址(0x00-0x03)
     * [in] data              存储要写入 4 字节增值数据
     * 返回值： 正确=0，错误=非 0
     */
    int incrementS50(in byte sectorAddr, in byte blockAddr, in byte[] data);

    /**
    * 功能：	S50 卡减值
    * 参数：
    * [in]sectorAddr         扇区的地址(0x00-0x0F)
    * [in]blockAddr         块的地址(0x00-0x03)
    * [in] data              存储要写入 4 字节减值数据
    * 返回值： 正确=0，错误=非 0
    */
    int decrementS50(in byte sectorAddr, in byte blockAddr, in byte[] data);

    /**
     * 功能：	S50 卡停机
     * 参数：
     * 返回值： 正确=0，错误=非 0
     */
    int haltS50();


    //=========================射频卡 S70 卡操作函数===================================

    /**
     * 功能：
     * S70 卡寻卡
     * 参数：
     * 返回值： 正确=0，错误=非 0
     */
    int detectS70Card();



    /**
     * 功能：	S70 卡读取 ID 号
     * 参数：
     * [out]cardID          存 储读取的 卡片 ID 号
     * 返回值： 正确=0，错误=非 0
     */
    int getS70CardID(out byte[] cardID);

    /**
     * 功能：	S70 卡检验密码
     * 参数：
     *
     * [in]sectorAddr          扇区地址(0x00-0x27)(前 32 个扇区每个扇区 4 个数据块后 8 个扇区每个扇 区 16 个数据块)
     * [in]keyType             密码类型 0x30=KEYA, 0x31=KEYB
     * [in]key                 存储要验证的 6 字节密码
     * 返回值： 正确=0，错误=非 0
     */
    int loadS70SecKey(in byte sectorAddr, in byte keyType, in byte[] key);

    /**
     * 功能：	S70 卡读数据
     * 参数：
     * [in]sectorAddr	扇区地址(0x00-0x27)(前 32 个扇区每个扇区 4 个数据块后 8 个扇区每个扇区16 个数据块)
     * [in]blockAddr	块地址前 32 个扇区块地址(0x00-0x03)后 8 个扇区块地址(0x00-0x0F)
      *[out]blockData	存储读取到的 16 字节数据
     * 返回值： 正确=0，错误=非 0
     */
    int readS70Block(in byte sectorAddr, in byte blockAddr, out byte[] blockData);

    /**
     *
     * 功能：	S70 卡写数据
     * 参数：
     * [in]sectorAddr          扇区地址(0x00-0x27)(前 32 个扇区每个扇区 4 个数据块后 8 个扇区每个扇区 16 个数据块)
     * [in]blockAddr           块地址前 32 个扇区块地址(0x00-0x03)后 8 个扇区块地址(0x00-0x0F)
     * [in]blockData           存储要写入的 16 字节数据
     * 返回值： 正确=0，错误=非 0
     */
    int writeS70Block(in byte sectorAddr, in byte blockAddr, in byte[] blockData);

    /**
     * 功能：	S70 卡初始值
     * 参数：
     * [in]sectorAddr          扇区地址(0x00-0x27)(前 32 个扇区每个扇区 4 个数据块后 8 个扇区每个扇区 16 个数据块)
     * [in]blockAddr           块地址前 32 个扇区块地址(0x00-0x03)后 8 个扇区块地址(0x00-0x0F)
     * [in]data                存储要写入的 4 字节数据
     * 返回值： 正确=0，错误=非 0
     */
    int initS70Value(in byte sectorAddr, in byte blockAddr, in byte[] data);

    /**
     * 功能：	S70 卡增值
     * 参数：
     * [in]sectorAddr          扇区地址(0x00-0x27)(前 32 个扇区每个扇区 4 个数据块后 8 个扇区每个扇区 16 个数据块)
     * [in]blockAddr           块地址前 32 个扇区块地址(0x00-0x03)后 8 个扇区块地址(0x00-0x0F)
     * [in]data                存储要写入的 4 字节增值数据
     * 返回值： 正确=0，错误=非 0
     */
    int incrementS70(in byte sectorAddr, in byte blockAddr, in byte[] data);

    /**
     * 功能：	S70 卡减值
     * 参数：
     * [in]sectorAddr          扇区地址(0x00-0x27)(前 32 个扇区每个扇区 4 个数据块后 8 个扇区每个扇区16 个数据块)
     * [in]blockAddr           块地址前 32 个扇区块地址(0x00-0x03)后 8 个扇区块地址(0x00-0x0F)
     * [in]data                存储要写入的 4 字节减值数据
     * 返回值： 正确=0，错误=非 0
     */
    int decrementS70(in byte sectorAddr, in byte blockAddr, in byte[] data);

    /**
     * 功能：	S70 卡停机
     * 参数：
     * 返回值： 正确=0，错误=非 0
     */
    int haltS70();

    //=========================射频卡 UL 卡操作函数===================================

    /**
     * 功能：	UL 卡寻卡
     * 参数：
     * 返回值： 正确=0，错误=非 0
     */
    int detectULCard();

    /**
     *
     * 功能：	UL 卡获取序列号
     * 参数：
     * [out]cardID           存储 7 字节序列号
     * 返回值： 正确=0，错误=非 0
     */
    int getULCardID(out byte[] cardID);

    /**
     * 功能：	UL 卡读扇区
     * 参数：
     * [in] sectorAddr	扇区地址(0x00-0x0F)
     * [out]blockData	存储 16 字节扇区数据
     * 返回值： 正确=0，错误=非 0
     */
    int readULBlock(in byte sectorAddr, out byte[] blockData);

    /**
     * 功能：	UL 卡写扇区
     * 参数：
     * [in] sectorAddr         扇区地址(0x00-0x0F)
     * [in]blockData          存储 16 字节扇区数据
     * 返回值： 正确=0，错误=非 0
     */
    int writeULBlock(in byte sectorAddr, in byte[] blockData);

    /**
     * 功能：	UL 卡停机
     * 参数：
     * [in]macAddr           机器地址，有限取值(0-15)
     * [out]recrodInfo        存储该条命令的通讯记录
     * 返回值： 正确=0，错误=非 0
     */
    int haltUL();




    //============================CPU 卡片操作函数===================================

    /**
     * 功能：	非接触式CPU卡激活
     * 参数：
     * [out]szATR[16]          返回16字节数据
     * 返回值： 正确=0，错误=非 0
     */
    int powerOnCPUCard(out byte[] szATR);

    /**
     *  功能: 	CPU 卡命令传送
     *  参数:
     *  [in]SCH	         连接标识 0x00
     *  [in]dataLen        传送命令包的长度
     *  [in]APDUData[]    传送的命令包
     *  [out]RCH           链接标志。=0x30无链接，=0x31有链接
     *  [out]exData[]       接收到的数据包
     *  [out]exDataLen     接收到的数据包长。
     *  返回值： 正确=0，错误=非 0
     */
    int CPUAPDU(in byte SCH, in int dataLen, in byte[] APDUData, out byte[] RCH, out byte[] exData, out int[] exDataLen);



    //----------------------------------------------------------------------------------------------
    //----------------------------------------动态库open/close版本函数---------------------------------

    /**
     * 功能：获取函数返回信息
     * 参数：
     * [in] errorCode    函数 返回值
     * [in] languageFlag 语言 0:中文 1:English
     * 返回值:	正确=0，错误=非0
     */
    String getErrorMsg(in int errorCode,in int languageFlag);

    /**
     * 功能：获取固件版本
     * 参数：
     * [out]version     固件版本
     * [out]recrodInfo     存储该条命令的通讯记录
     * 返回值:	正确=0，错误=非0
     */
    int getVersion(out byte[] version);

    /**
     * 功能:	读取D1801动态库版本信息
     * 参数: [out]strVerion		存动态库版本信息，读取成功会存储版本信息，如 “K720_Android_DLL_V1.00”
     * 返回值:	正确=0，错误=非0
     */
    int getDllVersion(out String[] strVersion);

    /**
     * 功能:	打开串口，默认的波特率“9600, n, 8, 1”
     * 参数:	[in]Port			要打开的串口，如："/dev/ttys4"
     * 返回值:	正确返回0；错误=非0
     *
     * 默认服务启动时，默认打开【"/dev/ttys4",9600】
     */
    int commOpen(in String port);

    /**
     * 功能:	以相应的波特率打开串口
     * 参数:	[in]Port			要打开的串口，如："/dev/ttys4"
     * 		[in]_data			波特率选项，有效值如下：
     * 						1200
     * 						2400
     * 						4800
     * 						9600
     * 						19200
     * 						38400
     * 返回值:	正确0；错误=非0
     *
     * 默认服务启动时，默认打开【"/dev/ttys4",9600】
     */
    int commOpenWithBaud(in String port, in int baudrate);

    /**
     * 功能：	关闭当前打开的串口（默认服务停止关闭）
     * 参数：
     * 返回值：	正确=0，错误=非0
     */
    int commClose();
}
