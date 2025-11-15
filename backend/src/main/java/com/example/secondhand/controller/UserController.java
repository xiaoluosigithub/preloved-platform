package com.example.secondhand.controller;

import com.example.secondhand.common.Result;
import com.example.secondhand.model.entity.User;
import com.example.secondhand.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
  @Autowired
  private UserService userService;

  @GetMapping("/me")
  public Result<?> me(HttpServletRequest req){
    Object o = req.getAttribute("userId");
    if (o == null) return Result.fail("unauthenticated");
    Long userId = (Long) o;
    User u = userService.findById(userId);
    if (u == null) return Result.fail("user not found");
    u.setPassword(null);
    return Result.ok(u);
  }
}