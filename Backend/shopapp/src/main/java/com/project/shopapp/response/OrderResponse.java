package com.project.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.entity.Order;
import com.project.shopapp.entity.OrderDetail;
import com.project.shopapp.entity.OrderStatus;
import com.project.shopapp.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse extends BaseResponse {

    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    @JsonProperty("note")
    private String note;

    @JsonProperty("order_date")
    private LocalDate orderDate;

    @JsonProperty("status")
    private OrderStatus status;

    @JsonProperty("total_money")
    private Float totalMoney;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("tracking_number")
    private String trackingNumber;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("order_details")
    private List<OrderDetail> orderDetails;

    public static OrderResponse mapFromOrder(Order order) {
        return OrderResponse
                .builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .fullName(order.getFullName())
                .phoneNumber(order.getPhoneNumber())
                .email(order.getEmail())
                .address(order.getAddress())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalMoney(order.getTotalMoney())
                .shippingMethod(order.getShippingMethod())
                .shippingAddress(order.getShippingAddress())
                .shippingDate(order.getShippingDate())
                .paymentMethod(order.getPaymentMethod())
                .orderDetails(order.getOrderDetails())
                .build();
    }

}
