package com.project.shopapp.controller;

import com.project.shopapp.dto.OrderDetailDto;
import com.project.shopapp.dto.OrderDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base.path}/order_details")
public class OrderDetailController {

    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDto orderDetailDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream().map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }

            return ResponseEntity.ok("Insert new order details: " + orderDetailDto.toString());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getOrderDetailById(@Valid @PathVariable Long id) {
        try {
            return ResponseEntity.ok("Get order detail in system with id = " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<String> getOrderDetailsByOrderId(@Valid @PathVariable Long orderId) {
        try {
            return ResponseEntity.ok("Get order details with order id = " + orderId);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateOrderDetail(@PathVariable Long id, @Valid @RequestBody OrderDetailDto orderDetailDto) {
        try {
            return ResponseEntity.ok("Update order detail with id = " + id + " and new order detail is: " + orderDetailDto.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderDetail(@PathVariable Long id) {
        try {
            // Delete soft: update active field is false.
            return ResponseEntity.ok("Delete order detail by id = " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
