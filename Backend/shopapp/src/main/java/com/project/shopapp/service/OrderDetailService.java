package com.project.shopapp.service;

import com.project.shopapp.dto.OrderDetailDto;
import com.project.shopapp.dto.OrderDto;
import com.project.shopapp.entity.Order;
import com.project.shopapp.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDto orderDetailDto);
    OrderDetail getOrderDetailById(Long id);

    List<OrderDetail> findByOrderId(Long orderId);

    OrderDetail updateOrderDetail(OrderDetailDto orderDetailDto, Long id);

    void deleteOrderDetail(Long id);
}
