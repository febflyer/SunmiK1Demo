package com.sunmi.idcardservice;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bps .
 */
public class IDCardInfo implements Parcelable {

    private String name;
    private String gender;
    private String nation;
    private String birthDay;
    private String address;
    private String idCard;
    private String department;
    private String startDate;
    private String endDate;
    private String imageAddress;
    private FingerPrint firstFinger;
    private FingerPrint secondFinger;


    public IDCardInfo() {
    }

    public IDCardInfo(String name, String gender, String nation, String birthDay, String address,
                      String idCard, String department, String startDate, String endDate) {
        this.name = name;
        this.gender = gender;
        this.nation = nation;
        this.birthDay = birthDay;
        this.address = address;
        this.idCard = idCard;
        this.department = department;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    protected IDCardInfo(Parcel in) {
        name = in.readString();
        gender = in.readString();
        nation = in.readString();
        birthDay = in.readString();
        address = in.readString();
        idCard = in.readString();
        department = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        imageAddress = in.readString();
        firstFinger = in.readParcelable(FingerPrint.class.getClassLoader());
        secondFinger = in.readParcelable(FingerPrint.class.getClassLoader());
    }

    public static final Creator<IDCardInfo> CREATOR = new Creator<IDCardInfo>() {
        @Override
        public IDCardInfo createFromParcel(Parcel in) {
            return new IDCardInfo(in);
        }

        @Override
        public IDCardInfo[] newArray(int size) {
            return new IDCardInfo[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getNation() {
        return nation;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public String getAddress() {
        return address;
    }

    public String getIdCard() {
        return idCard;
    }

    public String getDepartment() {
        return department;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getImageAddress() {
        return imageAddress;
    }

    public FingerPrint getFirstFinger() {
        return firstFinger;
    }

    public FingerPrint getSecondFinger() {
        return secondFinger;
    }

    public IDCardInfo setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
        return this;
    }

    public IDCardInfo setFirstFinger(FingerPrint firstFinger) {
        this.firstFinger = firstFinger;
        return this;
    }

    public IDCardInfo setSecondFinger(FingerPrint secondFinger) {
        this.secondFinger = secondFinger;
        return this;
    }

    @Override
    public String toString() {
        return "IDCardInfo{" +
                "name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", nation='" + nation + '\'' +
                ", birthDay=" + birthDay +
                ", address='" + address + '\'' +
                ", idCard='" + idCard + '\'' +
                ", department='" + department + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", imageAddress='" + imageAddress + '\'' +
                ", firstFinger=" + firstFinger +
                ", secondFinger=" + secondFinger +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(gender);
        dest.writeString(nation);
        dest.writeString(birthDay);
        dest.writeString(address);
        dest.writeString(idCard);
        dest.writeString(department);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(imageAddress);
        dest.writeParcelable(firstFinger, flags);
        dest.writeParcelable(secondFinger, flags);
    }

    public void readFromParcel(Parcel in) {
        new IDCardInfo(in);
    }

    static class FingerPrint implements Parcelable {
        public String position;
        public byte quality;

        public FingerPrint() {
        }

        public FingerPrint(String position, byte quality) {
            this.position = position;
            this.quality = quality;
        }

        protected FingerPrint(Parcel in) {
            position = in.readString();
            quality = in.readByte();
        }

        public static final Creator<FingerPrint> CREATOR = new Creator<FingerPrint>() {
            @Override
            public FingerPrint createFromParcel(Parcel in) {
                return new FingerPrint(in);
            }

            @Override
            public FingerPrint[] newArray(int size) {
                return new FingerPrint[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(position);
            dest.writeByte(quality);
        }

        public void readFromParcel(Parcel in) {
            new FingerPrint(in);
        }

        @Override
        public String toString() {
            return "FingerPrint{" +
                    "position='" + position + '\'' +
                    ", quality='" + quality + '\'' +
                    '}';
        }
    }


}
