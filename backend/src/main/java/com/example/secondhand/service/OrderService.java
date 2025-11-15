package com.example.secondhand.service;

import com.example.secondhand.model.entity.Order;
import java.util.List;

public interface OrderService {
  Long createOrder(Long productId, Long buyerId);
  void confirmPay(Long orderId, Long userId);
  void confirmReceived(Long orderId, Long userId);
  List<Order> listMyBuy(Long buyerId);
  List<Order> listMySell(Long sellerId);
}