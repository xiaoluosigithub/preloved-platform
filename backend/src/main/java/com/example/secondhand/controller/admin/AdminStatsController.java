package com.example.secondhand.controller.admin;

import com.example.secondhand.common.Result;
import com.example.secondhand.mapper.OrderMapper;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.model.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

  @Resource
  private OrderMapper orderMapper;
  @Resource
  private UserMapper userMapper;

  private String checkAdmin(HttpServletRequest request) {
    Object uid = request.getAttribute("userId");
    if (uid == null) return "unauthenticated";
    User u = userMapper.findById(Long.valueOf(uid.toString()));
    if (u == null || u.getRole() == null || !"ADMIN".equalsIgnoreCase(u.getRole())) return "forbidden";
    return null;
  }

  @GetMapping("/orders")
  public Result<java.util.Map<String, Object>> orders(@RequestParam(required = false) String from,
                       @RequestParam(required = false) String to,
                       HttpServletRequest request) {
    String err = checkAdmin(request);
    if (err != null) return Result.fail(err);
    String f = from, t = to;
    if (f == null || t == null) {
      LocalDate end = LocalDate.now();
      LocalDate start = end.minusDays(6);
      f = start.toString();
      t = end.toString();
    }
    List<Map<String, Object>> points = orderMapper.statsDaily(f, t);
    Map<String, Object> data = new HashMap<>();
    data.put("from", f);
    data.put("to", t);
    data.put("points", points);
    return Result.ok(data);
  }
}