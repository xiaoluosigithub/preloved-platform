package com.example.secondhand.controller.admin;

import com.example.secondhand.common.Result;
import com.example.secondhand.mapper.CategoryMapper;
import com.example.secondhand.mapper.ProductMapper;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.model.entity.Category;
import com.example.secondhand.model.entity.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

  @Resource
  private CategoryMapper categoryMapper;
  @Resource
  private ProductMapper productMapper;
  @Resource
  private UserMapper userMapper;

  private String checkAdmin(HttpServletRequest request) {
    Object uid = request.getAttribute("userId");
    if (uid == null) return "unauthenticated";
    User u = userMapper.findById(Long.valueOf(uid.toString()));
    if (u == null || u.getRole() == null || !"ADMIN".equalsIgnoreCase(u.getRole())) return "forbidden";
    return null;
  }

  @GetMapping
  public Result<java.util.List<Category>> list(HttpServletRequest request) {
    String err = checkAdmin(request);
    if (err != null) return Result.fail(err);
    List<Category> all = categoryMapper.findAll();
    return Result.ok(all);
  }

  @PostMapping
  public Result<Category> add(@RequestBody Category c, HttpServletRequest request) {
    String err = checkAdmin(request);
    if (err != null) return Result.fail(err);
    if (c == null || c.getName() == null || c.getName().trim().isEmpty()) return Result.fail("bad request");
    if (categoryMapper.findByName(c.getName().trim()) != null) return Result.fail("duplicate");
    categoryMapper.insert(c);
    return Result.ok(c);
  }

  @DeleteMapping("/{id}")
  public Result<String> delete(@PathVariable Integer id, HttpServletRequest request) {
    String err = checkAdmin(request);
    if (err != null) return Result.fail(err);
    if (id == null) return Result.fail("bad request");
    int cnt = productMapper.countByCategory(id);
    if (cnt > 0) return Result.fail("category in use");
    int n = categoryMapper.delete(id);
    if (n == 0) return Result.fail("not found");
    return Result.ok();
  }
}