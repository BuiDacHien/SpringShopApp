package com.project.shopapp.service.Impl;

import com.project.shopapp.dto.OrderDetailDto;
import com.project.shopapp.entity.Order;
import com.project.shopapp.entity.OrderDetail;
import com.project.shopapp.entity.Product;
import com.project.shopapp.exception.ResourceNotFoundException;
import com.project.shopapp.repository.OrderDetailRepository;
import com.project.shopapp.repository.OrderRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    @Override
    @Transactional
    public OrderDetail createOrderDetail(OrderDetailDto orderDetailDto) {
        Order existingOrder = orderRepository.findById(orderDetailDto.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderDetailDto.getOrderId()));
        Product product = productRepository.findById(orderDetailDto.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + orderDetailDto.getProductId()));
        OrderDetail orderDetail = OrderDetail.builder()
                .order(existingOrder)
                .product(product)
                .price(orderDetailDto.getPrice())
                .numberOfProducts(orderDetailDto.getNumberOfProducts())
                .totalMoney(orderDetailDto.getTotalMoney())
                .color(orderDetailDto.getColor())
                .build();

        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetailById(Long id) {
        return orderDetailRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order Detail not found with id: " + id));
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Override
    @Transactional
    public OrderDetail updateOrderDetail(OrderDetailDto orderDetailDto, Long id) {
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order Detail not found with id: " + id));

        Order existingOrder = orderRepository.findById(orderDetailDto.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderDetailDto.getOrderId()));
        Product product = productRepository.findById(orderDetailDto.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + orderDetailDto.getProductId()));

        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(product);
        existingOrderDetail.setPrice(orderDetailDto.getPrice());
        existingOrderDetail.setColor(orderDetailDto.getColor());
        existingOrderDetail.setTotalMoney(orderDetailDto.getTotalMoney());
        existingOrderDetail.setNumberOfProducts(orderDetailDto.getNumberOfProducts());

        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    @Transactional
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }
}
