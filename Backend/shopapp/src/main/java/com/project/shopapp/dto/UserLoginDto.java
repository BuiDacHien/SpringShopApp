package com.project.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginDto {
    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number OR Email can't blank")
    private String phoneNumber;

    @NotBlank(message = "Password can't blank")
    private String password;

    @Min(value = 1, message = "You must enter role's Id")
    @JsonProperty("role_id")
    private Long roleId;
}
