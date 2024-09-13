package com.taytelar.entity.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "admin_data")
@Getter
@Setter
public class AdminEntity {

    @Id
    @Column(name = "admin_id", nullable = false)
    private String adminId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;
}