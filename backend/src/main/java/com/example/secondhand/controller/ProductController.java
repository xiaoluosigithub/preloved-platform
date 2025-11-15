package com.example.secondhand.controller;

import com.example.secondhand.common.Result;
import com.example.secondhand.model.entity.Product;
import com.example.secondhand.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  @Autowired
  private ProductService productService;

  @GetMapping
  public Result<?> list(@RequestParam(defaultValue="1") int page,
                        @RequestParam(defaultValue="10") int size,
                        @RequestParam(required=false) Integer categoryId,
                        @RequestParam(required=false) String keyword) {
    Map<String,Object> data = productService.pageList(categoryId, keyword, page, size);
    return Result.ok(data);
  }

  @GetMapping("/{id}")
  public Result<?> detail(@PathVariable Long id) {
    Product p = productService.findById(id);
    if (p == null) return Result.fail("not found");
    return Result.ok(p);
  }

  @PostMapping
  public Result<?> create(@RequestBody Product p, HttpServletRequest req) {
    Object uid = req.getAttribute("userId");
    if (uid == null) return Result.fail("unauthenticated");
    p.setSellerId((Long)uid);
    productService.create(p);
    return Result.ok("created");
  }

  @PutMapping("/{id}")
  public Result<?> update(@PathVariable Long id, @RequestBody Product p, HttpServletRequest req) {
    Object uid = req.getAttribute("userId");
    if (uid == null) return Result.fail("unauthenticated");
    p.setId(id);
    productService.update(p);
    return Result.ok("updated");
  }

  @DeleteMapping("/{id}")
  public Result<?> delete(@PathVariable Long id, HttpServletRequest req) {
    Object uid = req.getAttribute("userId");
    if (uid == null) return Result.fail("unauthenticated");
    productService.delete(id);
    return Result.ok("deleted");
  }
}