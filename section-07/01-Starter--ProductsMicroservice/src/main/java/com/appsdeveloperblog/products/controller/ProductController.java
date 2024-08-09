package com.appsdeveloperblog.products.controller;


import com.appsdeveloperblog.products.model.Product;
import com.appsdeveloperblog.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<String> createProduct(@RequestBody Product product) {

        String productId = productService.createProduct(product);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Product created successfully With ID: " + productId);
    }
}
