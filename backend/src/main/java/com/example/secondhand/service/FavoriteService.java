package com.example.secondhand.service;

public interface FavoriteService {
  boolean toggleFavorite(Long userId, Long productId);
  int countByProduct(Long productId);
  boolean exists(Long userId, Long productId);
}