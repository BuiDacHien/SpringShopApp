package com.project.shopapp.service.Impl;

import com.project.shopapp.dto.ProductDto;
import com.project.shopapp.dto.ProductImageDto;
import com.project.shopapp.entity.Category;
import com.project.shopapp.entity.Product;
import com.project.shopapp.entity.ProductImage;
import com.project.shopapp.exception.InValidParamException;
import com.project.shopapp.exception.ResourceNotFoundException;
import com.project.shopapp.repository.CategoryRepository;
import com.project.shopapp.repository.ProductImageRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.response.ProductResponse;
import com.project.shopapp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    @Transactional
    public Product createProduct(ProductDto productDto) {
        Category categoryExist = categoryRepository.findById(productDto.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()));

        Product newProduct = Product.builder()
                .name(productDto.getName())
                .category(categoryExist)
                .price(productDto.getPrice())
                .thumbnail(productDto.getThumbnail())
                .description(productDto.getDescription())
                .build();

        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(Long id) {
//        return productRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        Optional<Product> optionalProduct = productRepository.getDetailProduct(id);
        if(optionalProduct.isPresent()) {
            return optionalProduct.get();
        }
        throw new ResourceNotFoundException("Product not found with id: " + id);
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
        Page<Product> products = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return products.map(ProductResponse::mapFromProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDto productDto) {

        Product productExist = getProductById(id);

        if (productExist != null) {
            Category categoryExist = categoryRepository.findById(productDto.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()));
            productExist.setCategory(categoryExist);
            productExist.setName(productDto.getName());
            productExist.setPrice(productDto.getPrice());
            productExist.setThumbnail(productDto.getThumbnail());
            productExist.setDescription(productDto.getDescription());
            return productRepository.save(productExist);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Optional<Product> productExist = productRepository.findById(id);
        productExist.ifPresent(productRepository::delete);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    @Transactional
    public ProductImage createProductImage(Long productId, ProductImageDto productImageDto) throws InValidParamException {
        Product productExist = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        ProductImage newProductImage = ProductImage.builder().product(productExist).imageUrl(productImageDto.getImageUrl()).
                build();

        int countOfImage = productImageRepository.findByProductId(productId).size();

        if (countOfImage >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InValidParamException("The number of photos for a product cannot exceed " + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT + "!");
        }

        return productImageRepository.save(newProductImage);
    }

    
}
