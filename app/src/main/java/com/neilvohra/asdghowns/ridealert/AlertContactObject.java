package com.neilvohra.asdghowns.ridealert;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Contains all the information necessary to send contact a text message
 * when the user is nearby
 */
public class AlertContactObject implements Parcelable {
    private String contactName;
    private String contactNumber;
    private Address contactAddress;

    public AlertContactObject(String name, String number, Address address) {
        contactName = name;
        contactNumber = number;
        contactAddress = address;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public Address getContactAddress() {
        return contactAddress;
    }

    protected AlertContactObject(Parcel in) {
        contactName = in.readString();
        contactNumber = in.readString();
        contactAddress = (Address) in.readValue(Address.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contactName);
        dest.writeString(contactNumber);
        dest.writeValue(contactAddress);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AlertContactObject> CREATOR = new Parcelable.Creator<AlertContactObject>() {
        @Override
        public AlertContactObject createFromParcel(Parcel in) {
            return new AlertContactObject(in);
        }

        @Override
        public AlertContactObject[] newArray(int size) {
            return new AlertContactObject[size];
        }
    };
}