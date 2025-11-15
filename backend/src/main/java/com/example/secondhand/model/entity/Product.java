package com.example.secondhand.model.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Product {
  private Long id;
  private String title;
  private String description;
  private BigDecimal price;
  private Long sellerId;
  private Integer categoryId;
  private String images;
  private String status;
  private Timestamp createdAt;
  private Timestamp updatedAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }
  public Long getSellerId() { return sellerId; }
  public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
  public Integer getCategoryId() { return categoryId; }
  public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
  public String getImages() { return images; }
  public void setImages(String images) { this.images = images; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Timestamp getCreatedAt() { return createdAt; }
  public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
  public Timestamp getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}