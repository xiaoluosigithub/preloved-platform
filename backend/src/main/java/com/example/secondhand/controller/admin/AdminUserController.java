package com.example.secondhand.controller.admin;

import com.example.secondhand.common.Result;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.model.entity.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

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
  public Result<java.util.Map<String, Object>> list(@RequestParam(defaultValue = "1") int page,
                     @RequestParam(defaultValue = "10") int size,
                     @RequestParam(required = false) String keyword,
                     HttpServletRequest request) {
    String err = checkAdmin(request);
    if (err != null) return Result.fail(err);
    int offset = (page - 1) * size;
    List<User> list = userMapper.listUsers(keyword, offset, size);
    int total = userMapper.countUsers(keyword);
    Map<String, Object> data = new HashMap<>();
    data.put("list", list);
    data.put("total", total);
    return Result.ok(data);
  }

  public static class RoleDTO { public String role; }

  @PatchMapping("/{id}/role")
  public Result<String> updateRole(@PathVariable Long id,
                           @RequestBody RoleDTO body,
                           HttpServletRequest request) {
    String err = checkAdmin(request);
    if (err != null) return Result.fail(err);
    if (body == null || body.role == null) return Result.fail("bad request");
    String role = body.role.toUpperCase();
    if (!"ADMIN".equals(role) && !"USER".equals(role)) return Result.fail("bad role");
    int n = userMapper.updateRole(id, role);
    if (n == 0) return Result.fail("not found");
    return Result.ok();
  }
}