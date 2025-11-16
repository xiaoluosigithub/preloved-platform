package com.example.secondhand.controller.admin;

import com.example.secondhand.common.Result;
import com.example.secondhand.mapper.ProductMapper;
import com.example.secondhand.mapper.OrderMapper;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.model.entity.Product;
import com.example.secondhand.model.entity.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

  @Resource
  private ProductMapper productMapper;
  @Resource
  private UserMapper userMapper;
  @Resource
  private OrderMapper orderMapper;

  private String checkAdmin(HttpServletRequest request) {
    Object uid = request.getAttribute("userId");
    if (uid == null) return "unauthenticated";
    User u = userMapper.findById(Long.valueOf(uid.toString()));
    if (u == null || u.getRole() == null || !"ADMIN".equalsIgnoreCase(u.getRole())) return "forbidden";
    return null;
  }

  @GetMapping("/pending")
  public Result<java.util.Map<String, Object>> listPending(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size,
                            HttpServletRequest request) {
    String err = checkAdmin(request);
    if (err != null) return Result.fail(err);
    int offset = (page - 1) * size;
    List<Product> list = productMapper.findByStatus("PENDING", offset, size);
    int total = productMapper.countByStatus("PENDING");
    Map<String, Object> data = new HashMap<>();
    data.put("list", list);
    data.put("total", total);
    return Result.ok(data);
  }

  @GetMapping
  public Result<java.util.Map<String, Object>> listAll(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) String keyword,
                            HttpServletRequest request) {
    String err = checkAdmin(request);
    if (err != null) return Result.fail(err);
    int offset = (page - 1) * size;
    List<Product> list = productMapper.findAllByPage(status, keyword, offset, size);
    int total = productMapper.countAllByFilter(status, keyword);
    Map<String, Object> data = new HashMap<>();
    data.put("list", list);
    data.put("total", total);
    return Result.ok(data);
  }

  @PostMapping("/{id}/approve")
  public Result<String> approve(@PathVariable Long id, HttpServletRequest request) {
    String err = checkAdmin(request);
    if (err != null) return Result.fail(err);
    int n = productMapper.updateStatusIfCurrent(id, "AVAILABLE", "PENDING");
    if (n == 0) return Result.fail("invalid status");
    return Result.ok();
  }

  @PostMapping("/{id}/reject")
  public Result<String> reject(@PathVariable Long id, HttpServletRequest request) {
    String err = checkAdmin(request);
    if (err != null) return Result.fail(err);
    int n = productMapper.delete(id);
    if (n == 0) return Result.fail("not found");
    return Result.ok();
  }

  @GetMapping("/{id}/orders")
  public Result<java.util.List<java.util.Map<String, Object>>> ordersByProduct(@PathVariable Long id, HttpServletRequest request) {
    String err = checkAdmin(request);
    if (err != null) return Result.fail(err);
    List<java.util.Map<String, Object>> orders = orderMapper.findByProductWithNames(id);
    return Result.ok(orders);
  }
}