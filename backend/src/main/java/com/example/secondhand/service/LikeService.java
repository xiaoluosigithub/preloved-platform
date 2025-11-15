package com.example.secondhand.service;

public interface LikeService {
  boolean toggleLike(Long userId, Long productId);
  int countByProduct(Long productId);
  boolean exists(Long userId, Long productId);
}