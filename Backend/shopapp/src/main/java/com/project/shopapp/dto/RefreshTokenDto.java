package com.project.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenDto {
    @NotBlank
    @JsonProperty("refresh_token")
    private String refreshToken;
}
