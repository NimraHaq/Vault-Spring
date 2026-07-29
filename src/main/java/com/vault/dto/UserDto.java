package com.vault.dto;

import com.vault.utils.Constants;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

//This class could have been a record

//lombok annotations, import lombok.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Integer id;


    @NotBlank(message = "Username is required.")
    @Size(min=4, max = 20)
    private String username;

    @Size(min=8, max = 12)
    private String password;

    private String role;

    //jakarta.validation.constraints
    @NotBlank(message = "First Name is required.")
    @Size(min=1)
    private String firstName;
    private String lastName;

    @Email(message = "Please enter a valid email address.")
    private String email;

    @NotNull(message = "Please select a gender.")
    private String gender;


    @Pattern(regexp="^[a-zA-Z0-9]{5}$", message = "Only five characters/digits allowed.")
    private String postalCode;


    private String address;

    private String phoneNumber;

    //instead of using primitive int, i will now use Integer class
    //if the user leaves this field blank, the system would not now what to do with null value
    //as int do not support null. but Integer do support null
    //making this field not null
    @NotNull(message = "Required Field.")
    @Min(value=18, message = "Age must be greater than or equal to 18")
    @Max(value=140, message = "Invalid age provided.")
    private Integer age;

    @NotBlank(message = "CNIC is required.")
    @Size(min=8, max = 45)
    private String cnic;

    @Builder.Default
    private String isActive = Constants.OPTION_YES;

    CustomerDto customerDto;

    private LocalDateTime createdOn;
    private LocalDateTime lastUpdatedOn;
}
