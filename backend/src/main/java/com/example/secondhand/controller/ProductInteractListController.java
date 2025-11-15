package com.example.secondhand.controller;

import com.example.secondhand.common.Result;
import com.example.secondhand.mapper.FavoriteMapper;
import com.example.secondhand.mapper.ProductLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product-interact")
public class ProductInteractListController {
  @Autowired private FavoriteMapper favoriteMapper;
  @Autowired private ProductLikeMapper likeMapper;

  @GetMapping("/my-favorite")
  public Result<?> myFavorite(@RequestParam(defaultValue="1") int page,
                              @RequestParam(defaultValue="10") int size,
                              HttpServletRequest req) {
    Long uid = (Long) req.getAttribute("userId");
    if (uid == null) return Result.fail("unauthenticated");
    int offset = (Math.max(page,1) - 1) * size;
    List<Map<String,Object>> items = favoriteMapper.listProductsByUser(uid, offset, size);
    int total = favoriteMapper.countByUser(uid);
    Map<String,Object> data = new HashMap<>();
    data.put("items", items);
    data.put("total", total);
    data.put("page", page);
    data.put("size", size);
    return Result.ok(data);
  }

  @GetMapping("/my-like")
  public Result<?> myLike(@RequestParam(defaultValue="1") int page,
                          @RequestParam(defaultValue="10") int size,
                          HttpServletRequest req) {
    Long uid = (Long) req.getAttribute("userId");
    if (uid == null) return Result.fail("unauthenticated");
    int offset = (Math.max(page,1) - 1) * size;
    List<Map<String,Object>> items = likeMapper.listProductsByUser(uid, offset, size);
    int total = likeMapper.countByUser(uid);
    Map<String,Object> data = new HashMap<>();
    data.put("items", items);
    data.put("total", total);
    data.put("page", page);
    data.put("size", size);
    return Result.ok(data);
  }
}