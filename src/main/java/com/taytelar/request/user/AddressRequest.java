package com.taytelar.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequest {

    private Long addressId;

    @NotBlank(message = "user id can't be blank")
    private String userId;

    @NotBlank(message = "First name is mandatory")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Building name is mandatory")
    private String buildingName;

    @NotBlank(message = "Street name is mandatory")
    private String streetName;

    @NotBlank(message = "City name is mandatory")
    private String cityName;

    @NotBlank(message = "State name is mandatory")
    private String stateName;

    @NotBlank(message = "Country name is mandatory")
    private String countryName;

    @NotBlank(message = "Pin code is mandatory")
    @Size(max = 6,message = "The provided pin code must be valid")
    private String pinCode;

    private String typeOfAddress;

    private String landMark;

}
