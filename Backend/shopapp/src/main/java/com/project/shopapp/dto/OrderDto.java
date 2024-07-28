package com.project.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {

    @Min(value = 1, message = "User's ID must be > 0")
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number can't empty!")
    @Size(min = 5, message = "Phone number must be at least 5 characters")
    private String phoneNumber;

    @NotBlank(message = "Address must be required!")
    private String address;

    private String note;

    @JsonProperty("total_money")
    @Min(value = 1, message = "Total money >= 0")
    private Float totalMoney;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("cart_items")
    private List<CartItemDto> cartItems;
}
