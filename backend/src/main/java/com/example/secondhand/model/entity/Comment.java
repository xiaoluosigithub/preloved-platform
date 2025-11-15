package com.example.secondhand.model.entity;

import java.sql.Timestamp;

public class Comment {
  private Long id;
  private Long productId;
  private Long userId;
  private String content;
  private Timestamp createdAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getProductId() { return productId; }
  public void setProductId(Long productId) { this.productId = productId; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }
  public Timestamp getCreatedAt() { return createdAt; }
  public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}