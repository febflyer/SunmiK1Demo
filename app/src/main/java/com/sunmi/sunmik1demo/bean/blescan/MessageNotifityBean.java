package com.sunmi.sunmik1demo.bean.blescan;


import java.util.Arrays;

/**
 * Created by thinkpad on 2018/1/10.
 */

public class MessageNotifityBean  {

     public static final byte[] APP_TYPE_CALL="CALL".getBytes();
     public static final byte[] APP_TYPE_MSG="MSG".getBytes();

    public  int messageId;
    public  byte[] messageType;
    public  String messageContent;
    public  String mSeatNumber;

    private MessageNotifityBean(int messageId, byte[] messageType, String seatNumber,String messageContent) {
        this.messageId = messageId;
        this.messageType = messageType;
        this.messageContent = messageContent;
        this.mSeatNumber = seatNumber;
    }


    @Override
    public String toString() {
        return "MessageNotifityBean{" +
                "messageId=" + messageId +
                ", messageType=" + Arrays.toString(messageType) +
                ", messageContent='" + messageContent + '\'' +
                '}';
    }

    public static class Builder{
        private   int messageId;
        private  byte[] messageType;
        private  String messageContent;
        private  String mSeatNumber;

        public Builder setMessageId(int messageId) {
            this.messageId = messageId;
            return this;
        }


        public Builder setMessageType(byte[] messageType) {
            this.messageType = messageType;
            return this;
        }


        public Builder setMessageContent(String messageContent) {
            this.messageContent = messageContent;
            return this;
        }

        public Builder setSeatNumber(String mSeatNumber) {
            this.mSeatNumber = mSeatNumber;
            return  this;
        }

        public MessageNotifityBean build(){
             return  new MessageNotifityBean(messageId,messageType,mSeatNumber,messageContent);
       }




    }


}
