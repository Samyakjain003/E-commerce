package com.samyakj820.productservice.service;

import com.samyakj820.productservice.dto.ProductRequest;
import com.samyakj820.productservice.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    void createProduct(ProductRequest productRequest);

    List<ProductResponse> getAllProducts();
}
