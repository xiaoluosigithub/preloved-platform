package com.example.secondhand.service;

import com.example.secondhand.model.entity.Product;
import java.util.List;
import java.util.Map;

public interface ProductService {
  Product findById(Long id);
  int create(Product p);
  int update(Product p);
  int delete(Long id);
  Map<String,Object> pageList(Integer categoryId, String keyword, int page, int size);
}