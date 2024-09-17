package com.taytelar.response.user;

import com.taytelar.entity.user.UserEntity;
import lombok.Data;

@Data
public class AddressResponse {

    private Long addressId;

    private String firstName;

    private String lastName;

    private String buildingName;

    private String streetName;

    private String cityName;

    private String stateName;

    private String countryName;

    private String pinCode;

    private String typeOfAddress;

    private String landMark;

}
