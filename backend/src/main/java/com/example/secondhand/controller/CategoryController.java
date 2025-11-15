package com.example.secondhand.controller;

import com.example.secondhand.common.Result;
import com.example.secondhand.mapper.CategoryMapper;
import com.example.secondhand.model.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
  @Autowired
  private CategoryMapper categoryMapper;

  @GetMapping
  public Result<?> list(){
    List<Category> list = categoryMapper.findAll();
    return Result.ok(list);
  }

  @PostMapping
  public Result<?> create(@RequestBody Category c){
    categoryMapper.insert(c);
    return Result.ok(c);
  }
}