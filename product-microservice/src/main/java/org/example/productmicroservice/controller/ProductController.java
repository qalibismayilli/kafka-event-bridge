package org.example.productmicroservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.productmicroservice.dto.CreateProductDto;
import org.example.productmicroservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping()
    public ResponseEntity<String> createProduct(@RequestBody CreateProductDto product) {
        String productId = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Product created successfully:" + productId);
    }
}
