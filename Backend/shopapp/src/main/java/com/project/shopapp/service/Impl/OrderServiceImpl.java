package com.project.shopapp.service.Impl;

import com.project.shopapp.dto.OrderDto;
import com.project.shopapp.entity.*;
import com.project.shopapp.exception.ResourceNotFoundException;
import com.project.shopapp.exception.ShopAppAPIException;
import com.project.shopapp.repository.OrderRepository;
import com.project.shopapp.repository.UserRepository;
import com.project.shopapp.response.OrderResponse;
import com.project.shopapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public Order createOrder(OrderDto orderDto) {
        User user = userRepository.findById(orderDto.getUserId()).
                orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + orderDto.getUserId()));

        modelMapper.typeMap(OrderDto.class, Order.class).addMappings(mapper -> mapper.skip(Order::setId));


        Order order = new Order();
        modelMapper.map(orderDto, order);

        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);

        LocalDate shippingDate = orderDto.getShippingDate() == null ? LocalDate.now() : orderDto.getShippingDate();

        if (shippingDate.isBefore(LocalDate.now())) {
            throw new ShopAppAPIException(HttpStatus.BAD_REQUEST, "Shipping date must be after current Date");
        }

        order.setShippingDate(shippingDate);

        order.setActive(true);

        orderRepository.save(order);

        return order;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order updateOrder(OrderDto orderDto, Long id) {
        Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        User existingUser = userRepository.findById(orderDto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        modelMapper.typeMap(OrderDto.class, Order.class).addMappings(mapper -> mapper.skip(Order::setId));

        modelMapper.map(orderDto, existingOrder);
        existingOrder.setUser(existingUser);

        return orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOder(Long id) {
        Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (existingOrder != null ) {
            existingOrder.setActive(false);
            orderRepository.save(existingOrder);
        }
    }
}
