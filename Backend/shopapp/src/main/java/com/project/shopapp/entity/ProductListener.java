package com.project.shopapp.entity;

import com.project.shopapp.service.ProductRedisService;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


@Configurable
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class ProductListener {
    @Autowired
    private final ProductRedisService productRedisService;
    private static final Logger logger = LoggerFactory.getLogger(ProductListener.class);
    @PrePersist
    public void prePersist(Product product) {
        logger.info("prePersist");
    }

    @PostPersist //save = persis
    public void postPersist(Product product) {
        // Update Redis cache
        logger.info("postPersist");
        productRedisService.clear();
    }

    @PreUpdate
    public void preUpdate(Product product) {
        //ApplicationEventPublisher.instance().publishEvent(event);
        logger.info("preUpdate");
    }

    @PostUpdate
    public void postUpdate(Product product) {
        // Update Redis cache
        logger.info("postUpdate");
        productRedisService.clear();
    }

    @PreRemove
    public void preRemove(Product product) {
        //ApplicationEventPublisher.instance().publishEvent(event);
        logger.info("preRemove");
    }

    @PostRemove
    public void postRemove(Product product) {
        // Update Redis cache
        logger.info("postRemove");
        productRedisService.clear();
    }
}
