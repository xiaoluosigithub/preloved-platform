package com.example.secondhand.controller;

import com.example.secondhand.common.Result;
import com.example.secondhand.model.entity.Comment;
import com.example.secondhand.service.CommentService;
import com.example.secondhand.service.FavoriteService;
import com.example.secondhand.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductInteractController {

  @Autowired private FavoriteService favoriteService;
  @Autowired private LikeService likeService;
  @Autowired private CommentService commentService;

  @PostMapping("/{id}/favorite")
  public Result<?> toggleFavorite(@PathVariable("id") Long productId, HttpServletRequest req){
    Long uid = (Long) req.getAttribute("userId");
    if(uid == null) return Result.fail("unauthenticated");
    boolean now = favoriteService.toggleFavorite(uid, productId);
    int count = favoriteService.countByProduct(productId);
    Map<String,Object> data = new HashMap<>();
    data.put("favorited", now);
    data.put("count", count);
    return Result.ok(data);
  }

  @PostMapping("/{id}/like")
  public Result<?> toggleLike(@PathVariable("id") Long productId, HttpServletRequest req){
    Long uid = (Long) req.getAttribute("userId");
    if(uid == null) return Result.fail("unauthenticated");
    boolean now = likeService.toggleLike(uid, productId);
    int count = likeService.countByProduct(productId);
    Map<String,Object> data = new HashMap<>();
    data.put("liked", now);
    data.put("count", count);
    return Result.ok(data);
  }

  @GetMapping("/{id}/comments")
  public Result<?> comments(@PathVariable("id") Long productId,
                            @RequestParam(defaultValue="1") int page,
                            @RequestParam(defaultValue="10") int size){
    List<Map<String,Object>> items = commentService.listComments(productId, page, size);
    int total = commentService.countComments(productId);
    Map<String,Object> data = new HashMap<>();
    data.put("items", items);
    data.put("total", total);
    data.put("page", page);
    data.put("size", size);
    return Result.ok(data);
  }

  @PostMapping("/{id}/comments")
  public Result<?> postComment(@PathVariable("id") Long productId, @RequestBody Map<String,String> body, HttpServletRequest req){
    Long uid = (Long) req.getAttribute("userId");
    if(uid == null) return Result.fail("unauthenticated");
    String content = body.get("content");
    if(content == null || content.trim().length() == 0) return Result.fail("empty content");
    Comment c = new Comment();
    c.setProductId(productId);
    c.setUserId(uid);
    c.setContent(content.trim());
    commentService.addComment(c);
    return Result.ok("comment added");
  }
}