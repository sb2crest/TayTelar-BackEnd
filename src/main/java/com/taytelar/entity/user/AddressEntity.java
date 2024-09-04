package com.taytelar.entity.user;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "address_data")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "building_name")
    private String buildingName;

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "city_name",nullable = false)
    private String cityName;

    @Column(name = "state_name",nullable = false)
    private String stateName;

    @Column(name = "country_name",nullable = false)
    private String countryName;

    @Column(name = "pin_code",nullable = false)
    private String pinCode;

    @Column(name = "type_of_address")
    private String typeOfAddress;

    @Column(name = "land_ark")
    private String landMark;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

}
