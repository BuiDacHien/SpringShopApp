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
    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number OR Email can't blank")
    private String phoneNumber;

    @NotBlank(message = "Password can't blank")
    private String password;
}
