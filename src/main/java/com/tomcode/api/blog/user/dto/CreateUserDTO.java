package com.tomcode.api.blog.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class CreateUserDTO {
    @Email
    @NotNull
    private String email;

    @NotNull
    @Length(min = 8)
    private String password;

    @NotNull
    private String username;
}
