package com.example.secondhand.controller;

import com.example.secondhand.common.Result;
import com.example.secondhand.model.entity.Product;
import com.example.secondhand.service.ProductService;
import com.example.secondhand.mapper.FavoriteMapper;
import com.example.secondhand.mapper.ProductLikeMapper;
import com.example.secondhand.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductUserController {
  @Autowired
  private ProductService productService;
  @Autowired private FavoriteMapper favoriteMapper;
  @Autowired private ProductLikeMapper likeMapper;
  @Autowired private CommentMapper commentMapper;

  @GetMapping("/my")
  public Result<?> my(@RequestParam(defaultValue="1") int page,
                      @RequestParam(defaultValue="10") int size,
                      HttpServletRequest req) {
    Long uid = (Long) req.getAttribute("userId");
    if (uid == null) return Result.fail("unauthenticated");
    Map<String,Object> data = productService.pageListBySeller(uid, page, size);
    return Result.ok(data);
  }

  @PostMapping("/update")
  public Result<?> updatePost(@RequestBody Product p, HttpServletRequest req) {
    return doUpdate(p, req);
  }

  @PutMapping("/update")
  public Result<?> updatePut(@RequestBody Product p, HttpServletRequest req) {
    return doUpdate(p, req);
  }

  private Result<?> doUpdate(Product p, HttpServletRequest req) {
    Long uid = (Long) req.getAttribute("userId");
    if (uid == null) return Result.fail("unauthenticated");
    Product origin = productService.findById(p.getId());
    if (origin == null) return Result.fail("not found");
    if (!uid.equals(origin.getSellerId())) return Result.fail("forbidden");
    productService.update(p);
    return Result.ok("updated");
  }

  @DeleteMapping("/{id}")
  public Result<?> delete(@PathVariable Long id, HttpServletRequest req) {
    Long uid = (Long) req.getAttribute("userId");
    if (uid == null) return Result.fail("unauthenticated");
    Product origin = productService.findById(id);
    if (origin == null) return Result.fail("not found");
    if (!uid.equals(origin.getSellerId())) return Result.fail("forbidden");
    if ("PENDING".equals(origin.getStatus())) return Result.fail("cannot delete pending product");
    if ("SOLD".equals(origin.getStatus())) return Result.fail("cannot delete sold product");
    favoriteMapper.deleteByProduct(id);
    likeMapper.deleteByProduct(id);
    commentMapper.deleteByProduct(id);
    productService.delete(id);
    return Result.ok("deleted");
  }
}