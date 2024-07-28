package com.project.shopapp.service;

import com.project.shopapp.dto.OrderDto;
import com.project.shopapp.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderDto orderDto);
    Order getOrderById(Long id);

    List<Order> findByUserId(Long userId);

    Order updateOrder(OrderDto orderDto, Long id);

    void deleteOder(Long id);

    Page<Order> getOrdersByKeyword(String keyword, Pageable pageable);
}
