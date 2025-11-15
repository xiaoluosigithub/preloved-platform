package com.example.secondhand.controller;

import com.example.secondhand.common.JwtUtil;
import com.example.secondhand.common.Result;
import com.example.secondhand.model.entity.User;
import com.example.secondhand.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private UserService userService;

  private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  @PostMapping("/register")
  public Result<?> register(@RequestBody Map<String,String> body) {
    String username = body.get("username");
    String password = body.get("password");
    String nickname = body.getOrDefault("nickname", username);

    if (username == null || password == null) return Result.fail("username and password required");

    if (userService.findByUsername(username) != null) {
      return Result.fail("username already exists");
    }

    User u = new User();
    u.setUsername(username);
    u.setPassword(encoder.encode(password));
    u.setNickname(nickname);
    u.setRole("USER");
    userService.createUser(u);
    return Result.ok("registered");
  }

  @PostMapping("/login")
  public Result<?> login(@RequestBody Map<String,String> body) {
    String username = body.get("username");
    String password = body.get("password");
    if (username == null || password == null) return Result.fail("username and password required");

    User u = userService.findByUsername(username);
    if (u == null) return Result.fail("invalid username or password");

    if (!encoder.matches(password, u.getPassword())) {
      return Result.fail("invalid username or password");
    }

    String token = JwtUtil.generateToken(u.getId());
    Map<String,Object> user = new HashMap<>();
    user.put("id", u.getId());
    user.put("username", u.getUsername());
    user.put("nickname", u.getNickname());
    user.put("role", u.getRole());
    Map<String,Object> data = new HashMap<>();
    data.put("token", token);
    data.put("user", user);
    return Result.ok(data);
  }
}