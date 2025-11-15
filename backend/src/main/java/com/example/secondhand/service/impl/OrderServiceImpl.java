package com.example.secondhand.service.impl;

import com.example.secondhand.mapper.OrderMapper;
import com.example.secondhand.mapper.ProductMapper;
import com.example.secondhand.model.entity.Order;
import com.example.secondhand.model.entity.Product;
import com.example.secondhand.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
  @Autowired OrderMapper orderMapper;
  @Autowired ProductMapper productMapper;

  @Transactional
  @Override
  public Long createOrder(Long productId, Long buyerId) {
    Product p = productMapper.findById(productId);
    if (p == null) throw new RuntimeException("商品不存在");
    if (p.getSellerId() != null && p.getSellerId().equals(buyerId)) throw new RuntimeException("不能购买自己发布的商品");
    if (!"AVAILABLE".equals(p.getStatus())) throw new RuntimeException("商品不可购买");
    if (orderMapper.countActiveByProduct(productId) > 0) throw new RuntimeException("该商品已有订单进行中");
    Order order = new Order();
    String ts = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
    int rnd = new java.util.Random().nextInt(10000);
    order.setOrderNo(String.format("ORD%s%04d", ts, rnd));
    order.setBuyerId(buyerId);
    order.setSellerId(p.getSellerId());
    order.setProductId(productId);
    order.setPrice(p.getPrice());
    order.setProductTitle(p.getTitle());
    order.setProductPrice(p.getPrice());
    int updated = productMapper.updateStatusIfCurrent(productId, "PENDING", "AVAILABLE");
    if (updated != 1) throw new RuntimeException("商品已被下单或购买");
    orderMapper.insert(order);
    return order.getId();
  }

  @Override
  public void confirmPay(Long orderId, Long userId) {
    Order o = orderMapper.findById(orderId);
    if (o == null) throw new RuntimeException("订单不存在");
    if (!o.getBuyerId().equals(userId)) throw new RuntimeException("无权限支付");
    orderMapper.updateStatus(orderId, "PAID");
    productMapper.updateStatusIfCurrent(o.getProductId(), "SOLD", "PENDING");
  }

  @Transactional
  @Override
  public void confirmReceived(Long orderId, Long userId) {
    Order o = orderMapper.findById(orderId);
    if (o == null) throw new RuntimeException("订单不存在");
    if (!o.getBuyerId().equals(userId)) throw new RuntimeException("无权限确认收货");
    orderMapper.updateStatus(orderId, "RECEIVED");
  }

  @Override
  public List<Order> listMyBuy(Long buyerId) { return orderMapper.findByBuyer(buyerId); }

  @Override
  public List<Order> listMySell(Long sellerId) { return orderMapper.findBySeller(sellerId); }
}