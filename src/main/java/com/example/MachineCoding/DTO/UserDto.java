package com.example.MachineCoding.DTO;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String userName;
    private String gender;
    private String phoneNumber;
}
