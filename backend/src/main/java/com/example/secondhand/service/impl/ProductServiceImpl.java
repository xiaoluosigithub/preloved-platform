package com.example.secondhand.service.impl;

import com.example.secondhand.mapper.ProductMapper;
import com.example.secondhand.model.entity.Product;
import com.example.secondhand.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {
  @Autowired
  private ProductMapper productMapper;

  @Override
  public Product findById(Long id) { return productMapper.findById(id); }

  @Override
  public int create(Product p) { return productMapper.insert(p); }

  @Override
  public int update(Product p) { return productMapper.update(p); }

  @Override
  public int delete(Long id) { return productMapper.delete(id); }

  @Override
  public Map<String,Object> pageList(Integer categoryId, String keyword, int page, int size) {
    if(page < 1) page = 1;
    int offset = (page - 1) * size;
    List<Product> list = productMapper.findByPage(categoryId, keyword, offset, size);
    int total = productMapper.countByFilter(categoryId, keyword);
    Map<String,Object> ret = new HashMap<>();
    ret.put("items", list);
    ret.put("total", total);
    ret.put("page", page);
    ret.put("size", size);
    return ret;
  }

  @Override
  public Map<String,Object> pageListBySeller(Long sellerId, int page, int size) {
    if(page < 1) page = 1;
    int offset = (page - 1) * size;
    List<Product> list = productMapper.findBySeller(sellerId, offset, size);
    int total = productMapper.countBySeller(sellerId);
    Map<String,Object> ret = new HashMap<>();
    ret.put("items", list);
    ret.put("total", total);
    ret.put("page", page);
    ret.put("size", size);
    return ret;
  }
}