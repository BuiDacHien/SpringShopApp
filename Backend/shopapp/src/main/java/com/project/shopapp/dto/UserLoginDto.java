package com.project.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginDto {
    @JsonProperty("phone_number_or_email")
    @NotBlank(message = "Phone number OR Email can't blank")
    private String phoneNumberOrEmail;

    @NotBlank(message = "Password can't blank")
    private String password;
}
