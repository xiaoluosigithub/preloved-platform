package com.example.secondhand.controller.admin;

import com.example.secondhand.common.Result;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.model.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/admin")
public class AdminCommonController {

  @Resource
  private UserMapper userMapper;

  @GetMapping("/ping")
  public Result<String> ping(HttpServletRequest request) {
    String err = checkAdmin(request);
    if (err != null) return Result.fail(err);
    return Result.ok("ok");
  }

  private String checkAdmin(HttpServletRequest request) {
    Object uid = request.getAttribute("userId");
    if (uid == null) return "unauthenticated";
    User u = userMapper.findById(Long.valueOf(uid.toString()));
    if (u == null || u.getRole() == null || !"ADMIN".equalsIgnoreCase(u.getRole())) return "forbidden";
    return null;
  }
}