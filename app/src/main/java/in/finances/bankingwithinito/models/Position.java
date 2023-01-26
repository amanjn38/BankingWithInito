package in.finances.bankingwithinito.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Position implements Parcelable {
    public Double latitude, longitude;

    public Position() {

    }

    public Position(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Position(Parcel in){
        latitude = in.readDouble();
        longitude = in.readDouble();
    }
    public static final Creator<Position> CREATOR = new Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        @Override
        public Position[] newArray(int size) {
            return new Position[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);

    }
}
