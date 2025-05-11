package com.example.demo_fbfmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CartItemDisplay implements Parcelable {
    private Long id;
    private String foodName;
    private String foodImageUrl;
    private String size;
    private Double price;
    private Double discountPercentage;
    private Integer quantity;
    private boolean isSelected;

    // Constructor cho Parcelable
    protected CartItemDisplay(Parcel in) {
        id = in.readLong();
        foodName = in.readString();
        foodImageUrl = in.readString();
        size = in.readString();
        price = in.readDouble();
        discountPercentage = in.readDouble();
        quantity = in.readInt();
        isSelected = in.readByte() != 0; // Đọc boolean từ byte
    }

    // Creator để tái tạo đối tượng từ Parcel
    public static final Creator<CartItemDisplay> CREATOR = new Creator<CartItemDisplay>() {
        @Override
        public CartItemDisplay createFromParcel(Parcel in) {
            return new CartItemDisplay(in);
        }

        @Override
        public CartItemDisplay[] newArray(int size) {
            return new CartItemDisplay[size];
        }
    };

    @Override
    public int describeContents() {
        return 0; // Không sử dụng file descriptor, trả về 0
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(foodName);
        dest.writeString(foodImageUrl);
        dest.writeString(size);
        dest.writeDouble(price);
        dest.writeDouble(discountPercentage);
        dest.writeInt(quantity);
        dest.writeByte((byte) (isSelected ? 1 : 0)); // Ghi boolean dưới dạng byte
    }
}