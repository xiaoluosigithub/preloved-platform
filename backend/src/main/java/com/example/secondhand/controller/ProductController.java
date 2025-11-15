package com.example.secondhand.controller;

import com.example.secondhand.common.Result;
import com.example.secondhand.model.entity.Product;
import com.example.secondhand.service.FavoriteService;
import com.example.secondhand.service.LikeService;
import com.example.secondhand.service.CommentService;
import com.example.secondhand.service.ProductService;
import com.example.secondhand.mapper.FavoriteMapper;
import com.example.secondhand.mapper.ProductLikeMapper;
import com.example.secondhand.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  @Autowired
  private ProductService productService;
  @Autowired
  private FavoriteMapper favoriteMapper;
  @Autowired
  private ProductLikeMapper likeMapper;
  @Autowired
  private CommentMapper commentMapper;
  @Autowired
  private FavoriteService favoriteService;
  @Autowired
  private LikeService likeService;
  @Autowired
  private CommentService commentService;

  @GetMapping
  public Result<?> list(@RequestParam(defaultValue="1") int page,
                        @RequestParam(defaultValue="10") int size,
                        @RequestParam(required=false) Integer categoryId,
                        @RequestParam(required=false) String keyword) {
    Map<String,Object> data = productService.pageList(categoryId, keyword, page, size);
    return Result.ok(data);
  }

  @GetMapping("/{id}")
  public Result<?> detail(@PathVariable Long id, HttpServletRequest req) {
    Product p = productService.findById(id);
    if (p == null) return Result.fail("not found");
    int likeCount = likeService.countByProduct(id);
    int favoriteCount = favoriteService.countByProduct(id);
    int commentCount = commentService.countComments(id);
    Long uid = (Long) req.getAttribute("userId");
    boolean liked = false;
    boolean favorited = false;
    if (uid != null) {
      liked = likeService.exists(uid, id);
      favorited = favoriteService.exists(uid, id);
    }
    java.util.Map<String,Object> m = new java.util.HashMap<>();
    m.put("product", p);
    m.put("likeCount", likeCount);
    m.put("favoriteCount", favoriteCount);
    m.put("commentCount", commentCount);
    m.put("liked", liked);
    m.put("favorited", favorited);
    return Result.ok(m);
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
    Product origin = productService.findById(id);
    if (origin == null) return Result.fail("not found");
    if (!((Long)uid).equals(origin.getSellerId())) return Result.fail("forbidden");
    if ("SOLD".equals(origin.getStatus())) return Result.fail("cannot update sold product");
    productService.update(p);
    return Result.ok("updated");
  }

  @DeleteMapping("/{id}")
  public Result<?> delete(@PathVariable Long id, HttpServletRequest req) {
    Object uid = req.getAttribute("userId");
    if (uid == null) return Result.fail("unauthenticated");
    Product origin = productService.findById(id);
    if (origin == null) return Result.fail("not found");
    if (!((Long)uid).equals(origin.getSellerId())) return Result.fail("forbidden");
    if ("PENDING".equals(origin.getStatus())) return Result.fail("cannot delete pending product");
    if ("SOLD".equals(origin.getStatus())) return Result.fail("cannot delete sold product");
    favoriteMapper.deleteByProduct(id);
    likeMapper.deleteByProduct(id);
    commentMapper.deleteByProduct(id);
    productService.delete(id);
    return Result.ok("deleted");
  }
}