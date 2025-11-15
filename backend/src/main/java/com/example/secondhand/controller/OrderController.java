package com.example.secondhand.controller;

import com.example.secondhand.common.Result;
import com.example.secondhand.service.OrderService;
import com.example.secondhand.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/order")
public class OrderController {
  @Autowired OrderService orderService;
  @Autowired OrderMapper orderMapper;

  @PostMapping("/create")
  public Result<?> create(@RequestParam Long productId, HttpServletRequest req) {
    Long uid = getUserId(req);
    if (uid == null) return Result.fail("unauthenticated");
    Long oid = orderService.createOrder(productId, uid);
    com.example.secondhand.model.entity.Order order = orderMapper.findById(oid);
    return Result.ok(order);
  }

  @PostMapping("/pay/{id}")
  public Result<?> pay(@PathVariable Long id, HttpServletRequest req) {
    Long uid = getUserId(req);
    if (uid == null) return Result.fail("unauthenticated");
    orderService.confirmPay(id, uid);
    return Result.ok();
  }

  @PostMapping("/received/{id}")
  public Result<?> received(@PathVariable Long id, HttpServletRequest req) {
    Long uid = getUserId(req);
    if (uid == null) return Result.fail("unauthenticated");
    orderService.confirmReceived(id, uid);
    return Result.ok();
  }

  @GetMapping("/buy")
  public Result<?> myBuy(HttpServletRequest req) {
    Long uid = getUserId(req);
    if (uid == null) return Result.fail("unauthenticated");
    return Result.ok(orderService.listMyBuy(uid));
  }

  @GetMapping("/sell")
  public Result<?> mySell(HttpServletRequest req) {
    Long uid = getUserId(req);
    if (uid == null) return Result.fail("unauthenticated");
    return Result.ok(orderService.listMySell(uid));
  }

  private Long getUserId(HttpServletRequest req) {
    Object o = req.getAttribute("userId");
    return o == null ? null : (Long)o;
  }
}