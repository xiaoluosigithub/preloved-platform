package com.example.secondhand.model.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Order {
  private Long id;
  private String orderNo;
  private Long buyerId;
  private Long sellerId;
  private Long productId;
  private BigDecimal price;
  private String status;
  private String productTitle;
  private BigDecimal productPrice;
  private String productImages;
  private Timestamp createdAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getOrderNo() { return orderNo; }
  public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
  public Long getBuyerId() { return buyerId; }
  public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
  public Long getSellerId() { return sellerId; }
  public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
  public Long getProductId() { return productId; }
  public void setProductId(Long productId) { this.productId = productId; }
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getProductTitle() { return productTitle; }
  public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
  public BigDecimal getProductPrice() { return productPrice; }
  public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }
  public String getProductImages() { return productImages; }
  public void setProductImages(String productImages) { this.productImages = productImages; }
  public Timestamp getCreatedAt() { return createdAt; }
  public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}